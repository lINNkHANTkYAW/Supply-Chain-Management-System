package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.DistriOrder;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.Invoice;
import com.example.SupplyChainManagement.model.InvoiceItem;
import com.example.SupplyChainManagement.model.ManuOrder;
import com.example.SupplyChainManagement.model.ManuOrderItem;
import com.example.SupplyChainManagement.model.ManuRawMaterial;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.ProductSales;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.repository.CusOrderItemRepository;
import com.example.SupplyChainManagement.repository.CusOrderRepository;
import com.example.SupplyChainManagement.repository.CustomerRepository;
import com.example.SupplyChainManagement.repository.ManuOrderItemRepository;
import com.example.SupplyChainManagement.repository.ManuOrderRepository;
import com.example.SupplyChainManagement.repository.ManuRawMaterialRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.repository.ProductSalesRepository;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ManuOrderService {

	private final ManuOrderRepository manuOrderRepository;
	private final ManuOrderItemRepository manuOrderItemRepository;
	private final ManufacturerRepository manuRepository;
	private final ProductSalesRepository productSalesRepository;
	private final ManuRawMaterialRepository manuRawMaterialRepository;

	public ManuOrderService(ManuOrderRepository manuOrderRepository, ManuOrderItemRepository manuOrderItemRepository,
			ManufacturerRepository manuRepository, ProductSalesRepository productSalesRepository,
			ManuRawMaterialRepository manuRawMaterialRepository) {
		this.manuOrderRepository = manuOrderRepository;
		this.manuOrderItemRepository = manuOrderItemRepository;
		this.manuRepository = manuRepository;
		this.productSalesRepository = productSalesRepository;
		this.manuRawMaterialRepository = manuRawMaterialRepository;
	}

	public ManuOrder createOrder(ManuOrder order) {
		return manuOrderRepository.save(order);
	}

	public List<ManuOrder> getOrdersForSupplier(Long supplierId) {
		return manuOrderRepository.findBySupplierId(supplierId);
	}

	// Get order by ID
	public Optional<ManuOrder> getOrderById(Long orderId) {
		return manuOrderRepository.findById(orderId);
	}

	public BigDecimal calculateTotalForOrder(Long orderId) {
	    List<ManuOrderItem> items = getOrderItems(orderId);
	    return items.stream().map(item -> {
	        BigDecimal unitPrice = item.getSupplierRawMaterial().getUnitPrice();
	        if (unitPrice == null) {
	            unitPrice = BigDecimal.ZERO; // Default to zero if unitPrice is null
	        }
	        return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
	    }).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public List<ManuOrderItem> getOrderItems(Long orderId) {
		return manuOrderItemRepository.findByManuOrder_OrderId(orderId);
	}

	/*
	 * @Transactional public void updateOrderStatus(Long orderId, String
	 * deliverStatus, String transactionStatus) { ManuOrder order =
	 * manuOrderRepository.findById(orderId) .orElseThrow(() -> new
	 * RuntimeException("Order not found")); order.setDeliverStatus(deliverStatus);
	 * order.setTransactionStatus(transactionStatus);
	 * manuOrderRepository.save(order); }
	 */

	@Transactional
	public void updateOrderStatus(Long orderId, String deliverStatus, String transactionStatus) {
		ManuOrder order = manuOrderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));

		// Update deliver and transaction status
		order.setDeliverStatus(deliverStatus);
		order.setTransactionStatus(transactionStatus);

		// Check if both statuses are "Delivered" and "Paid"
		if ("Delivered".equalsIgnoreCase(deliverStatus) && "Paid".equalsIgnoreCase(transactionStatus)) {
			order.setStatus("Completed");
			populateProductSales(order); // Populate ProductSales for completed orders
			saveOrderDataAsRawMaterial(order);
		}

		manuOrderRepository.save(order);
	}
	
	private void saveOrderDataAsRawMaterial(ManuOrder order) {
        // Fetch the invoice associated with the order
        Invoice invoice = order.getInvoice();

        for (ManuOrderItem orderItem : order.getOrderItems()) {
            // Fetch the SupplierRawMaterial associated with the order item
            SupplierRawMaterial supplierRawMaterial = orderItem.getSupplierRawMaterial();

            // Find the corresponding InvoiceItem for this order item
            InvoiceItem invoiceItem = invoice.getInvoiceItems().stream()
                    .filter(item -> item.getRawMaterial().getRawMaterialSid().equals(supplierRawMaterial.getRawMaterialSid()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("InvoiceItem not found for the raw material"));

            // Create a new ManuRawMaterial entity
            ManuRawMaterial manuRawMaterial = new ManuRawMaterial();
            manuRawMaterial.setManufacturer(order.getManufacturer());
            manuRawMaterial.setName(supplierRawMaterial.getName());
            manuRawMaterial.setQtyOnHand(orderItem.getQuantity()); // Set the quantity from the order item
            manuRawMaterial.setUnitPrice(invoiceItem.getUnitPrice()); // Set unitPrice from InvoiceItem
            manuRawMaterial.setUnitCost(invoiceItem.getUnitPrice()); // Assuming unitCost is the same as unitPrice
            manuRawMaterial.setCategory(supplierRawMaterial.getCategory());
            manuRawMaterial.setImage(supplierRawMaterial.getImage());
            manuRawMaterial.setDescription(supplierRawMaterial.getDescription());
            manuRawMaterial.setAddedDate(order.getDeliverDate());

            // Save the ManuRawMaterial entity
            manuRawMaterialRepository.save(manuRawMaterial);
        }
    }
	
	/* private void saveOrderToRawMaterials(ManuOrder order) {
        for (ManuOrderItem item : order.getOrderItems()) {
            // Check if raw material already exists for this manufacturer and supplier raw material
            ManuRawMaterial existingRawMaterial = manuRawMaterialRepository
                    .findByManufacturerAndName(order.getManufacturer(), item.getSupplierRawMaterial().getName())
                    .orElse(null);

            if (existingRawMaterial != null) {
                // Update existing raw material quantity
                existingRawMaterial.setQtyOnHand(existingRawMaterial.getQtyOnHand() + item.getQuantity());
                manuRawMaterialRepository.save(existingRawMaterial);
            } else {
                // Create new ManuRawMaterial entry
                ManuRawMaterial rawMaterial = new ManuRawMaterial();
                rawMaterial.setManufacturer(order.getManufacturer());
                rawMaterial.setName(item.getSupplierRawMaterial().getName());
                rawMaterial.setQtyOnHand(item.getQuantity());
                rawMaterial.setUnitPrice(item.getSupplierRawMaterial().getUnitPrice()); // Assuming SupplierRawMaterial has this
                rawMaterial.setUnitCost(item.getSupplierRawMaterial().getUnitCost());   // Assuming SupplierRawMaterial has this
                rawMaterial.setCategory(item.getSupplierRawMaterial().getCategory());   // Assuming SupplierRawMaterial has this
                rawMaterial.setDescription(item.getSupplierRawMaterial().getDescription()); // Optional
                rawMaterial.setImage(item.getSupplierRawMaterial().getImage());         // Optional

                manuRawMaterialRepository.save(rawMaterial);
            }
        }
    } */
	
	

	private void populateProductSales(ManuOrder order) {
		// Calculate total revenue and quantity sold for the order
		BigDecimal totalRevenue = calculateTotalForOrder(order.getOrderId());
		int totalQuantitySold = order.getOrderItems().stream().mapToInt(ManuOrderItem::getQuantity).sum();

		// Create a new ProductSales entry
		ProductSales productSales = new ProductSales();
		productSales.setSupplier(order.getSupplier()); // Set the supplier
		productSales.setQuantitySold(totalQuantitySold); // Set total quantity sold
		productSales.setRevenue(totalRevenue.doubleValue()); // Set total revenue
		productSales.setCustomerSatisfaction(0.0); // Default value, can be updated later
		productSales.setSaleDate(LocalDateTime.now()); // Set the sale date to now

		// Save the ProductSales entry
		productSalesRepository.save(productSales);
	}
	
	public List<ManuOrder> getCompletedOrders(Long manufacturerId) {
        return manuOrderRepository.findByManufacturer_ManufacturerIdAndStatus(manufacturerId, "Completed");
    }

    public ManuOrder updateOrder(Long orderId, ManuOrder updatedOrder) {
        ManuOrder order = manuOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.getOrderItems().get(0).setQuantity(updatedOrder.getOrderItems().get(0).getQuantity());
        return manuOrderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        manuOrderRepository.deleteById(orderId);
    }
    
    
    // ADDED
    public List<ManuOrder> getManufacturerOrders(Long userId) {
		Optional<Manufacturer> manu = manuRepository.findByUser_UserId(userId);
		if (manu.isEmpty()) {
			throw new RuntimeException("Distributor not found for user ID: " + userId);
		}
		return manuOrderRepository.findByManufacturer_ManufacturerId(manu.get().getManufacturerId());
	}
  

}
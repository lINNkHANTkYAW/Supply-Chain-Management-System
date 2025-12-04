package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.DistriInventory;
import com.example.SupplyChainManagement.model.DistriOrder;
import com.example.SupplyChainManagement.model.DistriOrderItem;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.ManuOrder;
import com.example.SupplyChainManagement.model.ManuProductSales;
import com.example.SupplyChainManagement.repository.CusOrderItemRepository;
import com.example.SupplyChainManagement.repository.CusOrderRepository;
import com.example.SupplyChainManagement.repository.CustomerRepository;
import com.example.SupplyChainManagement.repository.DistriInventoryRepository;
import com.example.SupplyChainManagement.repository.DistriOrderItemRepository;
import com.example.SupplyChainManagement.repository.DistriOrderRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.repository.ManuProductSalesRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DistriOrderService {

	private final DistriOrderRepository distriOrderRepository;
	private final DistriOrderItemRepository distriOrderItemRepository;
	private final DistributorRepository distributorRepository;
	private final ManuProductSalesRepository manuProductSalesRepository;
	private final DistriInventoryRepository distriInventoryRepository;

	public DistriOrderService(DistriOrderRepository distriOrderRepository,
			DistriOrderItemRepository distriOrderItemRepository, DistributorRepository distributorRepository,
			ManuProductSalesRepository manuProductSalesRepository, DistriInventoryRepository distriInventoryRepository) {
		this.distriOrderRepository = distriOrderRepository;
		this.distriOrderItemRepository = distriOrderItemRepository;
		this.distributorRepository = distributorRepository;
		this.manuProductSalesRepository = manuProductSalesRepository;
		this.distriInventoryRepository =  distriInventoryRepository;
	}

	public List<DistriOrder> getOrderHistory(Long distributorUserId) {
		return distriOrderRepository.findByDistributorUserId(distributorUserId);
	}
	
	public DistriOrderItem saveDistriOrderItem(DistriOrderItem item) {
        return distriOrderItemRepository.save(item);
    }
	
	 public DistriOrderItem getDistriOrderItemById(Long itemId) {
	        return distriOrderItemRepository.findById(itemId)
	                .orElseThrow(() -> new RuntimeException("DistriOrderItem not found"));
	    }
	 
	 public DistriOrderItem updateDistriOrderItem(Long itemId, DistriOrderItem updatedItem) {
	        DistriOrderItem item = distriOrderItemRepository.findById(itemId)
	                .orElseThrow(() -> new RuntimeException("DistriOrderItem not found"));
	        item.setQuantity(updatedItem.getQuantity());
	        return distriOrderItemRepository.save(item);
	    }
	/* public List<DistriOrderItem> getDeliveredItemsByDistributor(Long distributorId) {
        return distriOrderItemRepository.findByDistriOrder_Distributor_DistributorId(distributorId);
    } */
	
	public List<DistriOrderItem> getDeliveredItemsByDistributor(Long distributorId) {
        return distriOrderItemRepository.findByDistriOrder_Distributor_DistributorIdAndDistriOrder_Status(distributorId, "Completed");}

	public List<DistriOrderItem> getOrderItems(Long orderId) {
		return distriOrderRepository.findById(orderId).map(DistriOrder::getOrderItems).orElse(Collections.emptyList());
	}

	public List<DistriOrder> getDistributorOrders(Long userId) {
		Optional<Distributor> distributor = distributorRepository.findByUser_UserId(userId);
		if (distributor.isEmpty()) {
			throw new RuntimeException("Distributor not found for user ID: " + userId);
		}
		return distriOrderRepository.findByDistributor_DistributorId(distributor.get().getDistributorId());
	}

	public BigDecimal calculateTotalForOrder(Long orderId) {
		List<DistriOrderItem> items = getOrderItems(orderId);
		return items.stream()
				.map(item -> item.getManuProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
	} 
	
	public Optional<DistriOrder> getOrderById(Long orderId) {
        return distriOrderRepository.findByOrderId(orderId);
    }
	
	public List<DistriOrder> getOrdersForManufacturer(Long manufacturerId) {
        return distriOrderRepository.findByManufacturerId(manufacturerId);
    }
	
	@Transactional
    public void updateOrderStatus(Long orderId, String deliverStatus, String transactionStatus) {
        DistriOrder order = distriOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // Update deliver and transaction status
        order.setDeliverStatus(deliverStatus);
        order.setTransactionStatus(transactionStatus);

        // Check if the order is completed
        if ("Delivered".equalsIgnoreCase(deliverStatus) && "Paid".equalsIgnoreCase(transactionStatus)) {
            order.setStatus("Completed");

            // Populate ManuProductSales
            ManuProductSales sales = new ManuProductSales();
            sales.setManufacturer(order.getManufacturer());
            sales.setQuantitySold(order.getOrderItems().stream().mapToInt(DistriOrderItem::getQuantity).sum());
            sales.setRevenue(order.getOrderItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getManuProduct().getPrice().doubleValue())
                .sum());
            sales.setCustomerSatisfaction(0.0); // Default value, can be updated later
            sales.setSaleDate(LocalDateTime.now());

            manuProductSalesRepository.save(sales);
            
            List<DistriInventory> inventoryItems = order.getOrderItems().stream()
                    .map(item -> {
                        DistriInventory inventory = new DistriInventory();
                        inventory.setName(item.getManuProduct().getName());
                        inventory.setQuantity(item.getQuantity());
                        inventory.setCost(item.getManuProduct().getCost()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
                        inventory.setCostPerUnit(item.getManuProduct().getCost());
                        inventory.setAddedDate(order.getDeliverDate() != null ? order.getDeliverDate() : LocalDate.now());
                        inventory.setDistributorId(order.getDistributor().getDistributorId());
                        inventory.setCategory(item.getManuProduct().getCategory());
                        return inventory;
                    })
                    .collect(Collectors.toList());

                distriInventoryRepository.saveAll(inventoryItems);
        }

        distriOrderRepository.save(order);
    }
	
	// Get monthly sales data
    /* public List<Object[]> getMonthlySales(Long manufacturerId) {
        return distriOrderRepository.findMonthlySales(manufacturerId);
    }

    // Get order completion rate
    public Map<String, Integer> getOrderCompletionRate(Long manufacturerId) {
        List<Object[]> results = distriOrderRepository.getOrderCompletionRate(manufacturerId);
        Map<String, Integer> completionRate = new HashMap<>();
        for (Object[] result : results) {
            String status = (String) result[0];
            Integer count = ((Number) result[1]).intValue();
            completionRate.put(status, count);
        }
        return completionRate;
    } */
    
 // Get manufacturer stats (products sold, net profit, customer satisfaction)
    /* public Map<String, Object> getManufacturerStats(Long manufacturerId) {
        Map<String, Object> stats = new HashMap<>();

        // Total products sold
        int productsSold = manuProductSalesRepository.getTotalProductsSold(manufacturerId);
        stats.put("productsSold", productsSold);

        // Net profit
        double netProfit = manuProductSalesRepository.getTotalRevenue(manufacturerId);
        stats.put("netProfit", netProfit);

        // Customer satisfaction
        double customerSatisfaction = manuProductSalesRepository.getCustomerSatisfaction(manufacturerId);
        stats.put("customerSatisfaction", customerSatisfaction);

        return stats;
    } */
	
	// Updated to fetch monthly sales for completed orders only
    public List<Object[]> getMonthlySales(Long manufacturerId) {
        return distriOrderRepository.findMonthlySalesForCompletedOrders(manufacturerId);
    }

    // Updated to fetch completion rate for the manufacturer
    public Map<String, Integer> getOrderCompletionRate(Long manufacturerId) {
        List<Object[]> results = distriOrderRepository.getOrderCompletionRate(manufacturerId);
        Map<String, Integer> completionRate = new HashMap<>();
        completionRate.put("Completed", 0); // Default to 0 if no data
        completionRate.put("Pending", 0);   // Default to 0 if no data
        for (Object[] result : results) {
            String status = (String) result[0];
            Integer count = ((Number) result[1]).intValue();
            completionRate.put(status, count);
        }
        return completionRate;
    }

    public Map<String, Object> getManufacturerStats(Long manufacturerId) {
        Map<String, Object> stats = new HashMap<>();
        int productsSold = manuProductSalesRepository.getTotalProductsSold(manufacturerId);
        stats.put("productsSold", productsSold);
        double netProfit = manuProductSalesRepository.getTotalRevenue(manufacturerId);
        stats.put("netProfit", netProfit);
        int pendingOrders = distriOrderRepository.countByStatusAndManufacturerId("Pending", manufacturerId);
        stats.put("pendingOrders", pendingOrders);
        return stats;
    }
}
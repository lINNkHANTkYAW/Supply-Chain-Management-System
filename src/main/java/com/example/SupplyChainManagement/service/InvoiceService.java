package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.dto.InvoiceItemRequest;
import com.example.SupplyChainManagement.dto.InvoiceRequest;
import com.example.SupplyChainManagement.dto.InvoiceResponse;
import com.example.SupplyChainManagement.model.*;
import com.example.SupplyChainManagement.repository.*;
import com.example.SupplyChainManagement.utils.InvoiceImageGenerator;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private SupplierMaterialRepository supplierMaterialRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;
    
    @Autowired
    private ManuOrderRepository manuOrderRepository;

    @Autowired
    private ManuOrderItemRepository manuOrderItemRepository;

    @Transactional
    public Invoice createInvoice(InvoiceRequest invoiceRequest) {
        // Validate totalAmount
        if (invoiceRequest.getTotalAmount() == null) {
            throw new IllegalArgumentException("Total amount must be provided in the request.");
        }

        // Fetch the seller and buyer from the database
        User seller = userRepository.findById(invoiceRequest.getSellerId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        User buyer = userRepository.findById(invoiceRequest.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // Fetch role-specific details for seller and buyer
        Supplier sellerSupplier = supplierRepository.findByUser_UserId(seller.getUserId())
                .orElseThrow(() -> new RuntimeException("Seller supplier details not found"));
        Manufacturer buyerManufacturer = manufacturerRepository.findByUser_UserId(buyer.getUserId())
                .orElseThrow(() -> new RuntimeException("Buyer manufacturer details not found"));

        // Fetch the payment method from the database
        PaymentMethod paymentMethod = paymentMethodRepository.findById(invoiceRequest.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        // Create the Invoice entity
        Invoice invoice = new Invoice();
        invoice.setSeller(seller);
        invoice.setBuyer(buyer);
        invoice.setOrderDate(invoiceRequest.getOrderDate()); // LocalDate
        invoice.setDeliverDate(invoiceRequest.getDeliverDate()); // LocalDate
        invoice.setPaymentMethod(paymentMethod);
        invoice.setTotalAmount(invoiceRequest.getTotalAmount());

        // Create InvoiceItems
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (InvoiceItemRequest itemRequest : invoiceRequest.getMaterials()) {
            SupplierRawMaterial rawMaterial = supplierMaterialRepository.findByRawMaterialSid(itemRequest.getRawMaterialId())
                    .orElseThrow(() -> new RuntimeException("Raw material not found"));

            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setInvoice(invoice);
            invoiceItem.setRawMaterial(rawMaterial);
            invoiceItem.setQuantity(itemRequest.getQuantity());
            invoiceItem.setUnitPrice(itemRequest.getUnitPrice());

            invoiceItems.add(invoiceItem);
        }

        invoice.setInvoiceItems(invoiceItems);

        // Save the invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);
        invoiceItemRepository.saveAll(invoiceItems);

        // Create and save ManuOrder
        ManuOrder manuOrder = new ManuOrder();
        manuOrder.setManufacturer(buyerManufacturer);
        manuOrder.setSupplier(sellerSupplier);
        manuOrder.setInvoice(savedInvoice); // Link the saved Invoice to the ManuOrder
        LocalDate orderDate = invoiceRequest.getOrderDate() != null ? invoiceRequest.getOrderDate() : LocalDate.now();
        LocalDate deliverDate = invoiceRequest.getDeliverDate() != null ? invoiceRequest.getDeliverDate() : LocalDate.now().plusDays(7);
        manuOrder.setOrderDate(orderDate);
        manuOrder.setDeliverDate(deliverDate);
        manuOrder.setStatus("Pending"); // Set initial status
        manuOrder.setDeliverStatus("Not Delivered");
        manuOrder.setTransactionStatus("Not Paid");
        manuOrder.setDescription("Order created from invoice");
        manuOrder.setPaymentMethod(paymentMethod);

        // Save ManuOrder
        ManuOrder savedManuOrder = manuOrderRepository.save(manuOrder);

        // Create and save ManuOrderItems
        List<ManuOrderItem> manuOrderItems = new ArrayList<>();
        for (InvoiceItemRequest itemRequest : invoiceRequest.getMaterials()) {
            SupplierRawMaterial rawMaterial = supplierMaterialRepository.findByRawMaterialSid(itemRequest.getRawMaterialId())
                    .orElseThrow(() -> new RuntimeException("Raw material not found"));

            ManuOrderItem manuOrderItem = new ManuOrderItem();
            manuOrderItem.setManuOrder(savedManuOrder);
            manuOrderItem.setSupplierRawMaterial(rawMaterial);
            manuOrderItem.setQuantity(itemRequest.getQuantity());

            manuOrderItems.add(manuOrderItem);
        }

        // Save ManuOrderItems
        manuOrderItemRepository.saveAll(manuOrderItems);

        return savedInvoice;
    }

    public InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setInvoiceId(invoice.getInvoiceId());
        response.setSellerId(invoice.getSeller().getUserId());
        response.setBuyerId(invoice.getBuyer().getUserId());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus());
        response.setOrderDate(invoice.getOrderDate());
        response.setDeliverDate(invoice.getDeliverDate());
        response.setPaymentMethodId(invoice.getPaymentMethod().getPayMethodId());

        // Generate and set invoice image
        String imageUrl = InvoiceImageGenerator.generateInvoiceImage(invoice);
        response.setImageUrl(imageUrl);

        return response;
    }
    
    @PostConstruct
    public void init() {
        InvoiceImageGenerator.setRepositories(supplierRepository, manufacturerRepository);
    }
}
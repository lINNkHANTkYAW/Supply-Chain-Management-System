package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.dto.InvoiceItemRequest;
import com.example.SupplyChainManagement.dto.InvoiceRequest;
import com.example.SupplyChainManagement.dto.InvoiceResponse;
import com.example.SupplyChainManagement.dto.ManuInvoiceItemRequest;
import com.example.SupplyChainManagement.dto.ManuInvoiceRequest;
import com.example.SupplyChainManagement.model.*;
import com.example.SupplyChainManagement.repository.*;
import com.example.SupplyChainManagement.utils.InvoiceImageGenerator;
import com.example.SupplyChainManagement.utils.ManuInvoiceImageGenerator;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManuInvoiceService {

    @Autowired
    private ManuInvoiceRepository manuInvoiceRepository;

    @Autowired
    private ManuInvoiceItemRepository invoiceItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ManuProductRepository manuProductRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private DistributorRepository distributorRepository;
    
    @Autowired
    private DistriProductRepository distriProductRepository;
    
    @Autowired
    private DistriOrderRepository distriOrderRepository;

    @Autowired
    private DistriOrderItemRepository distriOrderItemRepository;

    @Transactional
    public ManuInvoice createInvoice(ManuInvoiceRequest invoiceRequest) {
    	System.out.println("Order Date from Request: " + invoiceRequest.getOrderDate());
        System.out.println("Deliver Date from Request: " + invoiceRequest.getDeliverDate());
        
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
        Manufacturer sellerManufacturer = manufacturerRepository.findByUser_UserId(seller.getUserId())
                .orElseThrow(() -> new RuntimeException("Seller manufacturer details not found"));
        Distributor buyerDistributor = distributorRepository.findByUser_UserId(buyer.getUserId())
                .orElseThrow(() -> new RuntimeException("Buyer distributor details not found"));

        // Fetch the payment method from the database
        PaymentMethod paymentMethod = paymentMethodRepository.findById(invoiceRequest.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        // Create the Invoice entity
        ManuInvoice invoice = new ManuInvoice();
        invoice.setSeller(seller);
        invoice.setBuyer(buyer);
        invoice.setOrderDate(invoiceRequest.getOrderDate());
        invoice.setDeliverDate(invoiceRequest.getDeliverDate());
        invoice.setPaymentMethod(paymentMethod);
        invoice.setTotalAmount(invoiceRequest.getTotalAmount());

        // Calculate total amount
        // BigDecimal totalAmount = BigDecimal.ZERO;
        List<ManuInvoiceItem> invoiceItems = new ArrayList<>();

        for (ManuInvoiceItemRequest itemRequest : invoiceRequest.getProducts()) {
            ManuProduct manuProduct = manuProductRepository.findByProductMid(itemRequest.getManuProductId())
                    .orElseThrow(() -> new RuntimeException("Manu Product not found"));

            ManuInvoiceItem invoiceItem = new ManuInvoiceItem();
            invoiceItem.setManuInvoice(invoice);
            invoiceItem.setManuProduct(manuProduct);
            invoiceItem.setQuantity(itemRequest.getQuantity());
            invoiceItem.setUnitPrice(itemRequest.getUnitPrice());

            // totalAmount = totalAmount.add(itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            invoiceItems.add(invoiceItem);
        }

        // invoice.setTotalAmount(totalAmount);
        invoice.setInvoiceItems(invoiceItems);

        // Save the invoice initially without the image URL
        ManuInvoice savedInvoice = manuInvoiceRepository.save(invoice);
        invoiceItemRepository.saveAll(invoiceItems);

        // Generate the invoice image URL (now that manuInvoiceId is available)
        String imageUrl;
        try {
            imageUrl = ManuInvoiceImageGenerator.generateInvoiceImage(savedInvoice);
            if (imageUrl == null) {
                throw new RuntimeException("ManuInvoiceImageGenerator returned null for invoice ID " + savedInvoice.getManuInvoiceId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice image for invoice ID " + savedInvoice.getManuInvoiceId(), e);
        }

        // Set the invoiceImageUrl on the entity and save again
        savedInvoice.setInvoiceImageUrl(imageUrl);
        savedInvoice = manuInvoiceRepository.save(savedInvoice);

        // Create and save DistriOrder
        DistriOrder distriOrder = new DistriOrder();
        distriOrder.setDistributor(buyerDistributor);
        distriOrder.setManufacturer(sellerManufacturer);
        LocalDate orderDate = invoiceRequest.getOrderDate() != null ? invoiceRequest.getOrderDate() : LocalDate.now();
        LocalDate deliverDate = invoiceRequest.getDeliverDate() != null ? invoiceRequest.getDeliverDate() : LocalDate.now().plusDays(7);
        distriOrder.setOrderDate(orderDate);
        distriOrder.setDeliverDate(deliverDate);
        distriOrder.setStatus("Pending");
        distriOrder.setDeliverStatus("Not Delivered");
        distriOrder.setTransactionStatus("Not Paid");
        distriOrder.setDescription("Order created from invoice");
        distriOrder.setPaymentMethod(paymentMethod);

        DistriOrder savedDistriOrder = distriOrderRepository.save(distriOrder);

        // Create and save DistriOrderItems
        List<DistriOrderItem> distriOrderItems = new ArrayList<>();
        for (ManuInvoiceItemRequest itemRequest : invoiceRequest.getProducts()) {
            ManuProduct manuProduct = manuProductRepository.findByProductMid(itemRequest.getManuProductId())
                    .orElseThrow(() -> new RuntimeException("Manu Product not found"));

            DistriOrderItem distriOrderItem = new DistriOrderItem();
            distriOrderItem.setDistriOrder(savedDistriOrder);
            distriOrderItem.setManuProduct(manuProduct);
            distriOrderItem.setQuantity(itemRequest.getQuantity());

            distriOrderItems.add(distriOrderItem);
        }

        distriOrderItemRepository.saveAll(distriOrderItems);

        return savedInvoice;
    }

    public InvoiceResponse mapToInvoiceResponse(ManuInvoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setInvoiceId(invoice.getManuInvoiceId());
        response.setSellerId(invoice.getSeller().getUserId());
        response.setBuyerId(invoice.getBuyer().getUserId());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus());
        response.setOrderDate(invoice.getOrderDate());
        response.setDeliverDate(invoice.getDeliverDate());
        response.setPaymentMethodId(invoice.getPaymentMethod().getPayMethodId());

        // Use the saved invoiceImageUrl
        response.setImageUrl(invoice.getInvoiceImageUrl());

        return response;
    }
    
    @PostConstruct
    public void init() {
    	ManuInvoiceImageGenerator.setRepositories(manufacturerRepository, distributorRepository);
    }
}
package com.example.SupplyChainManagement.utils;

import com.example.SupplyChainManagement.model.Invoice;
import com.example.SupplyChainManagement.model.InvoiceItem;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

public class InvoiceImageGenerator {

    private static SupplierRepository supplierRepository;
    private static ManufacturerRepository manufacturerRepository;

    public static void setRepositories(SupplierRepository supplierRepo, ManufacturerRepository manufacturerRepo) {
        supplierRepository = supplierRepo;
        manufacturerRepository = manufacturerRepo;
    }

    public static String generateInvoiceImage(Invoice invoice) {
        try {
            int width = 800;
            int height = 800; // Increased height to accommodate more content
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Enable anti-aliasing for smoother text and lines
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background gradient for a modern look
            GradientPaint gradient = new GradientPaint(0, 0, new Color(245, 245, 245), 0, height, Color.WHITE);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);

         // Header section (colored bar)
            g2d.setColor(Color.BLACK); // Changed to black
            g2d.fillRect(0, 0, width, 80);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("INVOICE #" + invoice.getInvoiceId(), 20, 50);

            // Seller and Buyer info sections
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            int y = 100;

            // Seller Info
            Supplier sellerSupplier = supplierRepository.findByUser_UserId(invoice.getSeller().getUserId())
                    .orElseThrow(() -> new RuntimeException("Seller supplier details not found"));
            g2d.drawString("From:", 20, y);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            y += 20;
            g2d.drawString(sellerSupplier.getCompanyName(), 20, y);
            y += 20;
            g2d.drawString(sellerSupplier.getContactInfo(), 20, y);
            y += 20;
            g2d.drawString(sellerSupplier.getAddress(), 20, y);

            // Buyer Info (right-aligned)
            Manufacturer buyerManufacturer = manufacturerRepository.findByUser_UserId(invoice.getBuyer().getUserId())
                    .orElseThrow(() -> new RuntimeException("Buyer manufacturer details not found"));
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            y = 100;
            g2d.drawString("To:", 400, y);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            y += 20;
            g2d.drawString(buyerManufacturer.getCompanyName(), 400, y);
            y += 20;
            g2d.drawString(buyerManufacturer.getContactInfo(), 400, y);
            y += 20;
            g2d.drawString(buyerManufacturer.getAddress(), 400, y);

            // Dates and Payment Method
            y = 200;
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("Order Date: " + invoice.getOrderDate(), 20, y);
            y += 20;
            g2d.drawString("Delivery Date: " + invoice.getDeliverDate(), 20, y);
            y += 20;
            g2d.drawString("Payment Method: " + invoice.getPaymentMethod().getPayMethodName(), 20, y);

            // Items Table
            y += 30;
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Order Items", 20, y);
            y += 20;

            // Table header
            g2d.setColor(new Color(230, 230, 230));
            g2d.fillRect(20, y, 760, 25);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Item", 30, y + 18);
            g2d.drawString("Quantity", 400, y + 18);
            g2d.drawString("Unit Price", 500, y + 18);
            g2d.drawString("Total", 650, y + 18);
            y += 25;

            // Table rows
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            for (InvoiceItem item : invoice.getInvoiceItems()) {
            	BigDecimal quantity = new BigDecimal(item.getQuantity());
            	BigDecimal unitPrice = item.getUnitPrice(); // Ensure this returns BigDecimal
            	BigDecimal total = quantity.multiply(unitPrice);
                g2d.drawString(item.getRawMaterial().getName(), 30, y + 18);
                g2d.drawString(String.valueOf(item.getQuantity()), 400, y + 18);
                g2d.drawString("MMK " + item.getUnitPrice(), 500, y + 18);
                g2d.drawString("MMK " + total.toString(), 650, y + 18);
                y += 25;
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(20, y, 780, y); // Row separator
                g2d.setColor(Color.BLACK);
            }

            // Total Amount
            y += 20;
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Total Amount: MMK " + String.format("%.2f", invoice.getTotalAmount()), 500, y);

            // Border
            g2d.setColor(Color.GRAY);
            g2d.drawRect(10, 10, width - 20, height - 20);

            // Save image
            String folderPath = "./invoices/";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filePath = folderPath + "invoice_" + invoice.getInvoiceId() + ".png";
            File file = new File(filePath);
            ImageIO.write(image, "png", file);

            g2d.dispose();
            return "/invoices/invoice_" + invoice.getInvoiceId() + ".png";

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
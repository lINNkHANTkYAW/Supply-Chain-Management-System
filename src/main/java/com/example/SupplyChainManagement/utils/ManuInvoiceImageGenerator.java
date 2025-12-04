package com.example.SupplyChainManagement.utils;

import com.example.SupplyChainManagement.model.ManuInvoice;
import com.example.SupplyChainManagement.model.ManuInvoiceItem;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

public class ManuInvoiceImageGenerator {

    private static ManufacturerRepository manufacturerRepository;
    private static DistributorRepository distributorRepository;

    public static void setRepositories(ManufacturerRepository manufacturerRepo, DistributorRepository distributorRepo) {
        manufacturerRepository = manufacturerRepo;
        distributorRepository = distributorRepo;
    }

    public static String generateInvoiceImage(ManuInvoice invoice) {
        try {
            if (invoice.getManuInvoiceId() == null) {
                throw new IllegalArgumentException("ManuInvoice ID cannot be null when generating invoice image");
            }

            int width = 800;
            int height = 800;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // Enable anti-aliasing for smoother text and lines
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background gradient
            GradientPaint gradient = new GradientPaint(0, 0, new Color(245, 245, 245), 0, height, Color.WHITE);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);

            // Header section
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, 80);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("INVOICE #" + invoice.getManuInvoiceId(), 20, 50);

            // Seller and Buyer info sections
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            int y = 100;

            // Seller Info
            Manufacturer sellerManufacturer = manufacturerRepository.findByUser_UserId(invoice.getSeller().getUserId())
                    .orElseThrow(() -> new RuntimeException("Seller manufacturer details not found"));
            g2d.drawString("From:", 20, y);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            y += 20;
            g2d.drawString(sellerManufacturer.getCompanyName(), 20, y);
            y += 20;
            g2d.drawString(sellerManufacturer.getContactInfo(), 20, y);
            y += 20;
            g2d.drawString(sellerManufacturer.getAddress(), 20, y);

            // Buyer Info (right-aligned)
            Distributor buyerDistributor = distributorRepository.findByUser_UserId(invoice.getBuyer().getUserId())
                    .orElseThrow(() -> new RuntimeException("Buyer distributor details not found"));
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            y = 100;
            g2d.drawString("To:", 400, y);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            y += 20;
            g2d.drawString(buyerDistributor.getCompanyName(), 400, y);
            y += 20;
            g2d.drawString(buyerDistributor.getContactInfo(), 400, y);
            y += 20;
            g2d.drawString(buyerDistributor.getAddress(), 400, y);

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
            for (ManuInvoiceItem item : invoice.getInvoiceItems()) {
                BigDecimal quantity = new BigDecimal(item.getQuantity());
                BigDecimal unitPrice = item.getUnitPrice();
                BigDecimal total = quantity.multiply(unitPrice);
                g2d.drawString(item.getManuProduct().getName(), 30, y + 18);
                g2d.drawString(String.valueOf(item.getQuantity()), 400, y + 18);
                g2d.drawString("MMK " + item.getUnitPrice(), 500, y + 18);
                g2d.drawString("MMK " + total.toString(), 650, y + 18);
                y += 25;
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(20, y, 780, y);
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
                boolean created = folder.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create directory: " + folderPath);
                }
            }

            String filePath = folderPath + "manu_invoice_" + invoice.getManuInvoiceId() + ".png";
            File file = new File(filePath);
            ImageIO.write(image, "png", file);

            if (!file.exists()) {
                throw new RuntimeException("Manu invoice image file was not created at: " + filePath);
            }

            g2d.dispose();
            return "/invoices/manu_invoice_" + invoice.getManuInvoiceId() + ".png";

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate or save manu invoice image for invoice ID " + (invoice.getManuInvoiceId() != null ? invoice.getManuInvoiceId() : "null"), e);
        }
    }
}
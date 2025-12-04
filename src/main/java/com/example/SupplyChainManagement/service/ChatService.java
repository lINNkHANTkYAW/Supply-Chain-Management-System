package com.example.SupplyChainManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.SupplyChainManagement.dto.ChatMessageDTO;
import com.example.SupplyChainManagement.dto.InvoiceRequest;

import com.example.SupplyChainManagement.dto.SendMessageRequest;
import com.example.SupplyChainManagement.model.ChatMessage;
import com.example.SupplyChainManagement.model.Invoice;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.ChatMessageRepository;

import com.example.SupplyChainManagement.repository.PaymentMethodRepository;
import com.example.SupplyChainManagement.repository.UserRepository;

import jakarta.persistence.criteria.Path;
import jakarta.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {
	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;



	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	/* public List<ChatMessage> getMessages(Long senderId, Long receiverId) {
		return chatMessageRepository.findBySenderUserIdAndReceiverUserId(senderId, receiverId);
	} */
	
	public List<ChatMessageDTO> getMessages(Long userId1, Long userId2) {
        // Fetch messages in both directions
        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(userId1, userId2);
        return messages.stream().map(ChatMessageDTO::new).collect(Collectors.toList());
    }

	/*
	 * public ChatMessage sendMessage(ChatMessage message) {
	 * message.setTimestamp(LocalDateTime.now()); return
	 * chatMessageRepository.save(message); }
	 */

	// private static final String uploadDir = "src/main/resources/static/img/";
	/* public ChatMessage sendMessage(Long senderId, Long receiverId, String text, MultipartFile imageFile) {
	    User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
	    User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

	    ChatMessage message = new ChatMessage();
	    message.setSender(sender);
	    message.setReceiver(receiver);
	    message.setText(text);
	    message.setTimestamp(LocalDateTime.now());
	    message.setRead(false);

	    if (imageFile != null && !imageFile.isEmpty()) {
	        try {
	            String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
	            java.nio.file.Path filePath = Paths.get(UPLOAD_DIR, filename);
	            Files.createDirectories(filePath.getParent());
	            Files.write(filePath, imageFile.getBytes());

	            // Use the full URL
	            String baseUrl = "http://localhost:8080"; // Replace with a configurable property in production
	            message.setImageUrl(baseUrl + "/uploads/" + filename);
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to store file", e);
	        }
	    }

	    return chatMessageRepository.save(message);
	} */
	
	@Transactional
    public ChatMessageDTO sendMessage(Long senderId, Long receiverId, String text, MultipartFile imageFile) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(text);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path filePath = Paths.get(UPLOAD_DIR, filename);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, imageFile.getBytes());
                String baseUrl = "http://localhost:8080";
                message.setImageUrl(baseUrl + "/uploads/" + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }

        System.out.println("Before saving message: " + message);
        ChatMessage savedMessage = chatMessageRepository.save(message);
        chatMessageRepository.flush();
        System.out.println("After saving message: " + savedMessage);

        return new ChatMessageDTO(savedMessage);
    }

	private static final String UPLOAD_DIR = "uploads/"; // Already matches the config

	public ChatMessage sendInvoiceMessage(SendMessageRequest request) {
		try {
			System.out.println("Sending message from sender ID: " + request.getSenderId());
			System.out.println("To receiver ID: " + request.getReceiverId());
			System.out.println("Text: " + request.getText());
			System.out.println("Image URL: " + request.getImageUrl());

			// Send the message logic here
			ChatMessage message = new ChatMessage();
			message.setSender(userRepository.findById(request.getSenderId())
					.orElseThrow(() -> new IllegalArgumentException("Sender not found")));
			message.setReceiver(userRepository.findById(request.getReceiverId())
					.orElseThrow(() -> new IllegalArgumentException("Receiver not found")));
			message.setText(request.getText());
			message.setImageUrl(request.getImageUrl());
			message.setTimestamp(LocalDateTime.now());

			return chatMessageRepository.save(message);
		} catch (Exception e) {
			throw new RuntimeException("Failed to send message: " + e.getMessage());
		}
	}

	public List<ChatMessage> getUnreadMessages(Long receiverId) {
		return chatMessageRepository.findByReceiverUserIdAndIsReadFalse(receiverId);
	}

	public void markMessagesAsRead(Long senderId, Long receiverId) {
		List<ChatMessage> unreadMessages = chatMessageRepository
				.findBySenderUserIdAndReceiverUserIdAndIsReadFalse(senderId, receiverId);
		for (ChatMessage message : unreadMessages) {
			message.setRead(true);
		}
		chatMessageRepository.saveAll(unreadMessages);
	}

	/*
	 * public void markMessagesAsIsRead(Long userId, Long senderId) {
	 * List<ChatMessage> unreadMessages =
	 * chatMessageRepository.findByReceiverUserIdAndSenderUserIdAndIsReadFalse(
	 * userId, senderId); for (ChatMessage message : unreadMessages) {
	 * message.setRead(true); chatMessageRepository.save(message); } }
	 */

	public ChatMessage editMessage(Long messageId, String newText) {
		System.out.println("Fetching message with ID: " + messageId);
		ChatMessage message = chatMessageRepository.findById(messageId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

		System.out.println("Updating message text to: " + newText);
		message.setText(newText);

		return chatMessageRepository.save(message);
	}

	public void deleteMessage(Long messageId) {
		ChatMessage message = chatMessageRepository.findById(messageId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

		chatMessageRepository.delete(message);
	}

	/*
	 * public String createInvoice(InvoiceRequest request) { // Generate invoice
	 * logic here (e.g., using a PDF generation library) // Save the invoice image
	 * and return the URL String invoiceImageUrl = "https://example.com/invoices/" +
	 * UUID.randomUUID() + ".png"; return invoiceImageUrl; }
	 */

	/*
	 * @Transactional public String createInvoice(InvoiceRequest2 request) { try {
	 * // Create and populate the Invoice2 object Invoice2 invoice = new Invoice2();
	 * invoice.setOrderedDatetime(request.getOrderedDatetime());
	 * invoice.setDeliveredDatetime(request.getDeliveredDatetime());
	 * invoice.setPaymentMethod(paymentMethodRepository.findById(request.
	 * getPaymentMethodId()) .orElseThrow(() -> new
	 * RuntimeException("Payment method not found")));
	 * invoice.setSeller(userRepository.findById(request.getSellerId())
	 * .orElseThrow(() -> new RuntimeException("Seller not found")));
	 * invoice.setBuyer(userRepository.findById(request.getBuyerId())
	 * .orElseThrow(() -> new RuntimeException("Buyer not found")));
	 * 
	 * // Save the invoice invoice = invoiceRepository2.save(invoice);
	 * 
	 * // Save invoice materials for (InvoiceMaterial material :
	 * request.getMaterials()) { material.setInvoice2(invoice); // Link the material
	 * to the invoice material.setQuantity(material.getQuantity()); // Quantity is
	 * already a String invoiceMaterialRepository.save(material); // Save the
	 * material }
	 * 
	 * // Generate the invoice image String invoiceImageUrl =
	 * generateInvoiceImage(invoice); invoice.setInvoiceImageUrl(invoiceImageUrl);
	 * invoiceRepository2.save(invoice); // Update the invoice with the image URL
	 * 
	 * return invoiceImageUrl; } catch (Exception e) { throw new
	 * RuntimeException("Failed to create invoice: " + e.getMessage()); } }
	 */

	/* public ChatMessage sendInvoice(Long senderId, Long receiverId, String imageUrl) {
		User sender = userService.getUserById(senderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

		User receiver = userService.getUserById(receiverId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

		ChatMessage message = new ChatMessage();
		message.setSender(sender);
		message.setReceiver(receiver);
		message.setImageUrl(imageUrl);
		message.setText("Invoice Voucher");

		return chatMessageRepository.save(message);
	} */
	
	public ChatMessage sendInvoiceToBuyer(Invoice invoice, User buyer, String imageUrl) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(invoice.getSeller());
        chatMessage.setReceiver(buyer);
        chatMessage.setImageUrl(imageUrl);
        chatMessage.setText("Invoice Voucher");
        chatMessage.setInvoice(invoice);
        return chatMessageRepository.save(chatMessage);
    }

}

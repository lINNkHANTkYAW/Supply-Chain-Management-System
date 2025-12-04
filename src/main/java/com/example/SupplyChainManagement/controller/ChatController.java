package com.example.SupplyChainManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.SupplyChainManagement.dto.ChatListResponse;
import com.example.SupplyChainManagement.dto.ChatMessageDTO;
import com.example.SupplyChainManagement.dto.ChatMessageResponse;
import com.example.SupplyChainManagement.dto.DeleteMessageRequest;
import com.example.SupplyChainManagement.dto.EditMessageRequest;
import com.example.SupplyChainManagement.dto.InvoiceRequest;
import com.example.SupplyChainManagement.dto.InvoiceResponse;
import com.example.SupplyChainManagement.dto.PaymentMethodDTO;
import com.example.SupplyChainManagement.dto.SendInvoiceRequest;
import com.example.SupplyChainManagement.dto.SendMessageRequest;
import com.example.SupplyChainManagement.model.ChatMessage;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.PaymentMethod;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.ChatMessageRepository;
import com.example.SupplyChainManagement.repository.UserRepository;
import com.example.SupplyChainManagement.service.ChatService;
import com.example.SupplyChainManagement.service.FileStorageService;
import com.example.SupplyChainManagement.service.FileUploadService;
import com.example.SupplyChainManagement.service.InvoiceService;
import com.example.SupplyChainManagement.service.PaymentMethodService;
import com.example.SupplyChainManagement.service.ProductService;
import com.example.SupplyChainManagement.service.SupplierMaterialService;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.SupplyChainManagement.service.UserService;

import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Path;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.core.io.UrlResource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.nio.file.Paths;



@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SupplierMaterialService supplierMaterialService;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private PaymentMethodService paymentMethodService;

   
    
    /* @GetMapping("/notifications")
    public List<ChatMessage> getChatNotifications(@RequestParam Long userId) {
        List<ChatMessage> notifications = chatService.getUnreadMessages(userId);
        System.out.println("Notifications: " + notifications); // Log the response
        return notifications;
    } */
    
    /* @GetMapping("/notifications")
    public List<ChatMessageResponse> getChatNotifications(@RequestParam Long userId) {
        List<ChatMessage> unreadMessages = chatService.getUnreadMessages(userId);

        return unreadMessages.stream()
            .map(chat -> new ChatMessageResponse(
                chat.getSender().getUserId(),
                chat.getSender().getUsername(),
                chat.getSender().getProfileImg(),
                chat.getText(),
                1 // Assuming each unread message counts as 1 notification
            ))
            .collect(Collectors.toList());
    } */
    
    /* @GetMapping("/notifications")
    public List<ChatMessageResponse> getChatNotifications(@RequestParam Long userId) {
        // Get all unread messages for the user
        List<ChatMessage> unreadMessages = chatService.getUnreadMessages(userId);

        // Group by sender to count unread messages per sender
        Map<Long, Long> unreadCountBySender = unreadMessages.stream()
            .collect(Collectors.groupingBy(msg -> msg.getSender().getUserId(), Collectors.counting()));

        // Map the messages into a notification response format
        return unreadMessages.stream()
            .map(chat -> new ChatMessageResponse(
                chat.getSender().getUserId(),
                chat.getSender().getUsername(),
                chat.getSender().getProfileImg(),
                chat.getText(),
                unreadCountBySender.get(chat.getSender().getUserId()) // Use count from the map
            ))
            .collect(Collectors.toList());
    } */
    
    /* @GetMapping("/notifications")
    public List<ChatMessageResponse> getChatNotifications(@RequestParam Long userId) {
        List<ChatMessage> unreadMessages = chatService.getUnreadMessages(userId);

        // Group by senderId and create a list of notifications
        Map<Long, ChatMessageResponse> groupedNotifications = new HashMap<>();
        
        for (ChatMessage chat : unreadMessages) {
            // Get sender information
            Long senderId = chat.getSender().getUserId();
            String senderName = chat.getSender().getUsername();
            String senderProfileImage = chat.getSender().getProfileImg();
            String text = chat.getText();
            
            // Get the current notification entry for the sender, or create a new one
            ChatMessageResponse notification = groupedNotifications.getOrDefault(senderId, 
                new ChatMessageResponse(senderId, senderName, senderProfileImage, text, (long) 0));

            // Update the latest message text and increment unread count
            notification.setText(text); // Set the latest message
            notification.setUnreadCount(notification.getUnreadCount() + 1); // Increment the unread count
            
            groupedNotifications.put(senderId, notification); // Update the map
        }

        return new ArrayList<>(groupedNotifications.values());
    } */
    
    
    
    @GetMapping("/notifications")
    public List<ChatMessageResponse> getChatNotifications(@RequestParam Long userId) {
        // Fetch all messages for the user (both read and unread)
        List<ChatMessage> allMessages = chatMessageRepository.findByReceiverUserId(userId);

        // Group messages by sender and calculate unread counts
        Map<Long, ChatMessageResponse> groupedNotifications = new HashMap<>();

        for (ChatMessage chat : allMessages) {
            Long senderId = chat.getSender().getUserId();
            String senderName = chat.getSender().getUsername();
            String senderProfileImage = chat.getSender().getProfileImg();
            String text = chat.getText();
            boolean isRead = chat.isRead();

            // Get or create the notification entry for the sender
            ChatMessageResponse notification = groupedNotifications.getOrDefault(senderId,
                new ChatMessageResponse(senderId, senderName, senderProfileImage, text, 0L));

            // Update the latest message text
            notification.setText(text);

            // Increment unread count if the message is unread
            if (!isRead) {
                notification.setUnreadCount(notification.getUnreadCount() + 1);
            }

            groupedNotifications.put(senderId, notification);
        }

        return new ArrayList<>(groupedNotifications.values());
    }





    /* @GetMapping("/messages")
    public List<ChatMessage> getMessages(@RequestParam Long userId, @RequestParam Long senderId) {
        return chatMessageRepository.findBySenderUserIdAndReceiverUserId(userId, senderId)
                .stream()
                .map(message -> new ChatMessage(
                ))
                .collect(Collectors.toList());
    } */
    
    /* @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(@RequestParam Long senderId, @RequestParam Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findBySenderUserIdAndReceiverUserId(senderId, userId);

        return messages.stream()
            .map(chat -> new ChatMessageResponse(
                chat.getSender().getUserId(),
                chat.getSender().getUsername(),
                chat.getSender().getProfileImg(),
                chat.getText(),
                chat.getTimestamp()
            ))
            .collect(Collectors.toList());
    } */
    
    /* @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(
        @RequestParam Long senderId,
        @RequestParam Long receiverId) {

        // Fetch messages between the two users
        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(senderId, receiverId);

        // Log the fetched messages
        System.out.println("Fetched messages between senderId: " + senderId + " and receiverId: " + receiverId);
        messages.forEach(message -> System.out.println(message.getText()));

        return messages.stream()
            .map(chat -> new ChatMessageResponse(
            	chat.getMessageId(),
                chat.getSender().getUserId(),
                chat.getSender().getUsername(),
                chat.getSender().getProfileImg(),
                chat.getText(),
                chat.getImageUrl(),
                chat.getTimestamp()
            ))
            .collect(Collectors.toList());
    } */
    
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId) {
        List<ChatMessageDTO> messages = chatService.getMessages(senderId, receiverId); // Now fetches both directions
        System.out.println("Fetched messages: " + messages); // Debug
        return ResponseEntity.ok(messages);
    }
    


    /* @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody ChatMessage message) {
        return chatService.sendMessage(message);
    }  */
    
    /* @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestParam Long receiverId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal User sender) {

        // Ensure receiver exists
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Create a new ChatMessage entity
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setText(text);
        chatMessage.setTimestamp(LocalDateTime.now());

        // Handle image if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveImageFile(image); // Save image and get URL
            chatMessage.setImageUrl(imageUrl);
        }

        // Save message to the database
        chatMessageRepository.save(chatMessage);

        // Return response
        ChatMessageResponse response = new ChatMessageResponse(
                sender.getUserId(),
                sender.getUsername(),
                sender.getProfileImg(),
                chatMessage.getText(),
                chatMessage.getImageUrl(),
                0 // No unread count needed
        );
        System.out.println("Received message from user: " + sender.getUserId() + " to " + receiverId);
        System.out.println("Message text: " + text);


        return ResponseEntity.ok(response);
    } */
    
    /* @PostMapping("/send")
    public ChatMessage sendMessage(@RequestParam Long senderId,
                                   @RequestParam Long receiverId,
                                   @RequestParam(required = false) String text,
                                   @RequestParam(required = false) MultipartFile image) {

        User sender = userService.getUserById(senderId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));

        User receiver = userService.getUserById(receiverId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(text);

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveImageFile(image);
            message.setImageUrl(imageUrl);
        }

        return chatService.sendMessage(message);
    } */
    
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId,
            @RequestParam(value = "text", required = false) String text,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            ChatMessageDTO message = chatService.sendMessage(senderId, receiverId, text, imageFile);
            System.out.println("Returning message: " + message);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            System.err.println("Error in sendMessage endpoint: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send message", e);
        }
    }
    
    @PostMapping("/sendInvoice")
    public ResponseEntity<ChatMessage> sendInvoiceMessage(@RequestBody SendMessageRequest request) {
        try {
            System.out.println("Sending message with data: " + request);

            // Validate the request
            if (request.getSenderId() == null || request.getReceiverId() == null) {
                throw new IllegalArgumentException("Sender ID and Receiver ID must not be null");
            }

            // Send the message
            ChatMessage sentMessage = chatService.sendInvoiceMessage(request);
            return ResponseEntity.ok(sentMessage);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();

            // Return a JSON error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to send message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    } 


    @PutMapping("/markAsRead")
    public ResponseEntity<Void> markMessagesAsRead(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        chatService.markMessagesAsRead(senderId, receiverId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/payment-methods/user/{userId}")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByUserId(@PathVariable Long userId) {
        List<PaymentMethodDTO> paymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        
        if (paymentMethods.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no payment methods are found
        }
        
        return ResponseEntity.ok(paymentMethods); // Return 200 OK with the list of payment methods
    }
    
    /* @GetMapping("/seller-products")
	public ResponseEntity<List<DistriProduct>> getSellerProducts(@RequestParam Long userId) {
	    List<DistriProduct> products = productService.getProductsBySeller(userId);
	    return ResponseEntity.ok(products);
	} */
    
    @GetMapping("/seller-products")
	public ResponseEntity<List<SupplierRawMaterial>> getSellerProducts(@RequestParam Long userId) {
	    List<SupplierRawMaterial> materials = supplierMaterialService.getProductsBySeller(userId);
	    return ResponseEntity.ok(materials);
	}

    
    @PutMapping("/edit")
    public ResponseEntity<ChatMessage> editMessage(@RequestBody EditMessageRequest request) {
        try {
            System.out.println("Editing message with ID: " + request.getMessageId());
            System.out.println("New text: " + request.getText());

            ChatMessage updatedMessage = chatService.editMessage(request.getMessageId(), request.getText());
            return ResponseEntity.ok(updatedMessage);
        } catch (Exception e) {
            System.err.println("Error editing message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMessage(@RequestBody DeleteMessageRequest request) {
        try {
            chatService.deleteMessage(request.getMessageId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /* @PostMapping("/create-invoice")
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceRequest request) {
        try {
            String invoiceImageUrl = invoiceService.createInvoice(request);
            return ResponseEntity.ok(new InvoiceResponse(invoiceImageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    } */
    
    /* @PostMapping("/send-invoice")
    public ResponseEntity<ChatMessage> sendInvoice(@RequestBody SendInvoiceRequest request) {
        try {
            ChatMessage message = chatService.sendInvoice(request.getSenderId(), request.getReceiverId(), request.getImageUrl());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    } */
    
    /* @GetMapping("/list")
    public ResponseEntity<List<ChatListResponse>> getChatList(@RequestParam Long userId) {
        // Fetch all unique senders who have sent messages to the current user
        List<Long> senderIds = chatMessageRepository.findDistinctSendersByReceiverId(userId);

        List<ChatListResponse> chatList = new ArrayList<>();

        for (Long senderId : senderIds) {
            // Fetch the sender's details
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            // Fetch the last message in the conversation
            ChatMessage lastMessage = chatMessageRepository.findLastMessageBetweenUsers(senderId, userId);

            // Count unread messages from this sender
            Long unreadCount = chatMessageRepository.countUnreadMessages(senderId, userId);

            // Create a ChatListResponse object
            ChatListResponse chatResponse = new ChatListResponse(
                    sender.getUserId(),
                    sender.getUsername(),
                    sender.getProfileImg(),
                    lastMessage != null ? lastMessage.getText() : "No messages yet",
                    lastMessage != null ? lastMessage.getTimestamp() : null,
                    unreadCount
            );

            chatList.add(chatResponse);
        }

        return ResponseEntity.ok(chatList);
    } */
    
    @GetMapping("/list")
    public ResponseEntity<List<ChatListResponse>> getChatList(@RequestParam Long userId) {
        List<Long> senderIds = chatMessageRepository.findDistinctSendersByReceiverId(userId);
        List<ChatListResponse> chatList = new ArrayList<>();
        for (Long senderId : senderIds) {
            User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
            ChatMessage lastMessage = chatMessageRepository.findLastMessageBetweenUsers(senderId, userId);
            Long unreadCount = chatMessageRepository.countUnreadMessages(senderId, userId);
            ChatListResponse chatResponse = new ChatListResponse(
                    sender.getUserId(),
                    sender.getUsername(),
                    sender.getProfileImg(),
                    lastMessage != null ? lastMessage.getText() : "No messages yet",
                    lastMessage != null ? lastMessage.getTimestamp() : null,
                    unreadCount
            );
            chatList.add(chatResponse);
        }
        return ResponseEntity.ok(chatList);
    }

}
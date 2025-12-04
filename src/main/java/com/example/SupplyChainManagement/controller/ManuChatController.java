package com.example.SupplyChainManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.SupplyChainManagement.dto.ChatListResponse;
import com.example.SupplyChainManagement.dto.ChatMessageDTO;
import com.example.SupplyChainManagement.dto.ChatMessageResponse;
import com.example.SupplyChainManagement.dto.ChatSummaryDTO;
import com.example.SupplyChainManagement.dto.DeleteMessageRequest;
import com.example.SupplyChainManagement.dto.EditMessageRequest;
import com.example.SupplyChainManagement.dto.InvoiceRequest;
import com.example.SupplyChainManagement.dto.InvoiceResponse;
import com.example.SupplyChainManagement.dto.PaymentMethodDTO;
import com.example.SupplyChainManagement.dto.SendInvoiceRequest;
import com.example.SupplyChainManagement.dto.SendMessageRequest;
import com.example.SupplyChainManagement.model.ChatMessage;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.PaymentMethod;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.ChatMessageRepository;
import com.example.SupplyChainManagement.repository.UserRepository;
import com.example.SupplyChainManagement.service.ChatService;
import com.example.SupplyChainManagement.service.FileStorageService;
import com.example.SupplyChainManagement.service.FileUploadService;
import com.example.SupplyChainManagement.service.InvoiceService;
import com.example.SupplyChainManagement.service.ManuProductService;
import com.example.SupplyChainManagement.service.PaymentMethodService;
import com.example.SupplyChainManagement.service.ProductService;
import com.example.SupplyChainManagement.service.SupplierMaterialService;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.SupplyChainManagement.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/manu")
public class ManuChatController {
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ManuProductService manuProductService;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private PaymentMethodService paymentMethodService;
    
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
    
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverId") Long receiverId) {
        List<ChatMessageDTO> messages = chatService.getMessages(senderId, receiverId); // Now fetches both directions
        System.out.println("Fetched messages: " + messages); // Debug
        return ResponseEntity.ok(messages);
    }
    
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
    
    @GetMapping("/seller-products")
	public ResponseEntity<List<ManuProduct>> getSellerProducts(@RequestParam Long userId) {
	    List<ManuProduct> products = manuProductService.getProductsBySeller(userId);
	    return ResponseEntity.ok(products);
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
    public List<ChatSummaryDTO> getChatList(@RequestParam Long userId) {
        // Fetch all messages where userId is either the sender or receiver
        List<ChatMessage> messages = chatMessageRepository.findBySenderUserIdOrReceiverUserId(userId, userId);

        // Group messages by the other user in the conversation
        Map<Long, List<ChatMessage>> chatMap = new HashMap<>();
        for (ChatMessage message : messages) {
            Long otherUserId = message.getSender().getUserId().equals(userId) ? message.getReceiver().getUserId() : message.getSender().getUserId();
            chatMap.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(message);
        }

        // Convert to ChatSummaryDTO list
        List<ChatSummaryDTO> chatList = new ArrayList<>();
        for (Map.Entry<Long, List<ChatMessage>> entry : chatMap.entrySet()) {
            Long otherUserId = entry.getKey();
            List<ChatMessage> conversationMessages = entry.getValue();

            // Sort messages by timestamp
            conversationMessages.sort(Comparator.comparing(ChatMessage::getTimestamp));

            // Filter messages to only include those sent by the other user (POTENTIAL ISSUE HERE)
            List<ChatMessage> filteredMessages = conversationMessages.stream()
                    .filter(msg -> !msg.getSender().equals(userId))
                    .collect(Collectors.toList());

            // Fetch the other user's details
            User otherUser = userRepository.findById(otherUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ChatSummaryDTO chatSummary = new ChatSummaryDTO();
            chatSummary.setSenderId(otherUserId);
            chatSummary.setSenderName(otherUser.getUsername());
            chatSummary.setMessages(filteredMessages); // Only messages from the other user

            chatList.add(chatSummary);
        }

        return chatList;
    }

}
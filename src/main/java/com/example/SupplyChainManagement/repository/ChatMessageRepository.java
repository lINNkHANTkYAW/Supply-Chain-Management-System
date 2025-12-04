package com.example.SupplyChainManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.SupplyChainManagement.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByReceiverUserIdAndIsReadFalse(Long receiverId);
    List<ChatMessage> findBySenderUserIdAndReceiverUserIdAndIsReadFalse(Long senderId, Long receiverId);
    // List<ChatMessage> findByReceiverUserIdAndSenderUserIdAndIsReadFalse(Long receiverUserId, Long senderUserId);
	List<ChatMessage> findByReceiverUserId(Long userId);
	/* List<ChatMessage> findBySenderUserIdAndReceiverUserIdOrReceiverUserIdAndSenderUserId(Long senderId, Long userId,
			Long userId2, Long senderId2); */
	
	/* @Query("SELECT m FROM ChatMessage m WHERE (m.sender.userId = :senderId AND m.receiver.userId = :receiverId) OR (m.sender.userId = :receiverId AND m.receiver.userId = :senderId)")
    List<ChatMessage> findMessagesBetweenUsers(
        @Param("senderId") Long senderId, 
        @Param("receiverId") Long receiverId
    ); */
	
	@Query("SELECT cm FROM ChatMessage cm " +
		       "WHERE (cm.sender.userId = :senderId AND cm.receiver.userId = :receiverId) " +
		       "OR (cm.sender.userId = :receiverId AND cm.receiver.userId = :senderId) " +
		       "ORDER BY cm.timestamp ASC")
		List<ChatMessage> findMessagesBetweenUsers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
	
	@Query("SELECT m FROM ChatMessage m WHERE m.sender.userId = :userId OR m.receiver.userId = :userId")
	List<ChatMessage> findByReceiverUserIdOrSenderUserId(@Param("userId") Long userId);
	
	// List<ChatMessage> findByMessageId(Long messageId);
	
	// Fetch distinct senders who have sent messages to the receiver
    @Query("SELECT DISTINCT cm.sender.userId FROM ChatMessage cm WHERE cm.receiver.userId = :receiverId")
    List<Long> findDistinctSendersByReceiverId(@Param("receiverId") Long receiverId);

    // Fetch the last message between two users
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE (cm.sender.userId = :senderId AND cm.receiver.userId = :receiverId) " +
           "OR (cm.sender.userId = :receiverId AND cm.receiver.userId = :senderId) " +
           "ORDER BY cm.timestamp DESC LIMIT 1")
    ChatMessage findLastMessageBetweenUsers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    // Count unread messages from a specific sender to the receiver
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.sender.userId = :senderId AND cm.receiver.userId = :receiverId AND cm.isRead = false")
    Long countUnreadMessages(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
	List<ChatMessage> findBySenderUserIdOrReceiverUserId(Long userId, Long userId2);
	
	@Query("SELECT m FROM ChatMessage m WHERE m.sender.userId = :senderId AND m.receiver.userId = :receiverId")
	List<ChatMessage> findBySenderUserIdAndReceiverUserId(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
	
	
}

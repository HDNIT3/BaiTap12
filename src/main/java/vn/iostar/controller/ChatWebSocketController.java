package vn.iostar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn.iostar.entity.Text;
import vn.iostar.entity.User;
import vn.iostar.service.RoomService;
import vn.iostar.service.TextService;
import vn.iostar.service.UserService;

@Controller
public class ChatWebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private TextService textService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private UserService userService;
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // Kiểm tra quyền truy cập phòng
        if (!roomService.canUserAccessRoom(chatMessage.getRoomId(), chatMessage.getSenderId())) {
            return;
        }
        
        // Lưu tin nhắn vào database
        Text savedMessage = textService.saveMessage(
            chatMessage.getRoomId(), 
            chatMessage.getSenderId(), 
            chatMessage.getContent()
        );
        
        // Lấy thông tin user gửi
        User sender = userService.getUserById(chatMessage.getSenderId());
        
        // Tạo message response
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(savedMessage.getId());
        response.setRoomId(savedMessage.getIdroom());
        response.setSenderId(savedMessage.getIduser());
        response.setSenderName(sender.getName());
        response.setContent(savedMessage.getText());
        response.setTimestamp(savedMessage.getCreatedAt());
        
        // Gửi tin nhắn đến tất cả client trong phòng
        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), response);
    }
    
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage) {
        // Thông báo user join phòng
        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);
    }
    
    // DTO classes
    public static class ChatMessage {
        private Long roomId;
        private Long senderId;
        private String content;
        private String type; // JOIN, CHAT, LEAVE
        
        // Constructors
        public ChatMessage() {}
        
        // Getters and Setters
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
    
    public static class ChatMessageResponse {
        private Long id;
        private Long roomId;
        private Long senderId;
        private String senderName;
        private String content;
        private java.time.LocalDateTime timestamp;
        
        // Constructors
        public ChatMessageResponse() {}
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public java.time.LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}
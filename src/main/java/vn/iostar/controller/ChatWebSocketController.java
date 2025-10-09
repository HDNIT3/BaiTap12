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
    
    @MessageMapping("/rooms.update")
    public void updateRooms(@Payload RoomUpdateMessage message) {
        // Broadcast room list update to all users
        messagingTemplate.convertAndSend("/topic/rooms/update", message);
    }
    
    @MessageMapping("/user.online")
    public void userOnline(@Payload UserStatusMessage message) {
        // Broadcast user online status
        messagingTemplate.convertAndSend("/topic/user/status", message);
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
    
    // New DTO classes
    public static class RoomUpdateMessage {
        private String type; // CREATE, DELETE
        private Long roomId;
        private Long userId1;
        private Long userId2;
        private String userName1;
        private String userName2;
        
        // Constructors
        public RoomUpdateMessage() {}
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }
        
        public Long getUserId1() { return userId1; }
        public void setUserId1(Long userId1) { this.userId1 = userId1; }
        
        public Long getUserId2() { return userId2; }
        public void setUserId2(Long userId2) { this.userId2 = userId2; }
        
        public String getUserName1() { return userName1; }
        public void setUserName1(String userName1) { this.userName1 = userName1; }
        
        public String getUserName2() { return userName2; }
        public void setUserName2(String userName2) { this.userName2 = userName2; }
    }
    
    public static class UserStatusMessage {
        private Long userId;
        private String userName;
        private String status; // ONLINE, OFFLINE
        
        // Constructors
        public UserStatusMessage() {}
        
        public UserStatusMessage(Long userId, String userName, String status) {
            this.userId = userId;
            this.userName = userName;
            this.status = status;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
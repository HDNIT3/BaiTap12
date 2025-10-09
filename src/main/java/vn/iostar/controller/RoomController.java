package vn.iostar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iostar.entity.Room;
import vn.iostar.entity.Text;
import vn.iostar.entity.User;
import vn.iostar.service.RoomService;
import vn.iostar.service.TextService;
import vn.iostar.service.UserService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TextService textService;
    
    /**
     * Trang chọn phòng chat - hiển thị danh sách phòng và danh sách user để tạo phòng mới
     */
    @GetMapping("")
    public String roomSelection(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Lấy danh sách phòng của user hiện tại
        List<Room> userRooms = roomService.getUserRooms(currentUser.getId());
        
        // Lấy thông tin chi tiết cho mỗi phòng
        List<RoomInfo> roomInfos = userRooms.stream().map(room -> {
            Long otherUserId = roomService.getOtherUserId(room.getIdroom(), currentUser.getId());
            User otherUser = userService.getUserById(otherUserId);
            Text lastMessage = textService.getLatestMessage(room.getIdroom());
            Long messageCount = textService.countRoomMessages(room.getIdroom());
            
            return new RoomInfo(room, otherUser, lastMessage, messageCount);
        }).collect(Collectors.toList());
        
        // Lấy danh sách user để tạo phòng mới (trừ user hiện tại)
        List<User> availableUsers = userService.getAllUsersExceptCurrent(currentUser.getId());
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("roomInfos", roomInfos);
        model.addAttribute("availableUsers", availableUsers);
        
        return "rooms/selection";
    }
    
    /**
     * Tạo phòng chat mới với user được chọn
     */
    @PostMapping("/create")
    public String createRoom(@RequestParam Long targetUserId,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            Room room = roomService.createOrFindRoom(currentUser.getId(), targetUserId);
            return "redirect:/rooms/" + room.getIdroom() + "/chat";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/rooms";
        }
    }
    
    /**
     * Vào phòng chat
     */
    @GetMapping("/{roomId}/chat")
    public String chatRoom(@PathVariable Long roomId,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Kiểm tra quyền truy cập phòng
        if (!roomService.canUserAccessRoom(roomId, currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập phòng này!");
            return "redirect:/rooms";
        }
        
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            redirectAttributes.addFlashAttribute("error", "Phòng không tồn tại!");
            return "redirect:/rooms";
        }
        
        // Lấy thông tin user còn lại trong phòng
        Long otherUserId = roomService.getOtherUserId(roomId, currentUser.getId());
        User otherUser = userService.getUserById(otherUserId);
        
        // Lấy lịch sử tin nhắn
        List<Text> messages = textService.getRoomMessages(roomId);
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("room", room);
        model.addAttribute("messages", messages);
        
        return "rooms/chat";
    }
    
    /**
     * Xóa phòng chat
     */
    @PostMapping("/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        if (roomService.deleteRoom(roomId, currentUser.getId())) {
            redirectAttributes.addFlashAttribute("success", "Đã xóa phòng chat!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa phòng chat!");
        }
        
        return "redirect:/rooms";
    }
    
    /**
     * Class hỗ trợ hiển thị thông tin phòng
     */
    public static class RoomInfo {
        private Room room;
        private User otherUser;
        private Text lastMessage;
        private Long messageCount;
        
        public RoomInfo(Room room, User otherUser, Text lastMessage, Long messageCount) {
            this.room = room;
            this.otherUser = otherUser;
            this.lastMessage = lastMessage;
            this.messageCount = messageCount;
        }
        
        // Getters
        public Room getRoom() { return room; }
        public User getOtherUser() { return otherUser; }
        public Text getLastMessage() { return lastMessage; }
        public Long getMessageCount() { return messageCount; }
    }
}
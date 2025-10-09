package vn.iostar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iostar.entity.Room;
import vn.iostar.entity.User;
import vn.iostar.repository.RoomRepository;
import vn.iostar.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Tạo hoặc tìm phòng chat giữa 2 user
     * @param user1Id ID của user thứ nhất
     * @param user2Id ID của user thứ hai
     * @return Room đã tạo hoặc đã tồn tại
     * @throws RuntimeException nếu không thể tạo phòng
     */
    public Room createOrFindRoom(Long user1Id, Long user2Id) {
        // Kiểm tra không thể tạo phòng với chính mình
        if (user1Id.equals(user2Id)) {
            throw new RuntimeException("Không thể tạo phòng chat với chính mình!");
        }
        
        // Kiểm tra 2 user có tồn tại không
        Optional<User> user1 = userRepository.findById(user1Id);
        Optional<User> user2 = userRepository.findById(user2Id);
        
        if (user1.isEmpty() || user2.isEmpty()) {
            throw new RuntimeException("Một hoặc cả hai user không tồn tại!");
        }
        
        // Tìm phòng đã tồn tại giữa 2 user
        Optional<Room> existingRoom = roomRepository.findByTwoUsers(user1Id, user2Id);
        
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }
        
        // Tạo phòng mới nếu chưa tồn tại
        Room newRoom = new Room(user1Id, user2Id);
        return roomRepository.save(newRoom);
    }
    
    /**
     * Lấy danh sách tất cả phòng của một user
     * @param userId ID của user
     * @return Danh sách phòng
     */
    public List<Room> getUserRooms(Long userId) {
        return roomRepository.findRoomsByUserId(userId);
    }
    
    /**
     * Kiểm tra user có quyền truy cập phòng không
     * @param roomId ID phòng
     * @param userId ID user
     * @return true nếu có quyền truy cập
     */
    public boolean canUserAccessRoom(Long roomId, Long userId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            return false;
        }
        
        Room r = room.get();
        return r.getIduser1().equals(userId) || r.getIduser2().equals(userId);
    }
    
    /**
     * Lấy thông tin phòng
     * @param roomId ID phòng
     * @return Room hoặc null nếu không tồn tại
     */
    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }
    
    /**
     * Lấy ID của user còn lại trong phòng
     * @param roomId ID phòng
     * @param currentUserId ID user hiện tại
     * @return ID của user còn lại
     */
    public Long getOtherUserId(Long roomId, Long currentUserId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            return null;
        }
        
        Room r = room.get();
        if (r.getIduser1().equals(currentUserId)) {
            return r.getIduser2();
        } else if (r.getIduser2().equals(currentUserId)) {
            return r.getIduser1();
        }
        
        return null;
    }
    
    /**
     * Xóa phòng chat
     * @param roomId ID phòng
     * @param userId ID user yêu cầu xóa
     * @return true nếu xóa thành công
     */
    public boolean deleteRoom(Long roomId, Long userId) {
        if (!canUserAccessRoom(roomId, userId)) {
            return false;
        }
        
        roomRepository.deleteById(roomId);
        return true;
    }
}
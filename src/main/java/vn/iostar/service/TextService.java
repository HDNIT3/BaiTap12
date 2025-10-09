package vn.iostar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iostar.entity.Text;
import vn.iostar.repository.TextRepository;

import java.util.List;

@Service
public class TextService {
    
    @Autowired
    private TextRepository textRepository;
    
    /**
     * Lưu tin nhắn mới
     * @param roomId ID phòng
     * @param userId ID user gửi
     * @param message Nội dung tin nhắn
     * @return Text đã lưu
     */
    public Text saveMessage(Long roomId, Long userId, String message) {
        Text text = new Text(roomId, userId, message);
        return textRepository.save(text);
    }
    
    /**
     * Lấy tất cả tin nhắn trong phòng
     * @param roomId ID phòng
     * @return Danh sách tin nhắn theo thứ tự thời gian
     */
    public List<Text> getRoomMessages(Long roomId) {
        return textRepository.findByRoomIdOrderByCreatedAt(roomId);
    }
    
    /**
     * Đếm số tin nhắn trong phòng
     * @param roomId ID phòng
     * @return Số lượng tin nhắn
     */
    public Long countRoomMessages(Long roomId) {
        return textRepository.countByIdroom(roomId);
    }
    
    /**
     * Lấy tin nhắn mới nhất trong phòng
     * @param roomId ID phòng
     * @return Tin nhắn mới nhất hoặc null
     */
    public Text getLatestMessage(Long roomId) {
        List<Text> messages = textRepository.findLatestMessagesByRoomId(roomId);
        return messages.isEmpty() ? null : messages.get(0);
    }
}
package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.Text;

import java.util.List;

@Repository
public interface TextRepository extends JpaRepository<Text, Long> {
    
    // Tìm tất cả tin nhắn trong phòng theo thứ tự thời gian
    @Query("SELECT t FROM Text t WHERE t.idroom = :roomId ORDER BY t.createdAt ASC")
    List<Text> findByRoomIdOrderByCreatedAt(@Param("roomId") Long roomId);
    
    // Tìm tin nhắn mới nhất trong phòng
    @Query("SELECT t FROM Text t WHERE t.idroom = :roomId ORDER BY t.createdAt DESC")
    List<Text> findLatestMessagesByRoomId(@Param("roomId") Long roomId);
    
    // Đếm số tin nhắn trong phòng
    Long countByIdroom(Long roomId);
}
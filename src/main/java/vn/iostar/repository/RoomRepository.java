package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    // Tìm phòng theo 2 user ID
    @Query("SELECT r FROM Room r WHERE (r.iduser1 = :user1 AND r.iduser2 = :user2) OR (r.iduser1 = :user2 AND r.iduser2 = :user1)")
    Optional<Room> findByTwoUsers(@Param("user1") Long user1, @Param("user2") Long user2);
    
    // Tìm tất cả phòng của 1 user
    @Query("SELECT r FROM Room r WHERE r.iduser1 = :userId OR r.iduser2 = :userId")
    List<Room> findRoomsByUserId(@Param("userId") Long userId);
    
    // Đếm số phòng của 1 user
    @Query("SELECT COUNT(r) FROM Room r WHERE r.iduser1 = :userId OR r.iduser2 = :userId")
    Long countRoomsByUserId(@Param("userId") Long userId);
    
    // Tìm phòng có sẵn (chưa đầy 2 người) - hiện tại không cần vì mỗi phòng chỉ có 2 người
    @Query("SELECT r FROM Room r")
    List<Room> findAllRooms();
}
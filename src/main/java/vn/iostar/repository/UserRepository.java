package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Tìm user theo tên
    Optional<User> findByName(String name);
    
    // Tìm user theo tên và password
    Optional<User> findByNameAndPass(String name, String pass);
    
    // Tìm tất cả user trừ user hiện tại
    @Query("SELECT u FROM User u WHERE u.id != :currentUserId")
    List<User> findAllExceptCurrent(@Param("currentUserId") Long currentUserId);
    
    // Kiểm tra tên user đã tồn tại
    boolean existsByName(String name);
}
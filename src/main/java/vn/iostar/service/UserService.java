package vn.iostar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iostar.entity.User;
import vn.iostar.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Đăng ký user mới
     * @param name Tên user
     * @param password Mật khẩu
     * @return User đã tạo
     * @throws RuntimeException nếu tên đã tồn tại
     */
    public User registerUser(String name, String password) {
        if (userRepository.existsByName(name)) {
            throw new RuntimeException("Tên người dùng đã tồn tại!");
        }
        
        User user = new User(name, password);
        return userRepository.save(user);
    }
    
    /**
     * Đăng nhập user
     * @param name Tên user
     * @param password Mật khẩu
     * @return User nếu đăng nhập thành công
     * @throws RuntimeException nếu thông tin không đúng
     */
    public User loginUser(String name, String password) {
        Optional<User> user = userRepository.findByNameAndPass(name, password);
        if (user.isEmpty()) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        return user.get();
    }
    
    /**
     * Lấy thông tin user theo ID
     * @param userId ID user
     * @return User hoặc null
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    /**
     * Lấy danh sách tất cả user trừ user hiện tại
     * @param currentUserId ID user hiện tại
     * @return Danh sách user
     */
    public List<User> getAllUsersExceptCurrent(Long currentUserId) {
        return userRepository.findAllExceptCurrent(currentUserId);
    }
    
    /**
     * Tìm user theo tên
     * @param name Tên user
     * @return User hoặc null
     */
    public User findByName(String name) {
        return userRepository.findByName(name).orElse(null);
    }
}
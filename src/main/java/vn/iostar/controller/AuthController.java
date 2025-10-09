package vn.iostar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iostar.entity.User;
import vn.iostar.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String name, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        try {
            User user = userService.loginUser(name, password);
            session.setAttribute("currentUser", user);
            
            // Broadcast user online status
            ChatWebSocketController.UserStatusMessage statusMessage = 
                new ChatWebSocketController.UserStatusMessage(user.getId(), user.getName(), "ONLINE");
            messagingTemplate.convertAndSend("/topic/user/status", statusMessage);
            
            return "redirect:/rooms";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String name,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            if (!password.equals(confirmPassword)) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp!");
            }
            
            userService.registerUser(name, password);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // Broadcast user offline status
            ChatWebSocketController.UserStatusMessage statusMessage = 
                new ChatWebSocketController.UserStatusMessage(currentUser.getId(), currentUser.getName(), "OFFLINE");
            messagingTemplate.convertAndSend("/topic/user/status", statusMessage);
        }
        
        session.invalidate();
        return "redirect:/login";
    }
}
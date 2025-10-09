package vn.iostar.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "texts")
public class Text {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "idroom", nullable = false)
    private Long idroom;
    
    @Column(name = "iduser", nullable = false)
    private Long iduser;
    
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Text() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Text(Long idroom, Long iduser, String text) {
        this.idroom = idroom;
        this.iduser = iduser;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getIdroom() {
        return idroom;
    }
    
    public void setIdroom(Long idroom) {
        this.idroom = idroom;
    }
    
    public Long getIduser() {
        return iduser;
    }
    
    public void setIduser(Long iduser) {
        this.iduser = iduser;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Text{" +
                "id=" + id +
                ", idroom=" + idroom +
                ", iduser=" + iduser +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
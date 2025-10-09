package vn.iostar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idroom;
    
    @Column(name = "iduser1", nullable = false)
    private Long iduser1;
    
    @Column(name = "iduser2", nullable = false)
    private Long iduser2;
    
    // Constructors
    public Room() {}
    
    public Room(Long iduser1, Long iduser2) {
        this.iduser1 = iduser1;
        this.iduser2 = iduser2;
    }
    
    // Getters and Setters
    public Long getIdroom() {
        return idroom;
    }
    
    public void setIdroom(Long idroom) {
        this.idroom = idroom;
    }
    
    public Long getIduser1() {
        return iduser1;
    }
    
    public void setIduser1(Long iduser1) {
        this.iduser1 = iduser1;
    }
    
    public Long getIduser2() {
        return iduser2;
    }
    
    public void setIduser2(Long iduser2) {
        this.iduser2 = iduser2;
    }
    
    @Override
    public String toString() {
        return "Room{" +
                "idroom=" + idroom +
                ", iduser1=" + iduser1 +
                ", iduser2=" + iduser2 +
                '}';
    }
}
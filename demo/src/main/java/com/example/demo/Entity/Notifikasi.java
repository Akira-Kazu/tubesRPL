package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifikasi")
public class Notifikasi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailUser; // Email penerima notif (Mhs/Dosen)
    
    @Column(columnDefinition = "TEXT")
    private String pesan;

    private LocalDateTime waktuDibuat;

    // Constructor helper
    public Notifikasi() {}
    public Notifikasi(String emailUser, String pesan) {
        this.emailUser = emailUser;
        this.pesan = pesan;
        this.waktuDibuat = LocalDateTime.now();
    }
}
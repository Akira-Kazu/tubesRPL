package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "pembimbing")
public class Pembimbing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pembimbing") // Mapping ke kolom baru di DB
    private Integer idPembimbing;

    // Relasi Mahasiswa
    @ManyToOne
    @JoinColumn(name = "email_mahasiswa", nullable = false)
    private Pengguna mahasiswa;

    // Relasi Dosen
    @ManyToOne
    @JoinColumn(name = "email_dosen", nullable = false)
    private Pengguna dosen;
}
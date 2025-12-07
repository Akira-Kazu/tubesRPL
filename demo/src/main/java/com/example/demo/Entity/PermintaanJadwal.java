package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "permintaan_jadwal")
public class PermintaanJadwal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permintaan")
    private Long id;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    @Column(name = "catatan")
    private String catatan;

    @Column(name = "status", nullable = false)
    private String status = "Pending"; // default biar aman

    @Column(name = "lokasi")
    private String lokasi;

    @Column(name = "waktu")
    private LocalTime waktu;

    // Relasi ke Mahasiswa
    @ManyToOne
    @JoinColumn(name = "email_mahasiswa", referencedColumnName = "email")
    private Pengguna mahasiswa;

    // Relasi ke Dosen
    @ManyToOne
    @JoinColumn(name = "email_dosen", referencedColumnName = "email")
    private Pengguna dosen;
}

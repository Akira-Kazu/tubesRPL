package com.example.demo.Repository;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "permintaan_jadwal")
public class PermintaanJadwal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permintaan")
    private Long id_permintaan; // Sesuaikan nama ID biar gampang   dipanggil

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    @Column(name = "catatan")
    private String catatan;

    // --- KOLOM STATUS (WAJIB ADA BUAT DOSEN) ---
    @Column(name = "status")    
    private String status = "PENDING"; // Default value

    @Column(name = "alasan_penolakan")
    private String alasan_penolakan;

    @Column(name = "tugas_berikutnya")
    private String tugas_berikutnya;
    // ------------------------------------------

    // Relasi ke Mahasiswa
    @ManyToOne
    @JoinColumn(name = "email_mahasiswa", referencedColumnName = "email")
    private Pengguna mahasiswa;

    // Relasi ke Dosen
    @ManyToOne
    @JoinColumn(name = "email_dosen", referencedColumnName = "email")
    private Pengguna dosen;
}
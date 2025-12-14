package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.time.LocalDateTime; // *** PENTING: Import ini baru ***

@Data
@Entity
@Table(name = "bimbingan")
public class Bimbingan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bimbingan")
    private Long idBimbingan;

    // --- Kolom Bawaan ---
    @Column(name = "is_bimbingan")
    private Boolean isBimbingan = true;

    private String lokasi;

    private String hari;

    private LocalTime waktu;

    // RELASI KE PERMINTAAN_JADWAL (Sudah ada)
    @ManyToOne(fetch = FetchType.LAZY) // Tambahkan fetch type untuk efisiensi
    @JoinColumn(name = "id_permintaan", referencedColumnName = "id_permintaan")
    private PermintaanJadwal permintaanJadwal;

    private String komentarDosen;

    // ---------------------------------------------
    // --- KOLOM DAN RELASI BARU DARI ALTER TABLE ---
    // ---------------------------------------------

    // 1. Waktu Realisasi Sesi (untuk pelacakan batas UTS/UAS)
    @Column(name = "tanggal_waktu_realisasi")
    private LocalDateTime tanggalWaktuRealisasi;

    // 2. Status Realisasi Sesi (apakah Selesai, Batal, Tidak Hadir)
    // Di DB: status_realisasi
    @Column(name = "status_realisasi")
    private String statusRealisasi;

    // 3. Relasi ke Tugas Akhir (untuk penghitungan persyaratan)
    @ManyToOne(fetch = FetchType.LAZY) // Hubungan Banyak Bimbingan ke Satu Tugas Akhir
    @JoinColumn(name = "id_ta") // Nama kolom Foreign Key di tabel 'bimbingan'
    private TugasAkhir tugasAkhir;

    /*
     * Catatan: Kolom 'hari' dan 'waktu' yang lama mungkin perlu diabaikan
     * atau dihapus dari kode Entitas jika Anda hanya menggunakan
     * 'tanggalWaktuRealisasi' untuk sesi yang telah diselesaikan.
     * Saya biarkan di sini karena sudah ada di kode Anda, tetapi
     * 'tanggalWaktuRealisasi' adalah yang digunakan untuk persyaratan.
     */
}

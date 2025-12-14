package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "tugas_akhir")
public class TugasAkhir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ta")
    private Integer idTa;

    @Column(name = "judul", nullable = false)
    private String judul;

    private String topik;

    private String komentar;

    // -----------------------------------------------------------------
    // --- RELASI ---
    // -----------------------------------------------------------------

    // RELASI KE PENGGUNA (MAHASISWA)
    // Foreign Key 'email' yang mereferensikan Pengguna
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email")
    private Pengguna mahasiswa;

    // RELASI KE BIMBINGAN
    // One-to-Many: Satu Tugas Akhir memiliki banyak sesi Bimbingan
    // mappedBy harus sesuai dengan nama field relasi di Entitas Bimbingan (yaitu, 'tugasAkhir')
    @OneToMany(mappedBy = "tugasAkhir", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bimbingan> bimbinganList;
}
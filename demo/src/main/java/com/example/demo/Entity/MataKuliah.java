package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "mata_kuliah")
public class MataKuliah {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mk")
    private Long id;

    @Column(name = "nama_mk", nullable = false)
    private String namaMK;

    // Relasi ke jadwal (multi-sesi)
    @OneToMany(mappedBy = "mataKuliah", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JadwalMK> jadwal;

    // Relasi ke mahasiswa melalui tabel terdaftar
    @ManyToMany
    @JoinTable(
            name = "terdaftar",
            joinColumns = @JoinColumn(name = "id_mk"),
            inverseJoinColumns = @JoinColumn(name = "email")
    )
    private Set<Pengguna> mahasiswa;

    // Method untuk mengakses semua sesi jadwal
    public Set<JadwalMK> getJadwalSesi() {
        return this.jadwal;
    }
}


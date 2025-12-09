package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "terdaftar")
@IdClass(TerdaftarId.class)
public class Terdaftar {

    @Id
    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Pengguna mahasiswa; // Mahasiswa yang mengambil mata kuliah

    @Id
    @ManyToOne
    @JoinColumn(name = "id_mk", nullable = false)
    private MataKuliah mataKuliah; // Mata kuliah yang diambil
}

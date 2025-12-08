package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "bimbingan")
public class Bimbingan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bimbingan")
    private Long idBimbingan;

    @Column(name = "is_bimbingan")
    private Boolean isBimbingan = true;

    private String lokasi;

    private String hari;

    private LocalTime waktu;

    // RELASI KE PERMINTAAN_JADWAL
    @ManyToOne
    @JoinColumn(name = "id_permintaan", referencedColumnName = "id_permintaan")
    private PermintaanJadwal permintaanJadwal;

    private String komentarDosen;
}


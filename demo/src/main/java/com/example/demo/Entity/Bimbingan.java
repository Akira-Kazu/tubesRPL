package com.example.demo.Entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "bimbingan")
public class Bimbingan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBimbingan;

    @Column(name = "is_bimbingan")
    private Boolean isBimbingan = true;

    private String lokasi;

    private String hari;   // <-- ini menyimpan tanggal (kalau mau LocalDate boleh ubah di database)

    private LocalTime waktu;

    @ManyToOne
    @JoinColumn(name = "id_permintaan", referencedColumnName = "id_permintaan")
    private PermintaanJadwal permintaanJadwal;
}

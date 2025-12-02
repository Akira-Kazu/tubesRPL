package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "pengajuan_dosen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PengajuanDosen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String waktu;
    private String hariTanggal;
    private String mahasiswa;
    private String lokasi;
    private String jenis;
}

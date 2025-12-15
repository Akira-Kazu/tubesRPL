package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Otomatis bikin Getter, Setter, toString, dll
@NoArgsConstructor // Bikin Constructor kosong
@AllArgsConstructor // Bikin Constructor dengan semua argumen
public class ProgressMhsDTO {
    private String namaMahasiswa;
    private String emailMahasiswa;
    private String judulTA;
    private String topikTA;
    private int jumlahBimbingan;
    private LocalDateTime tanggalTerakhirBimbingan;
}
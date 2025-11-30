package com.example.demo.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "permintaan_jadwal")
public class PermintaanJadwal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_permintaan;

    private LocalDate tanggal;

    private String catatan;

    private String email_mahasiswa;

    private String email_dosen;

    public Integer getId_permintaan() { 
        return id_permintaan; 
    }
    public void setId_permintaan(Integer id_permintaan) { 
        this.id_permintaan = id_permintaan; 
    }

    public LocalDate getTanggal() { 
        return tanggal; 
    }
    public void setTanggal(LocalDate tanggal) { 
        this.tanggal = tanggal; 
    }

    public String getCatatan() { 
        return catatan; 
    }
    public void setCatatan(String catatan) { 
        this.catatan = catatan; 
    }

    public String getEmail_mahasiswa() { 
        return email_mahasiswa; 
    }
    public void setEmail_mahasiswa(String email_mahasiswa) { 
        this.email_mahasiswa = email_mahasiswa; 
    }

    public String getEmail_dosen() { 
        return email_dosen; 
    }
    public void setEmail_dosen(String email_dosen) { 
        this.email_dosen = email_dosen; 
    }
}

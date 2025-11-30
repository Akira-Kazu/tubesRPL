package com.example.demo.Entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "bimbingan")
public class Bimbingan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bimbingan")
    private Integer id_bimbingan;

    @Column(name = "is_bimbingan")
    private Boolean is_bimbingan;

    private String lokasi;

    private String hari;

    @Column(columnDefinition = "TIME")
    private LocalTime waktu;

    @Column(name = "id_permintaan")
    private Integer id_permintaan;

    // GETTER & SETTER
    public Integer getId_bimbingan() { 
        return id_bimbingan; 
    }
    public void setId_bimbingan(Integer id_bimbingan) { 
        this.id_bimbingan = id_bimbingan; 
    }

    public Boolean getIs_bimbingan() { 
        return is_bimbingan; 
    }
    public void setIs_bimbingan(Boolean is_bimbingan) { 
        this.is_bimbingan = is_bimbingan; 
    }

    public String getLokasi() { 
        return lokasi; 
    }
    public void setLokasi(String lokasi) { 
        this.lokasi = lokasi; 
    }

    public String getHari() { 
        return hari; 
    }
    public void setHari(String hari) { 
        this.hari = hari; 
    }

    public LocalTime getWaktu() { 
        return waktu; 
    }
    public void setWaktu(LocalTime waktu) { 
        this.waktu = waktu; 
    }

    public Integer getId_permintaan() { 
        return id_permintaan; 
    }
    public void setId_permintaan(Integer id_permintaan) { 
        this.id_permintaan = id_permintaan; 
    }
}

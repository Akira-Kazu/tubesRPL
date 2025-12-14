package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(MengajarId.class)
@Table(name = "mengajar")
public class Mengajar {

    @Id
    @Column(name = "email_dosen")
    private String emailDosen;

    @Id
    @Column(name = "id_mk")
    private Long idMk;

    @ManyToOne
    @JoinColumn(name = "email_dosen", insertable = false, updatable = false)
    private Pengguna dosen;

    @ManyToOne
    @JoinColumn(name = "id_mk", insertable = false, updatable = false)
    private MataKuliah mataKuliah;
}

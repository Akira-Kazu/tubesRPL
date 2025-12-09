package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(TerdaftarId.class)
@Table(name = "terdaftar")
public class Terdaftar {

    @Id
    @Column(name = "email")
    private String email; // HARUS sama dengan field di TerdaftarId

    @Id
    @Column(name = "id_mk")
    private Long idMk; // HARUS sama dengan field di TerdaftarId
}

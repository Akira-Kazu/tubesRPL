package com.example.demo.Entity;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalTime;
@Data // otomatis membuat getter, setter, toString, equals, hashCode
@Entity
@Table(name = "bimbingan")
public class Bimbingan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBimbingan;

    @Column(name = "is_bimbingan")
    private Boolean isBimbingan = true;

    private String lokasi;

    private String hari;

    private LocalTime waktu;

}

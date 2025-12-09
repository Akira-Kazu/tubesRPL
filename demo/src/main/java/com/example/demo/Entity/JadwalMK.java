package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "jadwal_mk")
public class JadwalMK {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_mk", nullable = false)
    private MataKuliah mataKuliah;

    @Column(name = "hari", nullable = false)
    private String hari;  // contoh: "Senin", "Selasa", dll

    @Column(name = "waktu", nullable = false)
    private LocalTime waktu;  // jam mulai

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;  // jam selesai

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JadwalMK that = (JadwalMK) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

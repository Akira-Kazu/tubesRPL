package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "mata_kuliah")
public class MataKuliah {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mk")
    private Long id;

    @Column(name = "nama_mk", nullable = false)
    private String namaMK;

    // Relasi ke jadwal (multi-sesi)
    @OneToMany(mappedBy = "mataKuliah", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JadwalMK> jadwal;

    // Relasi ke mahasiswa melalui tabel terdaftar
    @ManyToMany
    @JoinTable(
            name = "terdaftar",
            joinColumns = @JoinColumn(name = "id_mk"),
            inverseJoinColumns = @JoinColumn(name = "email")
    )
    private Set<Pengguna> mahasiswa;

    // Method untuk mengakses semua sesi jadwal
    public Set<JadwalMK> getJadwalSesi() {
        return this.jadwal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MataKuliah that = (MataKuliah) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(namaMK, that.namaMK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, namaMK);
    }

}




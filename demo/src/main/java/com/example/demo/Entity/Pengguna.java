package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "pengguna")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pengguna {

    @Id
    private String email;

    private String password;

    private String nama;

    private Integer role;  // 1 = Mahasiswa, 2 = Dosen, 3 = Admin

    // -------------------
    // Relasi untuk mahasiswa: mata kuliah yang diambil
    // -------------------
    @ManyToMany
    @JoinTable(
            name = "terdaftar",
            joinColumns = @JoinColumn(name = "email"),
            inverseJoinColumns = @JoinColumn(name = "id_mk")
    )
    private Set<MataKuliah> mataKuliah;

    // -------------------
    // Relasi untuk dosen: mata kuliah yang diajarkan
    // -------------------
    @ManyToMany
    @JoinTable(
            name = "mengajar",
            joinColumns = @JoinColumn(name = "email_dosen"),
            inverseJoinColumns = @JoinColumn(name = "id_mk")
    )
    private Set<MataKuliah> mengajar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pengguna that = (Pengguna) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @OneToOne(mappedBy = "mahasiswa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TugasAkhir tugasAkhir;

}

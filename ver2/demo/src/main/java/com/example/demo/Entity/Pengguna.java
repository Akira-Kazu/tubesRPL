package com.example.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
}

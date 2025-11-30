package com.example.demo.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pengguna")
public class Pengguna {

    @Id
    private String email;

    private String password;

    private String nama;

    private Integer role; // 1 = Mhs, 2 = Dosen, 3 = Admin

    public Pengguna() {}

    public Pengguna(String email, String password, String nama, Integer role) {
        this.email = email;
        this.password = password;
        this.nama = nama;
        this.role = role;
    }

    // GETTER & SETTER
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
}

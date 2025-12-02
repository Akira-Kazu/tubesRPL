package com.example.demo.Admin;

import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.PengajuanDosen;
import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.PengajuanDosenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PengajuanDosenRepository pengajuanDosenRepository;


    // ================================================================
    // PENGAJUAN DOSEN
    // ================================================================
    public List<PengajuanDosen> getPengajuanDosen() {
        return pengajuanDosenRepository.findAll();
    }


    // ================================================================
    // USER MANAGEMENT
    // ================================================================
    public List<Pengguna> getAllUsers() {
        return penggunaRepository.findAll();
    }

    public Pengguna getUserByEmail(String email) {
        return penggunaRepository.findById(email).orElse(null);
    }

    public Pengguna addUser(Pengguna user) {
        return penggunaRepository.save(user);
    }

    public void deleteUser(String email) {
        penggunaRepository.deleteById(email);
    }


    // ================================================================
    // ROLE FILTER
    // ================================================================
    public List<Pengguna> getMahasiswa() {
        return penggunaRepository.findByRole(1); // role 1 = mahasiswa
    }

    public List<Pengguna> getDosen() {
        return penggunaRepository.findByRole(2); // role 2 = dosen
    }
}

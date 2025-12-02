package com.example.demo.Admin;

import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.Bimbingan;
import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.BimbinganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private PenggunaRepository penggunaRepository;

   @Autowired
    private BimbinganRepository bimbinganRepository;


    // ================================================================
    // PENGAJUAN DOSEN
    // ================================================================
 public List<Bimbingan> getPengajuanDosen() {
        return bimbinganRepository.findAll(); // sebelumnya repository pengajuan dosen
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

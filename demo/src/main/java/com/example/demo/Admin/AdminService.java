package com.example.demo.Admin;

import com.example.demo.Entity.Pengguna;
import com.example.demo.Repository.PenggunaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final PenggunaRepository penggunaRepository;

    public AdminService(PenggunaRepository penggunaRepository) {
        this.penggunaRepository = penggunaRepository;
    }

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
}

package com.example.demo.service;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Repository.BimbinganRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BimbinganService {

    private final BimbinganRepository bimbinganRepository;

    // Gunakan constructor injection lebih direkomendasikan daripada @Autowired di field
    @Autowired
    public BimbinganService(BimbinganRepository bimbinganRepository) {
        this.bimbinganRepository = bimbinganRepository;
    }

    // Ambil semua bimbingan
    public List<Bimbingan> getAllBimbingan() {
        return bimbinganRepository.findAll();
    }

    // Simpan atau update bimbingan
    public Bimbingan saveBimbingan(Bimbingan bimbingan) {
        return bimbingan;
    }
}

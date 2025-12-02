package com.example.demo.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.application.pota.entity.Bimbingan;
import com.application.pota.repository.BimbinganRepository

@Service
public class BimbinganService {
    @Autowired
    private BimbinganRepository repository;

    public List<Bimbingan> getAllBimbingan() {
        return repository.findAll();
    }
}
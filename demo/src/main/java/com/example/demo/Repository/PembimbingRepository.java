package com.example.demo.Repository;

import com.example.demo.Entity.Pembimbing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PembimbingRepository extends JpaRepository<Pembimbing, Long> {

    // Method ini PENTING!
    // Dipanggil di BimbinganService: getProgressByDosen
    // Fungsinya: Mencari siapa saja mahasiswa yang dibimbing oleh dosen dengan email X
    List<Pembimbing> findByDosen_Email(String email);

}
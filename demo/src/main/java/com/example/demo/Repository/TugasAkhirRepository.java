package com.example.demo.Repository;

import com.example.demo.Entity.TugasAkhir;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Integer adalah tipe data Primary Key dari TugasAkhir (idTa)
public interface TugasAkhirRepository extends JpaRepository<TugasAkhir, Integer> {

    // --- MAGIC METHOD ---
    // Spring Boot otomatis menerjemahkan nama method ini menjadi SQL:
    // "SELECT * FROM tugas_akhir WHERE email_mahasiswa = [email]"
    // Syaratnya: Di Entity TugasAkhir harus ada field bernama 'mahasiswa'
    //            dan di dalam 'mahasiswa' ada field 'email'.
    Optional<TugasAkhir> findByMahasiswa_Email(String email);

}
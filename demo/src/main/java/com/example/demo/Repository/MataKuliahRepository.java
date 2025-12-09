package com.example.demo.Repository;

import com.example.demo.Entity.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface MataKuliahRepository extends JpaRepository<MataKuliah, Long> {

    // Ambil semua MataKuliah yang diambil mahasiswa tertentu
    @Query("SELECT m FROM MataKuliah m JOIN m.mahasiswa ms WHERE ms.email = :emailMahasiswa")
    List<MataKuliah> findByMahasiswaEmail(@Param("emailMahasiswa") String emailMahasiswa);
}

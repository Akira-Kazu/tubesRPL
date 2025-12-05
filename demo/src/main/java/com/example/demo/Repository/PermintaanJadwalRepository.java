package com.example.demo.Repository;

import com.example.demo.Entity.PermintaanJadwal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermintaanJadwalRepository extends JpaRepository<PermintaanJadwal, Long> {
}

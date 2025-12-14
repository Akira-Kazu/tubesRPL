package com.example.demo.Repository;

import com.example.demo.Entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotifikasiRepository extends JpaRepository<Notifikasi, Long> {
    // Ambil notif berdasarkan email, urutkan dari yang paling baru
    List<Notifikasi> findByEmailUserOrderByWaktuDibuatDesc(String emailUser);
}
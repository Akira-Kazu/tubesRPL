package com.example.demo.Repository;

import com.example.demo.Entity.PermintaanJadwal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PermintaanJadwalRepository extends JpaRepository<PermintaanJadwal, Long> {

     // Karena email ada di entity Pengguna dan field relasinya adalah "dosen"
    List<PermintaanJadwal> findByDosen_Email(String email);

         // Karena email ada di entity Pengguna dan field relasinya adalah "dosen"
    List<PermintaanJadwal> findByMahasiswa_Email(String email);

}

package com.example.demo.Repository;

import com.example.demo.Entity.PermintaanJadwal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PermintaanJadwalRepository extends JpaRepository<PermintaanJadwal, Long> {

     // Karena email ada di entity Pengguna dan field relasinya adalah "dosen"
    List<PermintaanJadwal> findByDosen_Email(String email);

         // Karena email ada di entity Pengguna dan field relasinya adalah "dosen"
    List<PermintaanJadwal> findByMahasiswa_Email(String email);

    @Query("""
    SELECT p 
    FROM PermintaanJadwal p 
    WHERE p.dosen.email = :emailDosen
      AND p.tanggal < CURRENT_DATE
      AND p.status = 'Approved'
    ORDER BY p.tanggal DESC
""")
List<PermintaanJadwal> getRiwayatDosen(@Param("emailDosen") String emailDosen);

    List<PermintaanJadwal> findAllByMahasiswa_EmailOrderByIdDesc(String email);

    List<PermintaanJadwal> findAllByMahasiswa_EmailAndStatus(String email, String status);


}

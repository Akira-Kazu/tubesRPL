package com.example.demo.Repository;

import com.example.demo.Entity.Bimbingan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.Entity.PermintaanJadwal;

@Repository
public interface BimbinganRepository extends JpaRepository<Bimbingan, Long> {

    // ambil bimbingan berdasarkan email dosen
    List<Bimbingan> findByPermintaanJadwal_Dosen_Email(String email);
    List<Bimbingan> findByPermintaanJadwal_Mahasiswa_Email(String email);
    // Ambil list bimbingan berdasarkan tanggal permintaan
    List<Bimbingan> findByPermintaanJadwal_Tanggal(LocalDate tanggal);

    // Cari bimbingan berdasarkan Permintaan Jadwal
    Bimbingan findByPermintaanJadwal(PermintaanJadwal permintaan);

    // Tambahan (opsional): kalau kamu butuh by id permintaan
    Bimbingan findByPermintaanJadwal_Id(Long id);

    List<Bimbingan> findByPermintaanJadwal_Mahasiswa_EmailAndHari(String email, String hari);

    @Query("SELECT COUNT(b) FROM Bimbingan b " +
            "WHERE b.tugasAkhir.idTa = :idTa " +
            "AND b.statusRealisasi = :status " +
            "AND b.tanggalWaktuRealisasi < :batasWaktu") // Kondisi kunci untuk batas waktu
    Long countCompletedBeforeDate(
            @Param("idTa") Integer idTa,
            @Param("status") String status,
            @Param("batasWaktu") LocalDateTime batasWaktu // Menggunakan LocalDateTime
    );

    long countByTugasAkhir_IdTaAndStatusRealisasi(Integer idTa, String statusSelesai);
}

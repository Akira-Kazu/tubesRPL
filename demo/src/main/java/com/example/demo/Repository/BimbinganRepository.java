package com.example.demo.Repository;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.PermintaanJadwal;
import org.springframework.data.jpa.repository.JpaRepository;
// PENTING: Jangan sampai lupa import Query dan Param ini
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BimbinganRepository extends JpaRepository<Bimbingan, Long> {

    // --- QUERY STANDAR (BAWAAN JPA) ---

    List<Bimbingan> findByPermintaanJadwal_Dosen_Email(String email);

    List<Bimbingan> findByPermintaanJadwal_Mahasiswa_Email(String email);

    List<Bimbingan> findByPermintaanJadwal_Tanggal(LocalDate tanggal);

    Bimbingan findByPermintaanJadwal(PermintaanJadwal permintaan);

    Bimbingan findByPermintaanJadwal_Id(Long id);

    List<Bimbingan> findByPermintaanJadwal_Mahasiswa_EmailAndHari(String email, String hari);

    // --- QUERY CUSTOM (PAKAI @QUERY) ---

    @Query("SELECT COUNT(b) FROM Bimbingan b " +
            "WHERE b.tugasAkhir.idTa = :idTa " +
            "AND b.statusRealisasi = :status " +
            "AND b.tanggalWaktuRealisasi < :batasWaktu")
    Long countCompletedBeforeDate(
            @Param("idTa") Integer idTa,
            @Param("status") String status,
            @Param("batasWaktu") LocalDateTime batasWaktu
    );

    long countByTugasAkhir_IdTaAndStatusRealisasi(Integer idTa, String statusSelesai);

    // ==========================================================
    // BAGIAN INI YANG BIKIN ERROR TADI
    // Pastikan @Query ada di atas method findHistorySelesai
    // ==========================================================
    @Query("SELECT b FROM Bimbingan b " +
            "WHERE b.permintaanJadwal.mahasiswa.email = :emailMhs " +
            "AND b.permintaanJadwal.dosen.email = :emailDosen " +
            "AND b.statusRealisasi = 'Selesai'")
    List<Bimbingan> findHistorySelesai(@Param("emailMhs") String emailMhs,
                                       @Param("emailDosen") String emailDosen);
}
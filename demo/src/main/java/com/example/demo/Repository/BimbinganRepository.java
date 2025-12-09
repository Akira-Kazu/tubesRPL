package com.example.demo.Repository;

import com.example.demo.Entity.Bimbingan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    List<Bimbingan> findByMahasiswaEmailAndHari(String emailMahasiswa, String hari);

}

package com.example.demo.service;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Entity.TugasAkhir;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BimbinganService {

    private final BimbinganRepository bimbinganRepository;
    private final PermintaanJadwalRepository permintaanJadwalRepository;

    @Autowired
    public BimbinganService(BimbinganRepository bimbinganRepository, PermintaanJadwalRepository permintaanJadwalRepository) {
        this.bimbinganRepository = bimbinganRepository;
        this.permintaanJadwalRepository = permintaanJadwalRepository;
    }

    public Bimbingan getById(Long id) {
        return bimbinganRepository.findById(id).orElse(null);
    }
 public List<Bimbingan> getBimbinganUntukDosen(String email) {
    return bimbinganRepository.findByPermintaanJadwal_Dosen_Email(email);
}



    public List<Bimbingan> getBimbinganUntukMahasiswa(String email) {
        return bimbinganRepository.findByPermintaanJadwal_Mahasiswa_Email(email);
    }


    public void setKomentar(Long idBimbingan, String komentar) {
        Bimbingan b = bimbinganRepository.findById(idBimbingan).orElse(null);
        if (b != null) {
            b.setKomentarDosen(komentar);
            bimbinganRepository.save(b);
        }
    }

    public Bimbingan getByPermintaanId(Long idPermintaan) {
        return bimbinganRepository.findByPermintaanJadwal_Id(idPermintaan);
                 // atau handle sesuai kebutuhan
    }

    // Simpan bimbingan baru
    public Bimbingan saveBimbingan(Bimbingan bimbingan) {
        return bimbinganRepository.save(bimbingan);
    }

    // Hapus bimbingan berdasarkan id
public void deleteBimbingan(Long idBimbingan) {
    bimbinganRepository.deleteById(idBimbingan);
}


    public Map<String, List<Bimbingan>> getBimbinganPerHari(String emailMahasiswa) {
        List<Bimbingan> bimbinganList = getBimbinganUntukMahasiswa(emailMahasiswa);
        Map<String, List<Bimbingan>> jadwalPerHari = new HashMap<>();
        for (Bimbingan bimbingan : bimbinganList) {
            String hari = bimbingan.getHari();
            jadwalPerHari.computeIfAbsent(hari, k -> new ArrayList<>())
                    .add(bimbingan);
        }
        return jadwalPerHari;
    }
    // Menggunakan idBimbingan karena Entitas Bimbingan sudah dibuat saat Approved
    public Bimbingan realisasiSesiBimbingan(Long idBimbingan, String komentarDosen, LocalDateTime waktuRealisasi) {

        // 1. Cari Bimbingan
        Bimbingan bimbingan = bimbinganRepository.findById(idBimbingan)
                .orElseThrow(() -> new RuntimeException("Sesi Bimbingan tidak ditemukan"));

        // 2. Akses Tugas Akhir Mahasiswa melalui relasi chaining
        // PermintaanJadwal -> Pengguna (Mahasiswa) -> TugasAkhir
        TugasAkhir ta = bimbingan.getPermintaanJadwal().getMahasiswa().getTugasAkhir();

        if (ta == null) {
            throw new RuntimeException("Error: Mahasiswa ini belum memiliki Tugas Akhir yang terdaftar.");
        }

        // 3. Set data REALISASI SESI
        bimbingan.setTugasAkhir(ta); // Menghubungkan Bimbingan ke Tugas Akhir
        bimbingan.setKomentarDosen(komentarDosen);
        bimbingan.setTanggalWaktuRealisasi(waktuRealisasi); // PENTING: Untuk pengecekan batas UTS/UAS
        bimbingan.setStatusRealisasi("Selesai"); // PENTING: Hanya sesi 'Selesai' yang dihitung

        // *OPSIONAL: Anda mungkin ingin mengubah status PermintaanJadwal menjadi 'Done' atau sejenisnya
        // bimbingan.getPermintaanJadwal().setStatus("Done");
        // permintaanRepo.save(bimbingan.getPermintaanJadwal());

        return bimbinganRepository.save(bimbingan);
    }



}

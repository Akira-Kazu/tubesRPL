package com.example.demo.service;

import com.example.demo.Repository.BimbinganRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PersyaratanService {

    // --- Konstanta Persyaratan Bisnis ---
    // Sesuaikan nilai-nilai ini sesuai aturan kampus Anda
    public static final int MIN_TOTAL_BIMBINGAN = 3;
    public static final int MIN_BIMBINGAN_UTS = 1;
    public static final int MIN_BIMBINGAN_UAS = 2;
    private static final String STATUS_SELESAI = "Selesai";

    private final BimbinganRepository bimbinganRepository;

    @Autowired
    public PersyaratanService(BimbinganRepository bimbinganRepository) {
        this.bimbinganRepository = bimbinganRepository;
    }

    /**
     * Mendapatkan batas waktu untuk Periode UTS (Contoh: 15 April)
     */
    public LocalDateTime getBatasWaktuUts() {
        // Tentukan batas waktu UTS (Anda mungkin mendapatkan ini dari tabel konfigurasi/settings)
        return LocalDateTime.of(2026, 4, 15, 0, 0);
    }

    /**
     * Mendapatkan batas waktu untuk Periode UAS/Sidang (Contoh: 1 November)
     */
    public LocalDateTime getBatasWaktuUas() {
        // Tentukan batas waktu UAS
        return LocalDateTime.of(2026, 11, 1, 0, 0);
    }

    /**
     * Mengecek apakah persyaratan bimbingan untuk sidang terpenuhi.
     * Menggunakan idTa karena penghitungan bimbingan terkait langsung dengan Tugas Akhir.
     */
    public boolean isSyaratSidangTerpenuhi(Integer idTa) {

        // 1. Cek Total Minimum Bimbingan
        long totalBimbingan = bimbinganRepository.countByTugasAkhir_IdTaAndStatusRealisasi(idTa, STATUS_SELESAI);
        if (totalBimbingan < MIN_TOTAL_BIMBINGAN) {
            System.out.println("Gagal Syarat Total: " + totalBimbingan + " < " + MIN_TOTAL_BIMBINGAN);
            return false;
        }

        // 2. Cek Minimum Sebelum UTS
        long bimbinganPraUts = bimbinganRepository.countCompletedBeforeDate(
                idTa, STATUS_SELESAI, getBatasWaktuUts());
        if (bimbinganPraUts < MIN_BIMBINGAN_UTS) {
            System.out.println("Gagal Syarat Pra-UTS: " + bimbinganPraUts + " < " + MIN_BIMBINGAN_UTS);
            return false;
        }

        // 3. Cek Minimum Sebelum UAS
        long bimbinganPraUas = bimbinganRepository.countCompletedBeforeDate(
                idTa, STATUS_SELESAI, getBatasWaktuUas());
        if (bimbinganPraUas < MIN_BIMBINGAN_UAS) {
            System.out.println("Gagal Syarat Pra-UAS: " + bimbinganPraUas + " < " + MIN_BIMBINGAN_UAS);
            return false;
        }

        return true;
    }

    /**
     * Menghitung total bimbingan Selesai untuk satu Tugas Akhir.
     */
    public long countTotalBimbingan(Integer idTa) {
        return bimbinganRepository.countByTugasAkhir_IdTaAndStatusRealisasi(idTa, STATUS_SELESAI);
    }

    /**
     * Menghitung bimbingan Selesai yang dilakukan sebelum batas waktu UTS.
     */
    public long countBimbinganPraUts(Integer idTa) {
        return bimbinganRepository.countCompletedBeforeDate(
                idTa, STATUS_SELESAI, getBatasWaktuUts());
    }

    /**
     * Menghitung bimbingan Selesai yang dilakukan sebelum batas waktu UAS.
     */
    public long countBimbinganPraUas(Integer idTa) {
        return bimbinganRepository.countCompletedBeforeDate(
                idTa, STATUS_SELESAI, getBatasWaktuUas());
    }

}
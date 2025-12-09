package com.example.demo.service;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.JadwalMK;
import com.example.demo.Entity.MataKuliah;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.Repository.MataKuliahRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class JadwalService {

    private final MataKuliahRepository mataKuliahRepo;
    private final BimbinganRepository bimbinganRepo;

    public JadwalService(MataKuliahRepository mataKuliahRepo,
                         BimbinganRepository bimbinganRepo) {
        this.mataKuliahRepo = mataKuliahRepo;
        this.bimbinganRepo = bimbinganRepo;
    }

    /**
     * Mengecek apakah mahasiswa bisa mengajukan bimbingan pada tanggal & waktu tertentu
     * @param emailMahasiswa email mahasiswa
     * @param tanggal tanggal pengajuan
     * @param waktu jam mulai pengajuan
     * @return true jika tersedia, false jika bentrok
     */
    public boolean isAvailable(String emailMahasiswa, LocalDate tanggal, LocalTime waktu) {
        String hari = tanggal.getDayOfWeek().toString(); // MONDAY, TUESDAY, ...

        // 1. Ambil semua mata kuliah mahasiswa
        List<MataKuliah> mataKuliahList = mataKuliahRepo.findByMahasiswaEmail(emailMahasiswa);

        // 2. Ambil semua bimbingan mahasiswa di hari tersebut
        List<Bimbingan> bimbinganList = bimbinganRepo.findByMahasiswaEmailAndHari(emailMahasiswa, hari);

        LocalTime bimbinganStart = waktu;
        LocalTime bimbinganEnd = waktu.plusHours(1); // durasi bimbingan 1 jam

        // 3. Cek bentrok dengan setiap sesi JadwalMK dari mata kuliah yang diambil mahasiswa
        for (MataKuliah mk : mataKuliahList) {
            Set<JadwalMK> jadwalSesi = mk.getJadwalSesi(); // setiap sesi di JadwalMK
            for (JadwalMK sesi : jadwalSesi) {
                if (!sesi.getHari().equalsIgnoreCase(hari)) continue;

                LocalTime mkStart = sesi.getWaktu();
                LocalTime mkEnd = sesi.getEndTime();

                if (isOverlap(bimbinganStart, bimbinganEnd, mkStart, mkEnd)) {
                    return false; // bentrok dengan mata kuliah
                }
            }
        }

        // 4. Cek bentrok dengan bimbingan lain
        for (Bimbingan b : bimbinganList) {
            LocalTime bStart = b.getWaktu();
            LocalTime bEnd = bStart.plusHours(1);

            if (isOverlap(bimbinganStart, bimbinganEnd, bStart, bEnd)) {
                return false; // bentrok dengan bimbingan lain
            }
        }

        return true; // jadwal tersedia
    }

    // fungsi overlap
    private boolean isOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

}



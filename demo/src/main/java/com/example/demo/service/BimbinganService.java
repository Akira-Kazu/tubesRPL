package com.example.demo.service;

import com.example.demo.DTO.ProgressMhsDTO;
import com.example.demo.Entity.*;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.Repository.PembimbingRepository;
import com.example.demo.Repository.TugasAkhirRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BimbinganService {

    private final BimbinganRepository bimbinganRepository;
    private final PermintaanJadwalRepository permintaanJadwalRepository;
    private final PembimbingRepository pembimbingRepository;
    private final TugasAkhirRepository tugasAkhirRepository;
    @Autowired
    public BimbinganService(BimbinganRepository bimbinganRepository, PermintaanJadwalRepository permintaanJadwalRepository, PembimbingRepository pembimbingRepository, TugasAkhirRepository tugasAkhirRepository) {
        this.bimbinganRepository = bimbinganRepository;
        this.permintaanJadwalRepository = permintaanJadwalRepository;
        this.pembimbingRepository = pembimbingRepository; // 3. MASUKKAN KE VARIABEL
        this.tugasAkhirRepository = tugasAkhirRepository;
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

        Bimbingan bimbingan = bimbinganRepository.findById(idBimbingan)
                .orElseThrow(() -> new RuntimeException("Sesi Bimbingan tidak ditemukan"));

        TugasAkhir ta = bimbingan.getPermintaanJadwal().getMahasiswa().getTugasAkhir();

        if (ta == null) {
            throw new RuntimeException("Error: Mahasiswa ini belum memiliki Tugas Akhir yang terdaftar.");
        }

        bimbingan.setTugasAkhir(ta);
        bimbingan.setKomentarDosen(komentarDosen);
        bimbingan.setTanggalWaktuRealisasi(waktuRealisasi);
        bimbingan.setStatusRealisasi("Selesai");

        return bimbinganRepository.save(bimbingan);
    }


    // Method ini dipanggil saat TAB PROGRESS DIBUKA
    public List<ProgressMhsDTO> getProgressByDosen(String emailDosen) {

        // 1. Ambil daftar orangnya (Harry, Hermione, dll)
        List<Pembimbing> daftarBimbingan = pembimbingRepository.findByDosen_Email(emailDosen);

        List<ProgressMhsDTO> resultList = new ArrayList<>();

        // 2. Cek satu per satu
        for (Pembimbing p : daftarBimbingan) {
            Pengguna mhs = p.getMahasiswa();
            ProgressMhsDTO dto = new ProgressMhsDTO();

            // --> INI BUAT NGISI KOLOM "NAMA MAHASISWA"
            dto.setNamaMahasiswa(mhs.getNama());

            // --> INI BUAT NGISI KOLOM "JUDUL TA"
            Optional<TugasAkhir> taOpt = tugasAkhirRepository.findByMahasiswa_Email(mhs.getEmail());
            if (taOpt.isPresent()) dto.setJudulTA(taOpt.get().getJudul());

            // -------------------------------------------------------------
            // DISINI PERAN PENTING 'findHistorySelesai' !!!
            // Kalau method ini tidak ada, Anda tidak tahu Harry sudah berapa kali bimbingan.
            // -------------------------------------------------------------
            List<Bimbingan> history = bimbinganRepository.findHistorySelesai(mhs.getEmail(), emailDosen);

            // --> INI BUAT NGISI KOLOM "TOTAL BIMBINGAN" (Misal: 5 Kali)
            dto.setJumlahBimbingan(history.size());

            // --> INI BUAT NGISI KOLOM "TERAKHIR BIMBINGAN" (Misal: 2025-12-15)
            // Dia nyari tanggal paling baru dari list history tadi
            LocalDateTime lastDate = history.stream()
                    .map(Bimbingan::getTanggalWaktuRealisasi) // Ambil tanggalnya
                    .filter(Objects::nonNull)                 // Buang yang null (PENTING!)
                    .max(LocalDateTime::compareTo)            // Cari tanggal paling besar/baru
                    .orElse(null);                            // Kalau list kosong, hasil akhirnya null
            dto.setTanggalTerakhirBimbingan(lastDate);

            resultList.add(dto);
        }

        return resultList; // Data siap ditampilkan di HTML!
    }
}

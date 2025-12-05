package com.example.demo.Admin;

import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private BimbinganRepository bimbinganRepository;

    @Autowired
    private PermintaanJadwalRepository permintaanJadwalRepository;


    // ================================================================
    // PENGAJUAN BIMBINGAN (FITUR BARU)
    // ================================================================
    public void prosesPengajuan(
            String dosenEmail,
            String mahasiswaEmail,
            String lokasi,
            LocalDate tanggal,
            LocalTime waktu,
            String catatan
    ) {

        // 1. Permintaan Jadwal
        PermintaanJadwal p = new PermintaanJadwal();
        p.setTanggal(tanggal);
        p.setCatatan(catatan);
        p.setMahasiswa(penggunaRepository.findByEmail(mahasiswaEmail));
        p.setDosen(penggunaRepository.findByEmail(dosenEmail));

        PermintaanJadwal saved = permintaanJadwalRepository.save(p);

        // 2. Bimbingan
        Bimbingan b = new Bimbingan();
        b.setLokasi(lokasi);
        b.setWaktu(waktu);
        b.setHari(tanggal.getDayOfWeek().name());
        b.setPermintaan(saved); // RELASI baru

        bimbinganRepository.save(b);
    }


    // ================================================================
    // PENGAJUAN DOSEN (LIST BIMBINGAN)
    // ================================================================
    public List<Bimbingan> getPengajuanDosen() {
        return bimbinganRepository.findAll();
    }

    // ================================================================
    // USER MANAGEMENT
    // ================================================================
    public List<Pengguna> getAllUsers() {
        return penggunaRepository.findAll();
    }

    public Pengguna getUserByEmail(String email) {
        return penggunaRepository.findById(email).orElse(null);
    }

    public Pengguna addUser(Pengguna user) {
        return penggunaRepository.save(user);
    }

    public void deleteUser(String email) {
        penggunaRepository.deleteById(email);
    }

    // ================================================================
    // ROLE FILTER
    // ================================================================
    public List<Pengguna> getMahasiswa() {
        return penggunaRepository.findByRole(1);
    }

    public List<Pengguna> getDosen() {
        return penggunaRepository.findByRole(2);
    }
}

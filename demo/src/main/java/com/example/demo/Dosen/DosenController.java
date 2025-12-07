package com.example.demo.Dosen;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/dosen")
public class DosenController {

    @Autowired
    private PenggunaRepository penggunaRepository;
    @Autowired
private PermintaanJadwalRepository permintaanRepo;
@Autowired
private BimbinganRepository bimbinganRepo;

 @GetMapping
    public String dosenHome() {
        return "berandaDosen"; 
    }

    @GetMapping("/riwayat")
    public String riwayatBimbingan() {
        return "riwayatBimbinganDosen";
    }

    @GetMapping("/pengajuan")
    public String pengajuanBimbingan(Model model) {

        // Ambil semua mahasiswa
        model.addAttribute("mahasiswaList", penggunaRepository.findByRole(1));
        return "pengajuanFormDosen";
    }

@PostMapping("/pengajuan/create")
public String buatPengajuanDosen(
        @RequestParam("mahasiswa") String emailMahasiswa,
        @RequestParam("lokasi") String lokasi,
        @RequestParam("tanggal") String tanggal,
        @RequestParam("waktu") String waktu,
        @RequestParam(value = "catatan", required = false) String catatan,
        HttpSession session
) {
    Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
    Pengguna mahasiswa = penggunaRepository.findByEmail(emailMahasiswa);

    // 1️⃣ Simpan ke PermintaanJadwal
    PermintaanJadwal pengajuan = new PermintaanJadwal();
    pengajuan.setDosen(dosen);
    pengajuan.setMahasiswa(mahasiswa);
    pengajuan.setLokasi(lokasi);
    pengajuan.setTanggal(LocalDate.parse(tanggal));
    pengajuan.setWaktu(LocalTime.parse(waktu));
    pengajuan.setCatatan(catatan);
    pengajuan.setStatus("Approved");

    permintaanRepo.save(pengajuan);

    // 2️⃣ Buat Bimbingan otomatis
    Bimbingan bimbingan = new Bimbingan();
    bimbingan.setPermintaanJadwal(pengajuan); // relasi ke pengajuan
    bimbingan.setLokasi(lokasi);
    bimbingan.setHari(pengajuan.getTanggal().getDayOfWeek().toString()); // bisa disesuaikan
    bimbingan.setWaktu(LocalTime.parse(waktu));
    bimbingan.setIsBimbingan(true);

    bimbinganRepo.save(bimbingan);

    return "kelolaPengajuanDosen";
}


    @GetMapping("/kelola")
    public String kelolaPengajuan() {
        return "kelolaPengajuanDosen";
    }

    @GetMapping("/progress")
    public String progressTA() {
        return "progressTAMahasiswa"; // sementara pakai ini karena belum ada progress untuk dosen
    }

    @GetMapping("/jadwal")
    public String jadwalBimbingan() {
        return "JadwalDosen";
    }
}

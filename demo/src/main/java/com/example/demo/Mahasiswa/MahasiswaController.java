package com.example.demo.Mahasiswa;
import com.example.demo.Entity.*;
import com.example.demo.service.JadwalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.service.PermintaanJadwalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.service.BimbinganService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class MahasiswaController {

    @Autowired
    private JadwalService jadwalService;

    @Autowired
    private PermintaanJadwalService permintaanJadwalService;

    @Autowired
    private PermintaanJadwalRepository permintaanRepo;

    @Autowired
    private PenggunaRepository penggunaRepo;

    @Autowired
    private BimbinganService bimbinganService;

    @GetMapping("/mahasiswa")
    public String beranda(){
        return "BerandaMahasiswa";
    }

     @GetMapping("/menu")
    public String menu(){
        return "BerandaMahasiswa";
    }

    @GetMapping("/riwayat")
    public String riwayatBimbingan(HttpSession session, Model model) {

        // Ambil user dari session
        Pengguna mahasiswa = (Pengguna) session.getAttribute("loggedUser");
        if (mahasiswa == null) return "redirect:/login";

        String email = mahasiswa.getEmail();

        List<PermintaanJadwal> approvedRiwayat = permintaanJadwalService.getRiwayatApprovedUntukMahasiswa(email);

        model.addAttribute("riwayat", approvedRiwayat);
        model.addAttribute("inisialUser", mahasiswa.getNama().substring(0,1));

        return "riwayatBimbinganMahasiswa";
    }

    @GetMapping("/mahasiswa/detail/{id}")
    public String detailMahasiswa(@PathVariable("id") Long idPermintaan, Model model){
        Bimbingan bimbingan = bimbinganService.getByPermintaanId(idPermintaan);
        model.addAttribute("bimbingan", bimbingan);
        return "riwayatdetailBimbinganMahasiswa";
    }





    @GetMapping("/pengajuan")
    public String pengajuan(Model model) {
   model.addAttribute("dosenList", penggunaRepo.findByRole(2));

    return "pengajuanFormMahasiswa";
}

@PostMapping("/pengajuan/submit")
public String submitPengajuan(
        @RequestParam("dosenEmail") String emailDosen,
        @RequestParam("lokasi") String lokasi,
        @RequestParam("tanggal") String tanggal,
        @RequestParam("waktu") String waktu,
        @RequestParam(value = "catatan", required = false) String catatan,
        HttpSession session,
        Model model
) {
    Pengguna mahasiswa = (Pengguna) session.getAttribute("loggedUser");

    if (mahasiswa == null) return "redirect:/login";

    Pengguna dosen = penggunaRepo.findByEmail(emailDosen);

    LocalDate tanggalParsed = LocalDate.parse(tanggal);
    LocalTime waktuParsed = LocalTime.parse(waktu);

    // Cek jadwal bentrok
    if (!jadwalService.isAvailable(mahasiswa.getEmail(), tanggalParsed, waktuParsed)) {
        model.addAttribute("error", "Jadwal bentrok dengan MK atau bimbingan lain.");
        return "pengajuanFormMahasiswa"; // kembali ke halaman form
    }
    PermintaanJadwal p = new PermintaanJadwal();
    p.setMahasiswa(mahasiswa);
    p.setDosen(dosen);
    p.setLokasi(lokasi);
    p.setTanggal(LocalDate.parse(tanggal));
    p.setWaktu(LocalTime.parse(waktu));
    p.setCatatan(catatan);
    p.setStatus("Pending");

    permintaanRepo.save(p);

    return "redirect:/pengajuan?success=true";
}


    @GetMapping("/kelola")
    public String kelola() {
        return "kelolaPengajuanmahasiswa"; // templates/kelola.html
    }

    

    @GetMapping("/progress")
    public String progress() {
        return "progressTAMahasiswa"; // templates/progress.html
    }

    @GetMapping("/jadwal")
    public String jadwalMahasiswa(HttpSession session, Model model) {
        // 1. Ambil user dari session
        Pengguna mahasiswa = (Pengguna) session.getAttribute("loggedUser");
        if (mahasiswa == null) return "redirect:/login";

        // 2. Ambil semua mata kuliah mahasiswa
        Set<MataKuliah> mataKuliahSet = mahasiswa.getMataKuliah(); // pastikan ada relasi di Pengguna entity

        // 3. Ambil semua sesi jadwal dari setiap mata kuliah
        Map<String, List<JadwalMK>> jadwalPerHari = new HashMap<>();
        for (MataKuliah mk : mataKuliahSet) {
            for (JadwalMK sesi : mk.getJadwalSesi()) {
                jadwalPerHari
                        .computeIfAbsent(sesi.getHari(), k -> new ArrayList<>())
                        .add(sesi);
            }
        }

        // 4. Tambahkan data ke model
        model.addAttribute("jadwalPerHari", jadwalPerHari);
        model.addAttribute("inisialUser", mahasiswa.getNama().substring(0, 1));

        return "JadwalMahasiswa";
    }


     @GetMapping("/inbox")
    public String inbox() {
        return "inboxMahasiswa"; // templates/jadwal.html
    }
}

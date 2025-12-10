package com.example.demo.Mahasiswa;

import com.example.demo.Entity.*;
import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.service.BimbinganService;
import com.example.demo.service.JadwalService;
import com.example.demo.service.PermintaanJadwalService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // --- HALAMAN UTAMA (DASHBOARD) ---
    @GetMapping("/mahasiswa")
    public String beranda(HttpSession session, Model model) {
        Pengguna mhs = (Pengguna) session.getAttribute("loggedUser");
        if (mhs == null) return "redirect:/login";

        model.addAttribute("namaUser", mhs.getNama());

        // 1. Ambil Data Jadwal
        List<Bimbingan> listJadwal = bimbinganService.getBimbinganUntukMahasiswa(mhs.getEmail());
        model.addAttribute("listJadwal", listJadwal);

        // 2. HITUNG STATISTIK (LOGIKA DINAMIS)
        // Hitung berapa yang Approved (On Progress)
        int countProgress = permintaanRepo.findAllByMahasiswa_EmailAndStatus(mhs.getEmail(), "Approved").size();
        
        // Hitung berapa yang Pending (Waiting)
        int countPending = permintaanRepo.findAllByMahasiswa_EmailAndStatus(mhs.getEmail(), "Pending").size();
        
        // Hitung yang Finished (Sementara 0 atau logika lain)
        int countFinished = 0; 

        // Kirim ke HTML
        model.addAttribute("taOnProgress", countProgress);
        model.addAttribute("taNotStarted", countPending); // Anggap aja Pending = Belum mulai/nunggu
        model.addAttribute("taFinished", countFinished);

        return "BerandaMahasiswa";
    }

    // --- REDIRECT MENU ---
    @GetMapping("/menu")
    public String menu() { return "redirect:/mahasiswa"; }

    @GetMapping("/dashboard")
    public String dashboard() { return "redirect:/mahasiswa"; }

    // --- RIWAYAT ---
    @GetMapping("/riwayat")
    public String riwayat(HttpSession session, Model model) {
        Pengguna mhs = (Pengguna) session.getAttribute("loggedUser");
        if (mhs == null) return "redirect:/login";

        List<PermintaanJadwal> approvedRiwayat = permintaanJadwalService.getRiwayatApprovedUntukMahasiswa(mhs.getEmail());
        model.addAttribute("riwayat", approvedRiwayat);
        
        return "riwayatBimbinganMahasiswa";
    }

    // --- DETAIL RIWAYAT ---
    @GetMapping("/mahasiswa/detail/{id}")
    public String detailMahasiswa(@PathVariable("id") Long idPermintaan, Model model) {
        Bimbingan bimbingan = bimbinganService.getByPermintaanId(idPermintaan);
        model.addAttribute("bimbingan", bimbingan);
        return "riwayatdetailBimbinganMahasiswa"; // Pastikan file HTML ini ada
    }

    // --- FORM PENGAJUAN ---
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

    // --- MENU LAIN ---
    @GetMapping("/kelola")
    public String kelola() { return "kelolaPengajuanmahasiswa"; }

    @GetMapping("/progress")
    public String progress() { return "progressTAMahasiswa"; }

    @GetMapping("/inbox")
    public String inbox() { return "inboxMahasiswa"; }

    // --- JADWAL ---
    @Transactional
    @GetMapping("/jadwal")
    public String jadwalMahasiswa(HttpSession session, Model model) {
        Pengguna sessionUser = (Pengguna) session.getAttribute("loggedUser");
        if (sessionUser == null) return "redirect:/login";

        Pengguna mahasiswa = penggunaRepo.findByEmail(sessionUser.getEmail());
        Map<String, List<Object>> jadwalPerHari = new HashMap<>();

        // Jadwal MK
        if (mahasiswa.getMataKuliah() != null) {
            for (MataKuliah mk : mahasiswa.getMataKuliah()) {
                if (mk.getJadwal() != null) {
                    for (JadwalMK sesi : mk.getJadwal()) {
                        jadwalPerHari.computeIfAbsent(sesi.getHari().toUpperCase(), k -> new ArrayList<>()).add(sesi);
                    }
                }
            }
        }

        // Jadwal Bimbingan
        List<Bimbingan> bimbinganList = bimbinganService.getBimbinganUntukMahasiswa(mahasiswa.getEmail());
        if (bimbinganList != null) {
            for (Bimbingan b : bimbinganList) {
                String hari = b.getHari() != null ? b.getHari().toUpperCase() : "UNKNOWN";
                jadwalPerHari.computeIfAbsent(hari, k -> new ArrayList<>()).add(b);
            }
        }

        model.addAttribute("jadwalPerHari", jadwalPerHari);
        model.addAttribute("nama", mahasiswa.getNama());

        return "JadwalMahasiswa";
    }
}
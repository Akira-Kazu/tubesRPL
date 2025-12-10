package com.example.demo.Dosen;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.Repository.PenggunaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/dosen")
public class DosenController {

    @Autowired
    private PermintaanJadwalRepository permintaanRepo;

    @Autowired
    private BimbinganRepository bimbinganRepo;

    @Autowired
    private PenggunaRepository penggunaRepo;

    // --- DASHBOARD (BERANDA) ---
    @GetMapping
    public String dosenHome(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        model.addAttribute("namaUser", dosen.getNama());

        // 1. DATA TABEL: Ambil semua jadwal bimbingan milik dosen ini
        // (Method ini sudah ada di BimbinganRepository kamu)
        List<Bimbingan> listJadwal = bimbinganRepo.findByPermintaanJadwal_Dosen_Email(dosen.getEmail());
        model.addAttribute("listJadwal", listJadwal);

        // 2. DATA STATISTIK
        // Hitung Pengajuan Baru (Status = Pending)
        int pendingCount = permintaanRepo.findByDosen_EmailAndStatus(dosen.getEmail(), "Pending").size();
        
        // Hitung Total Bimbingan (Yang sudah masuk tabel Bimbingan)
        int bimbinganCount = listJadwal.size();
        
        // Hitung Catatan (Contoh: Bimbingan yang kolom komentarnya sudah diisi)
        long catatanCount = listJadwal.stream()
                            .filter(b -> b.getKomentarDosen() != null && !b.getKomentarDosen().isEmpty())
                            .count();

        model.addAttribute("pengajuanBaru", pendingCount);
        model.addAttribute("bimbinganTotal", bimbinganCount);
        model.addAttribute("catatanDiberikan", catatanCount);

        return "berandaDosen";
    }

    // ... (SISA METHOD LAIN TETAP SAMA SEPERTI SEBELUMNYA) ...
    // Pastikan method lain seperti /kelola, /riwayat, approve, reject tetap ada di sini.
    // Kalau mau saya kirim full code DosenController lagi, bilang aja.
    
    // --- HALAMAN KELOLA PENGAJUAN (Hanya Pending) ---
    @GetMapping("/kelola")
    public String kelolaPengajuan(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        List<PermintaanJadwal> listPending = permintaanRepo.findByDosen_EmailAndStatus(dosen.getEmail(), "Pending");
        model.addAttribute("pengajuanList", listPending);
        return "kelolaPengajuanDosen";
    }

    // --- HALAMAN RIWAYAT ---
    @GetMapping("/riwayat")
    public String riwayatBimbingan(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        List<PermintaanJadwal> history = permintaanRepo.findByDosen_EmailAndStatusNot(dosen.getEmail(), "Pending");
        model.addAttribute("riwayatList", history);
        return "riwayatBimbinganDosen";
    }

    // --- APPROVE ---
    @GetMapping("/pengajuan/approve/{id}")
    public String approvePengajuan(@PathVariable Long id) {
        PermintaanJadwal p = permintaanRepo.findById(id).orElse(null);
        if (p != null) {
            p.setStatus("Approved");
            permintaanRepo.save(p);

            // Buat Jadwal Bimbingan Real
            Bimbingan b = new Bimbingan();
            b.setPermintaanJadwal(p);
            b.setLokasi(p.getLokasi());
            b.setHari(p.getTanggal().getDayOfWeek().toString());
            b.setWaktu(p.getWaktu());
            b.setIsBimbingan(true);
            bimbinganRepo.save(b);
        }
        return "redirect:/dosen/kelola";
    }

    // --- REJECT ---
    @GetMapping("/pengajuan/reject/{id}")
    public String rejectPengajuan(@PathVariable Long id) {
        PermintaanJadwal p = permintaanRepo.findById(id).orElse(null);
        if (p != null) {
            p.setStatus("Rejected");
            permintaanRepo.save(p);
        }
        return "redirect:/dosen/kelola";
    }
    
    // --- FORM PENGAJUAN ---
    @GetMapping("/pengajuan")
    public String pengajuanBimbingan(Model model) {
        model.addAttribute("mahasiswaList", penggunaRepo.findByRole(1));
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
        Pengguna mahasiswa = penggunaRepo.findByEmail(emailMahasiswa);

        PermintaanJadwal pengajuan = new PermintaanJadwal();
        pengajuan.setDosen(dosen);
        pengajuan.setMahasiswa(mahasiswa);
        pengajuan.setLokasi(lokasi);
        pengajuan.setTanggal(LocalDate.parse(tanggal));
        pengajuan.setWaktu(LocalTime.parse(waktu));
        pengajuan.setCatatan(catatan);
        pengajuan.setStatus("Approved"); // Dosen yang buat, otomatis approved
        permintaanRepo.save(pengajuan);

        Bimbingan b = new Bimbingan();
        b.setPermintaanJadwal(pengajuan);
        b.setLokasi(lokasi);
        b.setHari(pengajuan.getTanggal().getDayOfWeek().toString());
        b.setWaktu(LocalTime.parse(waktu));
        b.setIsBimbingan(true);
        bimbinganRepo.save(b);

        return "redirect:/dosen/riwayat";
    }

    @GetMapping("/jadwal")
    public String jadwalDosen() { return "JadwalDosen"; }
    
    @GetMapping("/progress")
    public String progressTA() { return "progressTAMahasiswa"; }
}
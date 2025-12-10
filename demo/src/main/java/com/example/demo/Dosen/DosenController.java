package com.example.demo.Dosen;

import org.springframework.web.bind.annotation.*; // Menggunakan RestController, RequestMapping, dll.
import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.Repository.PenggunaRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private PermintaanJadwalService permintaanJadwalService;

    @Autowired
    private PenggunaRepository penggunaRepository;
    @Autowired
    private PermintaanJadwalRepository permintaanRepo;
    @Autowired
    private BimbinganRepository bimbinganRepo;
    @Autowired
    private PenggunaRepository penggunaRepo;

    // --- HOME & NAVIGASI ---

    @GetMapping
    public String dosenHome() {
        return "berandaDosen";
    }

    @GetMapping("/jadwal")
    public String jadwalBimbingan() {
        return "JadwalDosen";
    }

    @GetMapping("/progress")
    public String progressTA() {
        // Ini adalah tempat di mana nanti Anda akan mengintegrasikan PersyaratanService
        return "progressTAMahasiswa";
    }

    // --- RIWAYAT DAN DETAIL BIMBINGAN ---

    // Dalam DosenController.java

    @GetMapping("/riwayat")
    public String riwayatBimbingan(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        String emailDosen = dosen.getEmail();

        // 1. Ambil semua bimbingan untuk Dosen ini
        List<Bimbingan> riwayat = bimbinganService.getBimbinganUntukDosen(emailDosen);

        // 2. Filter: Tampilkan yang (1) Approved DIJADWALKAN ATAU (2) Sudah SELESAI
        List<Bimbingan> approvedOrRealized = riwayat.stream()
                .filter(b -> b.getPermintaanJadwal() != null
                        // Kondisi 1: Status Permintaan adalah Approved (dijadwalkan)
                        && (b.getPermintaanJadwal().getStatus().equalsIgnoreCase("Approved")
                        // Kondisi 2: Status Realisasi (di entitas Bimbingan) adalah Selesai
                        || (b.getStatusRealisasi() != null && b.getStatusRealisasi().equalsIgnoreCase("Selesai"))
                ))
                .toList();

        model.addAttribute("riwayat", approvedOrRealized);
        return "riwayatBimbinganDosen";
    }

    @GetMapping("/riwayat/detail/{id}")
    public String detailRiwayat(@PathVariable Long id, Model model) {
        Bimbingan bimbingan = bimbinganService.getById(id);
        model.addAttribute("bimbingan", bimbingan);
        return "riwayatDetailDosen";
    }

    // --- PENCATATAN REALISASI SESI (KUNCI PELACAKAN TA) ---

    @PostMapping("/riwayat/realisasi/{idBimbingan}")
    public String realisasiBimbingan(
            @PathVariable Long idBimbingan,
            @RequestParam String komentarRealisasi,
            Model model) {

        // Waktu realisasi diambil saat ini
        LocalDateTime waktuSelesai = LocalDateTime.now();

        try {
            // Memanggil service untuk mencatat status Selesai, Waktu Realisasi, dan menghubungkan ke TA
            bimbinganService.realisasiSesiBimbingan(idBimbingan, komentarRealisasi, waktuSelesai);

            // Perlu ditambahkan: Update status di PermintaanJadwal jika diperlukan (misalnya dari Approved ke Completed)

            return "redirect:/dosen/riwayat/detail/" + idBimbingan + "?success=true";

        } catch (RuntimeException e) {
            // Handle error, misalnya Mahasiswa tidak punya Tugas Akhir
            return "redirect:/dosen/riwayat/detail/" + idBimbingan + "?error=" + e.getMessage();
        }
    }

    // --- UPDATE KOMENTAR (Jika hanya ingin update komentar tanpa realisasi) ---

    @PostMapping("/riwayat/update-komentar")
    public String updateKomentar(@RequestParam Long id,
                                 @RequestParam String komentar) {

        Bimbingan b = bimbinganService.getById(id);
        if (b != null) {
            b.setKomentarDosen(komentar);
            bimbinganService.saveBimbingan(b);
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


    // --- PENGAJUAN (DOSEN-INITIATED) ---

    @GetMapping("/pengajuan")
    public String pengajuanBimbingan(Model model) {
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
            HttpSession session) {

        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

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

        // 2️⃣ Buat Bimbingan otomatis (sebagai placeholder jadwal yang disetujui)
        Bimbingan bimbingan = new Bimbingan();
        bimbingan.setPermintaanJadwal(pengajuan);
        bimbingan.setLokasi(lokasi);
        bimbingan.setHari(pengajuan.getTanggal().getDayOfWeek().toString());
        bimbingan.setWaktu(LocalTime.parse(waktu));
        bimbingan.setIsBimbingan(true);
        // CATATAN: tanggalWaktuRealisasi dan statusRealisasi tetap NULL/default sampai sesi selesai!
        bimbinganRepo.save(bimbingan);

        return "redirect:/dosen/riwayat"; // Redirect ke riwayat untuk melihat jadwal yang dibuat
    }

    // --- KELOLA PENGAJUAN (MAHASISWA-INITIATED) ---

    @GetMapping("/kelola") // Menggunakan salah satu untuk memetakan ke halaman kelola
    public String listPengajuanPending(Model model) {
        // Asumsi: Dosen hanya melihat pengajuan yang diarahkan ke dia (perlu filter by Dosen email)
        // Saat ini, semua pending dilihat. Gunakan filter Dosen yang login untuk skalabilitas.

        List<PermintaanJadwal> pengajuanList = permintaanRepo.findAll()
                .stream()
                .filter(p -> "Pending".equals(p.getStatus()))
                .toList();

        model.addAttribute("pengajuanList", pengajuanList);
        return "kelolaPengajuanDosen";
    }


    @GetMapping("/pengajuan/approve/{id}")
    public String approvePengajuan(@PathVariable Long id) {
        PermintaanJadwal pengajuan = permintaanRepo.findById(id).orElse(null);
        if (pengajuan != null) {
            pengajuan.setStatus("Approved");
            permintaanRepo.save(pengajuan);

            // Buat Entitas Bimbingan sebagai placeholder jadwal
            Bimbingan bimbingan = new Bimbingan();
            bimbingan.setPermintaanJadwal(pengajuan);
            bimbingan.setLokasi(pengajuan.getLokasi());
            bimbingan.setHari(pengajuan.getTanggal().getDayOfWeek().toString());
            bimbingan.setWaktu(pengajuan.getWaktu());
            bimbingan.setIsBimbingan(true);

            bimbinganRepo.save(bimbingan);
        }
        return "redirect:/dosen/kelola";
    }

    @GetMapping("/pengajuan/reject/{id}")
    public String rejectPengajuan(@PathVariable Long id) {
        PermintaanJadwal pengajuan = permintaanRepo.findById(id).orElse(null);
        if (pengajuan != null) {
            pengajuan.setStatus("Rejected");
            permintaanRepo.save(pengajuan);
            // Hapus Bimbingan jika ada yang sudah dibuat (walaupun seharusnya tidak ada jika status Pending)
        }
        return "redirect:/dosen/kelola";
    }

    // --- METODE YANG TIDAK DIPAKAI / DUPLIKAT DIHAPUS ---
    // @PostMapping("/dosen/permintaan/{id}/komentar") telah dihapus karena duplikat/redundant
}
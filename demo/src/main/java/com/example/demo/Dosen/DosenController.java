package com.example.demo.Dosen;

import com.example.demo.Entity.*;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*; // Menggunakan RestController, RequestMapping, dll.
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.service.BimbinganService;
import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.service.PermintaanJadwalService;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.demo.Entity.Notifikasi;
import com.example.demo.Repository.NotifikasiRepository;

@Controller
@RequestMapping("/dosen")
public class DosenController {

    @Autowired
    private PermintaanJadwalService permintaanJadwalService;
    @Autowired
    private NotifikasiRepository notifRepo;	
    @Autowired
    private PenggunaRepository penggunaRepository;
    @Autowired
    private PermintaanJadwalRepository permintaanRepo;
    @Autowired
    private BimbinganRepository bimbinganRepo;
    @Autowired
    private BimbinganService bimbinganService;

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
        }

        return "redirect:/dosen/riwayat/detail/" + id;
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

        // Simpan ke PermintaanJadwal
        PermintaanJadwal pengajuan = new PermintaanJadwal();
        pengajuan.setDosen(dosen);
        pengajuan.setMahasiswa(mahasiswa);
        pengajuan.setLokasi(lokasi);
        pengajuan.setTanggal(LocalDate.parse(tanggal));
        pengajuan.setWaktu(LocalTime.parse(waktu));
        pengajuan.setCatatan(catatan);
        pengajuan.setStatus("Approved");

        permintaanRepo.save(pengajuan);

        // Buat Bimbingan otomatis (sebagai placeholder jadwal yang disetujui)
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
    @Transactional
    @GetMapping("/jadwal")
    public String jadwalMahasiswa(HttpSession session, Model model) {

        Pengguna sessionUser = (Pengguna) session.getAttribute("loggedUser");
        if (sessionUser == null) return "redirect:/login";

        Pengguna dosen = penggunaRepo.findByEmail(sessionUser.getEmail());

        Map<String, List<Map<String, Object>>> jadwalPerHari = new HashMap<>();

        // Mapping hari Inggris -> Indonesia (untuk bimbingan)
        Map<String, String> hariMapping = Map.of(
                "MONDAY", "Senin",
                "TUESDAY", "Selasa",
                "WEDNESDAY", "Rabu",
                "THURSDAY", "Kamis",
                "FRIDAY", "Jumat",
                "SATURDAY", "Sabtu",
                "SUNDAY", "Minggu"
        );

        // =====================
// Jadwal Mengajar Dosen
// =====================
        for (MataKuliah mk : dosen.getMengajar()) {
            if (mk.getJadwal() == null) continue;

            for (JadwalMK sesi : mk.getJadwal()) {
                Map<String, Object> e = new HashMap<>();
                e.put("type", "MK");
                e.put("name", mk.getNamaMK());
                e.put("startHour", sesi.getWaktu().getHour());
                e.put("endHour", sesi.getEndTime().getHour());

                System.out.println("DEBUG: [DOSEN] MK " + mk.getNamaMK()
                        + " hari=" + sesi.getHari()
                        + " start=" + sesi.getWaktu()
                        + " end=" + sesi.getEndTime());

                jadwalPerHari
                        .computeIfAbsent(sesi.getHari(), k -> new ArrayList<>())
                        .add(e);
            }
        }


        // --- Bimbingan ---
        List<Bimbingan> bimbinganList = bimbinganService.getBimbinganUntukMahasiswa(dosen.getEmail());
        if (bimbinganList != null) {
            for (Bimbingan b : bimbinganList) {
                if (b.getWaktu() != null) {
                    Map<String,Object> e = new HashMap<>();
                    e.put("type","Bimbingan");
                    e.put("note", b.getPermintaanJadwal() != null ? b.getPermintaanJadwal().getCatatan() : null);
                    e.put("startHour", b.getWaktu().getHour());

                    String hariIndonesia = hariMapping.getOrDefault(b.getHari().toUpperCase(), b.getHari());
                    System.out.println("DEBUG: Menambahkan Bimbingan id=" + b.getIdBimbingan() + " ke hari " + hariIndonesia
                            + " jam=" + b.getWaktu());

                    jadwalPerHari.computeIfAbsent(hariIndonesia, k -> new ArrayList<>()).add(e);
                }
            }
        }

        // --- Cetak jadwalPerHari lengkap untuk debug ---
        System.out.println("===== DEBUG JADWAL PER HARI =====");
        for (String h : jadwalPerHari.keySet()) {
            System.out.println("Hari: " + h);
            for (Map<String,Object> item : jadwalPerHari.get(h)) {
                System.out.println("  " + item);
            }
        }

        List<String> hariList = Arrays.asList("Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu");

        model.addAttribute("hariList", hariList);
        model.addAttribute("jadwalPerHari", jadwalPerHari);
        model.addAttribute("nama", dosen.getNama());

        return "JadwalDosen";
    }
    @GetMapping("/pengajuan/approve/{id}")
    public String approvePengajuan(@PathVariable Long id) {
        // 1. Cari data pengajuan berdasarkan ID
        PermintaanJadwal pengajuan = permintaanRepo.findById(id).orElse(null);

        if (pengajuan != null) {
            // 2. Ubah status menjadi Approved
            pengajuan.setStatus("Approved");
            permintaanRepo.save(pengajuan);

            // 3. (Opsional) Buat notifikasi untuk mahasiswa
            Notifikasi notif = new Notifikasi(
                    pengajuan.getMahasiswa().getEmail(),
                    "Pengajuan bimbingan Anda pada " + pengajuan.getTanggal() + " telah DISETUJUI."
            );
            notifRepo.save(notif);

            // 4. (Opsional) Otomatis buat Bimbingan baru (jika logika bisnis Anda demikian)
            Bimbingan bimbinganBaru = new Bimbingan();
            bimbinganBaru.setPermintaanJadwal(pengajuan);
            bimbinganBaru.setLokasi(pengajuan.getLokasi());
            bimbinganBaru.setHari(pengajuan.getTanggal().getDayOfWeek().toString());
            bimbinganBaru.setWaktu(pengajuan.getWaktu());
            bimbinganBaru.setIsBimbingan(true);
            bimbinganRepo.save(bimbinganBaru);
        }

        // 5. Kembali ke halaman kelola (daftar pengajuan)
        return "redirect:/dosen/kelola";
    }

    @GetMapping("/pengajuan/reject/{id}")
    public String rejectPengajuan(@PathVariable Long id) {
        // 1. Cari data pengajuan
        PermintaanJadwal pengajuan = permintaanRepo.findById(id).orElse(null);

        if (pengajuan != null) {
            // 2. Ubah status menjadi Rejected
            pengajuan.setStatus("Rejected");
            permintaanRepo.save(pengajuan);

            // 3. (Opsional) Buat notifikasi
            Notifikasi notif = new Notifikasi(
                    pengajuan.getMahasiswa().getEmail(),
                    "Maaf, pengajuan bimbingan Anda pada " + pengajuan.getTanggal() + " DITOLAK."
            );
            notifRepo.save(notif);
        }

        // 4. Kembali ke halaman kelola
        return "redirect:/dosen/kelola";
    }

    }

    // --- METODE YANG TIDAK DIPAKAI / DUPLIKAT DIHAPUS ---
    // @PostMapping("/dosen/permintaan/{id}/komentar") telah dihapus karena duplikat/redundant

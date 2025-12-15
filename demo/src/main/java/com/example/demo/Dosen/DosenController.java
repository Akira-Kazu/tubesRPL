package com.example.demo.Dosen;

import com.example.demo.DTO.ProgressMhsDTO; // Pastikan import ini sesuai nama package folder DTO Anda (huruf kecil 'dto' atau besar 'DTO')
import com.example.demo.Entity.*;
import com.example.demo.Repository.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequestMapping("/dosen")
public class DosenController {

    // --- SERVICE & REPOSITORY ---
    @Autowired
    private PermintaanJadwalService permintaanJadwalService;
    @Autowired
    private BimbinganService bimbinganService;

    // --- REPOSITORY ---
    @Autowired
    private NotifikasiRepository notifRepo;
    @Autowired
    private PenggunaRepository penggunaRepository; // Gunakan satu nama variable saja agar konsisten
    @Autowired
    private PermintaanJadwalRepository permintaanRepo;
    @Autowired
    private BimbinganRepository bimbinganRepo;

    // @Autowired private ProgressMhsDTO ...  <-- SUDAH DIHAPUS (TIDAK PERLU)

    // ==========================================
    // BERANDA DOSEN
    // ==========================================
    @GetMapping
    public String dosenHome(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        model.addAttribute("namaUser", dosen.getNama());

        List<Bimbingan> listJadwal = bimbinganRepo.findByPermintaanJadwal_Dosen_Email(dosen.getEmail());
        model.addAttribute("listJadwal", listJadwal);

        int pendingCount = permintaanRepo.findByDosen_EmailAndStatus(dosen.getEmail(), "Pending").size();
        int bimbinganCount = listJadwal.size();
        long catatanCount = listJadwal.stream()
                .filter(b -> b.getKomentarDosen() != null && !b.getKomentarDosen().isEmpty())
                .count();

        model.addAttribute("pengajuanBaru", pendingCount);
        model.addAttribute("bimbinganTotal", bimbinganCount);
        model.addAttribute("catatanDiberikan", catatanCount);

        return "berandaDosen";
    }

    // ==========================================
    // PROGRESS MAHASISWA (Fitur Baru)
    // ==========================================
    @GetMapping("/progress")
    public String progressTA(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        // Service mengembalikan List<ProgressMhsDTO> yang sudah berisi data
        List<ProgressMhsDTO> listData = bimbinganService.getProgressByDosen(dosen.getEmail());

        model.addAttribute("dataProgress", listData);

        return "progressTADosen";
    }

    // ==========================================
    // RIWAYAT BIMBINGAN
    // ==========================================
    @GetMapping("/riwayat")
    public String riwayatBimbingan(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        String emailDosen = dosen.getEmail();
        List<Bimbingan> riwayat = bimbinganService.getBimbinganUntukDosen(emailDosen);

        List<Bimbingan> approvedOrRealized = riwayat.stream()
                .filter(b -> b.getPermintaanJadwal() != null
                        && (b.getPermintaanJadwal().getStatus().equalsIgnoreCase("Approved")
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

    @PostMapping("/riwayat/realisasi/{idBimbingan}")
    public String realisasiBimbingan(
            @PathVariable Long idBimbingan,
            @RequestParam String komentarRealisasi,
            Model model) {

        LocalDateTime waktuSelesai = LocalDateTime.now();
        try {
            bimbinganService.realisasiSesiBimbingan(idBimbingan, komentarRealisasi, waktuSelesai);
            return "redirect:/dosen/riwayat/detail/" + idBimbingan + "?success=true";
        } catch (RuntimeException e) {
            return "redirect:/dosen/riwayat/detail/" + idBimbingan + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/riwayat/update-komentar")
    public String updateKomentar(@RequestParam Long id, @RequestParam String komentar) {
        Bimbingan b = bimbinganService.getById(id);
        if (b != null) {
            b.setKomentarDosen(komentar);
            bimbinganService.saveBimbingan(b);
        }
        return "redirect:/dosen/riwayat/detail/" + id;
    }

    // ==========================================
    // PENGAJUAN (Bikin Jadwal Manual)
    // ==========================================
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

        PermintaanJadwal pengajuan = new PermintaanJadwal();
        pengajuan.setDosen(dosen);
        pengajuan.setMahasiswa(mahasiswa);
        pengajuan.setLokasi(lokasi);
        pengajuan.setTanggal(LocalDate.parse(tanggal));
        pengajuan.setWaktu(LocalTime.parse(waktu));
        pengajuan.setCatatan(catatan);
        pengajuan.setStatus("Approved");
        permintaanRepo.save(pengajuan);

        Bimbingan bimbingan = new Bimbingan();
        bimbingan.setPermintaanJadwal(pengajuan);
        bimbingan.setLokasi(lokasi);
        bimbingan.setHari(pengajuan.getTanggal().getDayOfWeek().toString());
        bimbingan.setWaktu(LocalTime.parse(waktu));
        bimbingan.setIsBimbingan(true);
        bimbinganRepo.save(bimbingan);

        return "redirect:/dosen/riwayat";
    }

    // ==========================================
    // KELOLA (Approve/Reject Mahasiswa)
    // ==========================================
    @GetMapping("/kelola")
    public String listPengajuanPending(Model model) {
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

            Notifikasi notif = new Notifikasi(
                    pengajuan.getMahasiswa().getEmail(),
                    "Pengajuan bimbingan Anda pada " + pengajuan.getTanggal() + " telah DISETUJUI."
            );
            notifRepo.save(notif);

            Bimbingan bimbinganBaru = new Bimbingan();
            bimbinganBaru.setPermintaanJadwal(pengajuan);
            bimbinganBaru.setLokasi(pengajuan.getLokasi());
            bimbinganBaru.setHari(pengajuan.getTanggal().getDayOfWeek().toString());
            bimbinganBaru.setWaktu(pengajuan.getWaktu());
            bimbinganBaru.setIsBimbingan(true);
            bimbinganRepo.save(bimbinganBaru);
        }
        return "redirect:/dosen/kelola";
    }

    @GetMapping("/pengajuan/reject/{id}")
    public String rejectPengajuan(@PathVariable Long id) {
        PermintaanJadwal pengajuan = permintaanRepo.findById(id).orElse(null);
        if (pengajuan != null) {
            pengajuan.setStatus("Rejected");
            permintaanRepo.save(pengajuan);

            Notifikasi notif = new Notifikasi(
                    pengajuan.getMahasiswa().getEmail(),
                    "Maaf, pengajuan bimbingan Anda pada " + pengajuan.getTanggal() + " DITOLAK."
            );
            notifRepo.save(notif);
        }
        return "redirect:/dosen/kelola";
    }

    // ==========================================
    // JADWAL VISUALISASI
    // ==========================================
    @Transactional
    @GetMapping("/jadwal")
    public String jadwalMahasiswa(HttpSession session, Model model) {
        Pengguna sessionUser = (Pengguna) session.getAttribute("loggedUser");
        if (sessionUser == null) return "redirect:/login";

        Pengguna dosen = penggunaRepository.findByEmail(sessionUser.getEmail());

        Map<String, List<Map<String, Object>>> jadwalPerHari = new HashMap<>();
        Map<String, String> hariMapping = Map.of(
                "MONDAY", "Senin", "TUESDAY", "Selasa", "WEDNESDAY", "Rabu",
                "THURSDAY", "Kamis", "FRIDAY", "Jumat", "SATURDAY", "Sabtu", "SUNDAY", "Minggu"
        );

        // Loop Mata Kuliah
        for (MataKuliah mk : dosen.getMengajar()) {
            if (mk.getJadwal() == null) continue;
            for (JadwalMK sesi : mk.getJadwal()) {
                Map<String, Object> e = new HashMap<>();
                e.put("type", "MK");
                e.put("name", mk.getNamaMK());
                e.put("startHour", sesi.getWaktu().getHour());
                e.put("endHour", sesi.getEndTime().getHour());
                jadwalPerHari.computeIfAbsent(sesi.getHari(), k -> new ArrayList<>()).add(e);
            }
        }

        // Loop Bimbingan
        List<Bimbingan> bimbinganList = bimbinganService.getBimbinganUntukMahasiswa(dosen.getEmail());
        if (bimbinganList != null) {
            for (Bimbingan b : bimbinganList) {
                if (b.getWaktu() != null) {
                    Map<String,Object> e = new HashMap<>();
                    e.put("type","Bimbingan");
                    e.put("note", b.getPermintaanJadwal() != null ? b.getPermintaanJadwal().getCatatan() : null);
                    e.put("startHour", b.getWaktu().getHour());

                    String hariIndonesia = hariMapping.getOrDefault(b.getHari().toUpperCase(), b.getHari());
                    jadwalPerHari.computeIfAbsent(hariIndonesia, k -> new ArrayList<>()).add(e);
                }
            }
        }

        List<String> hariList = Arrays.asList("Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu");
        model.addAttribute("hariList", hariList);
        model.addAttribute("jadwalPerHari", jadwalPerHari);
        model.addAttribute("nama", dosen.getNama());

        return "JadwalDosen";
    }
}
package com.example.demo.Dosen;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.service.BimbinganService;
import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.service.PermintaanJadwalService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

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
private PermintaanJadwalService permintaanJadwalService;

    @Autowired
    private PenggunaRepository penggunaRepository;
    @Autowired
private PermintaanJadwalRepository permintaanRepo;
@Autowired
private BimbinganRepository bimbinganRepo;
    @Autowired
    private BimbinganService bimbinganService;


 @GetMapping
    public String dosenHome() {
        return "berandaDosen"; 
    }

    @GetMapping("/riwayat")
    public String riwayatBimbingan(HttpSession session, Model model) {
        Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");
        if (dosen == null) return "redirect:/login";

        String emailDosen = dosen.getEmail();

        String email = dosen.getEmail();
        List<Bimbingan> riwayat = bimbinganService.getBimbinganUntukDosen(emailDosen);

        model.addAttribute("riwayat", riwayat);
        return "riwayatBimbinganDosen";
    }

    @GetMapping("/pengajuan")
    public String pengajuanBimbingan(Model model) {

        // Ambil semua mahasiswa
        model.addAttribute("mahasiswaList", penggunaRepository.findByRole(1));
        return "pengajuanFormDosen";
    }

    @GetMapping("/riwayat/detail/{id}")
    public String detailRiwayat(@PathVariable Long id, Model model) {
        Bimbingan bimbingan = bimbinganService.getById(id);
        model.addAttribute("bimbingan", bimbingan);
        return "riwayatDetailDosen";
    }

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
@GetMapping("/pengajuan/pending")
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

        // Optional: langsung buat Bimbingan jika disetujui
        Bimbingan bimbingan = new Bimbingan();
        bimbingan.setPermintaanJadwal(pengajuan);
        bimbingan.setLokasi(pengajuan.getLokasi());
        bimbingan.setHari(pengajuan.getTanggal().getDayOfWeek().toString());
        bimbingan.setWaktu(pengajuan.getWaktu());
        bimbingan.setIsBimbingan(true);

        bimbinganRepo.save(bimbingan);
    }
    return "redirect:/dosen/pengajuan/pending";
}

    @PostMapping("/dosen/permintaan/{id}/komentar")
public String tambahKomentar(
        @PathVariable Long id,
        @RequestParam String komentar,
        HttpSession session
) {
    Pengguna dosen = (Pengguna) session.getAttribute("loggedUser");

        PermintaanJadwal permintaan = permintaanRepo.findById(id).orElse(null);
        permintaanRepo.save(permintaan);


        permintaanRepo.save(permintaan);
    return "redirect:/dosen/permintaan";
}

@GetMapping("/pengajuan/reject/{id}")
public String rejectPengajuan(@PathVariable Long id) {
    PermintaanJadwal pengajuan = permintaanRepo.findById(id).orElse(null);
    if (pengajuan != null) {
        pengajuan.setStatus("Rejected");
        permintaanRepo.save(pengajuan);
    }
    return "redirect:/dosen/pengajuan/pending";
}


@GetMapping("/kelola")
public String kelolaPengajuan(Model model) {
    List<PermintaanJadwal> pengajuanList = permintaanRepo.findAll()
            .stream()
            .filter(p -> "Pending".equals(p.getStatus()))
            .toList();

    model.addAttribute("pengajuanList", pengajuanList);
    return "kelolaPengajuanDosen"; // sesuaikan nama file HTML
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

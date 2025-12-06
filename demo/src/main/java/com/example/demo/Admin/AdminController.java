package com.example.demo.Admin;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Repository.BimbinganRepository;
import com.example.demo.service.AdminService;
import com.example.demo.service.BimbinganService;
import com.example.demo.service.PermintaanJadwalService;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
private BimbinganRepository bimbinganRepository;

    private final AdminService adminService;
    private final BimbinganService bimbinganService;
    private final PermintaanJadwalService permintaanJadwalService;

    @Autowired
    public AdminController(AdminService adminService,
                           BimbinganService bimbinganService,
                           PermintaanJadwalService permintaanJadwalService) {
        this.adminService = adminService;
        this.bimbinganService = bimbinganService;
        this.permintaanJadwalService = permintaanJadwalService;
    }

    @GetMapping("/pengajuan")
    public String kelolaPengajuan(Model model) {
        model.addAttribute("pengajuan", new Bimbingan());
        model.addAttribute("mahasiswa", adminService.getMahasiswa());
        model.addAttribute("dosenList", adminService.getDosen());
        return "pengajuanFormAdmin";
    }

    @PostMapping("/pengajuan/submit")
    public String submit(
            @RequestParam("dosen") String dosenEmail,
            @RequestParam("mahasiswa") String mahasiswaEmail,
            @RequestParam("lokasi") String lokasi,
            @RequestParam("tanggal") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tanggal,
            @RequestParam("waktu") @DateTimeFormat(pattern = "HH:mm") LocalTime waktu,
            @RequestParam(value = "catatan", required = false) String catatan
    ) {
        adminService.prosesPengajuan(dosenEmail, mahasiswaEmail, lokasi, tanggal, waktu, catatan);
        return "redirect:/admin/pengajuan?success";
    }

    @GetMapping("/kelolaPengajuanDosen/{email}")
public String kelolaPengajuan(@PathVariable("email") String email, Model model) {

    // Ambil semua bimbingan untuk dosen
    List<Bimbingan> listBimbingan = bimbinganService.getBimbinganUntukDosen(email);

    model.addAttribute("listPengajuan", listBimbingan); // nama sama dengan HTML
    model.addAttribute("email", email);

    return "kelolaPengajuanDosen_Admin";
}

@GetMapping("/hapus/{idBimbingan}")
public String hapusBimbingan(@PathVariable("idBimbingan") Long idBimbingan,
                             @RequestParam(value = "email", required = false) String email) {
    // Hapus data bimbingan
    bimbinganService.deleteBimbingan(idBimbingan);

    // Redirect kembali ke halaman pengajuan dosen yang sama
    if (email != null) {
        return "redirect:/admin/kelolaPengajuanDosen/" + email;
    }
    return "redirect:/admin/kelola-dosen";
}

    @GetMapping({"/menu-utama", "/dashboard"})
    public String menuUtama(Model model) {
        LocalDate today = LocalDate.now();

    // Ambil semua bimbingan yang tanggalnya hari ini
    List<Bimbingan> todayList = bimbinganRepository.findByPermintaanJadwal_Tanggal(today);

    model.addAttribute("todayList", todayList);
        return "berandaAdmin";
    }

    @GetMapping("/kelola-mahasiswa")
    public String kelolaMahasiswa(Model model) {
        model.addAttribute("mahasiswa", adminService.getMahasiswa());
        return "kelolaMahasiswa_Admin";
    }

    @GetMapping("/kelola-dosen")
    public String kelolaDosen(Model model) {
        model.addAttribute("dosenList", adminService.getDosen());
        return "kelolaDosen_Admin";
    }

    @GetMapping("/delete/{email}")
    public String deleteUser(@PathVariable("email") String email) {
        adminService.deleteUser(email);
        return "redirect:/admin/menu-utama";
    }

}

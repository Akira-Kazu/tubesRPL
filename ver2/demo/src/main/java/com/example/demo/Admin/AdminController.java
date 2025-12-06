package com.example.demo.Admin;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Entity.Pengguna;
import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.service.BimbinganService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

       private final AdminService adminService;
    private final BimbinganService bimbinganService; // tambah service

    public AdminController(AdminService adminService, BimbinganService bimbinganService) {
        this.adminService = adminService;
        this.bimbinganService = bimbinganService;
    }
@GetMapping("/pengajuan")
public String kelolaPengajuan(Model model) {
    Bimbingan bimbingan = new Bimbingan();
    model.addAttribute("pengajuan", new Bimbingan());
    // Kirim ke template
    model.addAttribute("mahasiswa", adminService.getMahasiswa());
    model.addAttribute("dosenList", adminService.getDosen());
    return "pengajuanFormAdmin";
}

    @PostMapping("/pengajuan/submit")
    public String submit(@ModelAttribute("pengajuan") Bimbingan bimbingan) {
        bimbinganService.saveBimbingan(bimbingan); // pakai service
        return "redirect:/admin/pengajuan";
    }

    @GetMapping({"/menu-utama", "/dashboard"})
    public String menuUtama(Model model) {
        // todayList dummy atau ambil dari service nanti
        model.addAttribute("todayList", null);
        return "berandaAdmin";   // nama HTML kamu
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

    
    // ===== TOMBOL DI CARD =====

    @GetMapping("/admin/dosen")
    public String tombolKelolaDosen() {
        return "admin/kelola-dosen";
    }

    @GetMapping("/admin/mahasiswa")
    public String tombolKelolaMahasiswa() {
        return "admin/kelola-mahasiswa";
    }

    // ===== JADWAL DOSEN =====
    @GetMapping("/admin/jadwal-dosen")
    public String jadwalDosen(Model model) {
        model.addAttribute("jadwalDosenList", null);
        return "admin/jadwal-dosen";
    }

    // ===== JADWAL MAHASISWA =====
    @GetMapping("/admin/jadwal-mahasiswa")
    public String jadwalMahasiswa(Model model) {
        model.addAttribute("jadwalMahasiswaList", null);
        return "admin/jadwal-mahasiswa";
    }

    // ===== INBOX =====
    @GetMapping("/admin/inbox")
    public String inboxAdmin() {
        return "admin/inbox"; 
    }

}

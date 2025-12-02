package com.example.demo.Admin;

import com.example.demo.Entity.Pengguna;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
        model.addAttribute("dosen", adminService.getDosen());
        return "kelolaDosen_Admin";
    }

    @GetMapping("/pengajuan")
    public String kelolaPengajuan(Model model) {
        model.addAttribute("listPengajuan", adminService.getPengajuanDosen());
        return "admin/kelolaPengajuanDosen"; 
    }

    @GetMapping("/delete/{email}")
    public String deleteUser(@PathVariable("email") String email) {
        adminService.deleteUser(email);
        return "redirect:/admin/menu-utama";
    }

}

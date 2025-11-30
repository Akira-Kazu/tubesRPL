package com.example.demo.Admin;

import com.example.demo.Entity.Pengguna;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Pengguna> penggunaList = adminService.getAllUsers();
        model.addAttribute("users", penggunaList);
        return "dashboardAdmin"; // arahkan ke HTML admin
    }

    @GetMapping("/add")
    public String addUserPage(Model model) {
        model.addAttribute("pengguna", new Pengguna());
        return "addUserPage";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute Pengguna pengguna) {
        adminService.addUser(pengguna);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/{email}")
    public String deleteUser(@PathVariable String email) {
        adminService.deleteUser(email);
        return "redirect:/admin/dashboard";
    }
}

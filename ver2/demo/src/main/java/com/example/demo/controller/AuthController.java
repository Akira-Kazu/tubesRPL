package com.example.demo.controller;

import com.example.demo.Entity.Pengguna;
import com.example.demo.Repository.PenggunaRepository;
import jakarta.servlet.http.HttpSession; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @GetMapping("/")
    public String landingPage() {
        return "landingpage";
    }


    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Modul hal. 4 poin 1: Kalau sudah login, langsung lempar ke dashboard
        if (session.getAttribute("user") != null) {
            Pengguna user = (Pengguna) session.getAttribute("user");
            return redirectBasedOnRole(user.getRole());
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Hapus sesi
        return "redirect:/";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String email, // Di HTML kamu name="username"
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        
        Pengguna user = penggunaRepository.findById(email).orElse(null);
        
        if (user != null && user.getPassword().equals(password)) {
	     // ^ Nanti ganti jadi: passwordEncoder.matches(password, user.getPassword()) 
            session.setAttribute("user", user);
            return redirectBasedOnRole(user.getRole());
        }
        model.addAttribute("error", "Email atau Password salah!");
        return "login";
    }

    private String redirectBasedOnRole(Integer role) {
        if (role == 1) return "redirect:/mahasiswa";
        if (role == 2) return "redirect:/dosen";
        if (role == 3) return "redirect:/admin/dashboard";
        return "redirect:/";
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.Entity.Pengguna;
import com.example.demo.Repository.PenggunaRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @GetMapping("/")
    public String landingPage() {
        return "index"; 
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; 
    }

    @GetMapping("/logout")
	public String logout(HttpSession session) {
  	  // 1. Hapus session biar aman
    session.invalidate(); 
    
 	  // 2. Balik ke halaman login
	    return "redirect:/login"; 
	}

    @PostMapping("/login")
public String processLogin(@ModelAttribute Pengguna pengguna, Model model, HttpSession session) {
    String email = pengguna.getEmail();
    String password = pengguna.getPassword();

    Pengguna user = penggunaRepository.findByEmailAndPassword(email, password);

    if (user != null) {
        // Simpan user ke session
        session.setAttribute("loggedUser", user);

        switch (user.getRole()) {
            case 1: return "redirect:/mahasiswa";
            case 2: return "redirect:/dosen";
            case 3: return "redirect:/admin/dashboard";
        }
    }

    model.addAttribute("error", "Email atau Password salah!");
    return "login";
}

}

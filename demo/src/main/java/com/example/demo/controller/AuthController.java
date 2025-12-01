/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.Entity.Pengguna;
import com.example.demo.Repository.PenggunaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String logout() {
        return "redirect:/";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("account") String email,
                               @RequestParam("password") String password,
                               Model model) {

        Pengguna user = penggunaRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            if (user.getRole() == 1) {
                return "redirect:/mahasiswa";                
            } else if (user.getRole() == 2) {
                return "redirect:/dosen";                    
            } else if (user.getRole() == 3) {
                return "redirect:/admin/dashboard"; 
            }
        }
        model.addAttribute("error", "Email atau Password salah!");
        return "login";
    }
}
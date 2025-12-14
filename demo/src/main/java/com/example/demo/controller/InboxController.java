package com.example.demo.controller;

import com.example.demo.Entity.Notifikasi;
import com.example.demo.Entity.Pengguna;
import com.example.demo.Repository.NotifikasiRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class InboxController {

    @Autowired
    private NotifikasiRepository notifRepo;

    // Inbox buat Mahasiswa
    @GetMapping("/inbox") // atau /mahasiswa/inbox tergantung link di html lu
    public String inboxMahasiswa(HttpSession session, Model model) {
        Pengguna user = (Pengguna) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        // Ambil notif punya user ini
        List<Notifikasi> listNotif = notifRepo.findByEmailUserOrderByWaktuDibuatDesc(user.getEmail());
        model.addAttribute("listNotif", listNotif);
        model.addAttribute("role", "Mahasiswa"); // Buat sidebar/header

        return "inboxMahasiswa"; // Pake satu file HTML aja biar hemat
    }
    
    // Inbox buat Dosen (Mapping sama, nanti dibedain di view kalo perlu)
    // Tapi button inbox dosen arahnya ke /inbox juga, pake method di atas cukup.
    // Kalau mau dipisah path-nya:

    @GetMapping("/dosen/inbox")
    public String inboxDosen(HttpSession session, Model model) {
        Pengguna user = (Pengguna) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";
        
        List<Notifikasi> listNotif = notifRepo.findByEmailUserOrderByWaktuDibuatDesc(user.getEmail());
        model.addAttribute("listNotif", listNotif);
        model.addAttribute("role", "Dosen");

        return "inboxDosen";
    }

}
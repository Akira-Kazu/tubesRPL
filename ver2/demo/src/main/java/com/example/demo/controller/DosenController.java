package com.example.demo.controller; 

import com.example.demo.Entity.PermintaanJadwal;
import com.example.demo.Repository.PermintaanJadwalRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DosenController {

    @Autowired
    private PermintaanJadwalRepository permintaanRepository;

    @GetMapping("/dosen")
    public String berandaDosen() {
        return "berandaDosen"; 
    }

    @GetMapping("/dosen/kelola")
    public String kelolaPengajuan(Model model) {
        // Ambil SEMUA data dulu
        List<PermintaanJadwal> allData = permintaanRepository.findAll();
        
        // Filter: Cuma ambil yang statusnya NULL atau PENDING
        List<PermintaanJadwal> listPending = allData.stream()
            .filter(x -> x.getStatus() == null || x.getStatus().equalsIgnoreCase("PENDING"))
            .collect(Collectors.toList());
        
        model.addAttribute("listPermintaan", listPending);
        return "kelolaPengajuanDosen"; 
    }

    @GetMapping("/dosen/riwayat")
    public String riwayatDosen(Model model) {
        List<PermintaanJadwal> allData = permintaanRepository.findAll();

            List<PermintaanJadwal> listRiwayat = allData.stream()
            .filter(x -> x.getStatus() != null && !x.getStatus().equalsIgnoreCase("PENDING"))
            .collect(Collectors.toList());

        model.addAttribute("listRiwayat", listRiwayat); //nanti dipake di HTML riwayat
        return "riwayatBimbinganDosen";
    }

    @PostMapping("/dosen/approve/{id}")
    public String approvePermintaan(@PathVariable("id") Integer id) {
        PermintaanJadwal permintaan = permintaanRepository.findById(id).orElse(null);
        if (permintaan != null) {
            permintaan.setStatus("DISETUJUI");
            permintaanRepository.save(permintaan);
        }
        return "redirect:/dosen/kelola";
    }

    @PostMapping("/dosen/reject/{id}")
    public String rejectPermintaan(@PathVariable("id") Integer id, 
                                   @RequestParam(value = "alasan", required = false) String alasan) {
        PermintaanJadwal permintaan = permintaanRepository.findById(id).orElse(null);
        if (permintaan != null) {
            permintaan.setStatus("DITOLAK");
            if (alasan != null) permintaan.setAlasan_penolakan(alasan);
            permintaanRepository.save(permintaan);
        }
        return "redirect:/dosen/kelola";
    }
}
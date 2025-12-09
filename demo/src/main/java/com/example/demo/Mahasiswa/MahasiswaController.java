package com.example.demo.Mahasiswa;
import com.example.demo.Entity.*;
import com.example.demo.service.JadwalService;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Repository.PenggunaRepository;
import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.service.PermintaanJadwalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.service.BimbinganService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class MahasiswaController {

    @Autowired
    private JadwalService jadwalService;

    @Autowired
    private PermintaanJadwalService permintaanJadwalService;

    @Autowired
    private PermintaanJadwalRepository permintaanRepo;

    @Autowired
    private PenggunaRepository penggunaRepo;

    @Autowired
    private BimbinganService bimbinganService;

    @GetMapping("/mahasiswa")
    public String beranda() {
        return "BerandaMahasiswa";
    }

    @GetMapping("/menu")
    public String menu() {
        return "BerandaMahasiswa";
    }

    @GetMapping("/riwayat")
    public String riwayatBimbingan(HttpSession session, Model model) {

        // Ambil user dari session
        Pengguna mahasiswa = (Pengguna) session.getAttribute("loggedUser");
        if (mahasiswa == null) return "redirect:/login";

        String email = mahasiswa.getEmail();

        List<PermintaanJadwal> approvedRiwayat = permintaanJadwalService.getRiwayatApprovedUntukMahasiswa(email);

        model.addAttribute("riwayat", approvedRiwayat);
        model.addAttribute("inisialUser", mahasiswa.getNama().substring(0, 1));

        return "riwayatBimbinganMahasiswa";
    }

    @GetMapping("/mahasiswa/detail/{id}")
    public String detailMahasiswa(@PathVariable("id") Long idPermintaan, Model model) {
        Bimbingan bimbingan = bimbinganService.getByPermintaanId(idPermintaan);
        model.addAttribute("bimbingan", bimbingan);
        return "riwayatdetailBimbinganMahasiswa";
    }


    @GetMapping("/pengajuan")
    public String pengajuan(Model model) {
        model.addAttribute("dosenList", penggunaRepo.findByRole(2));

        return "pengajuanFormMahasiswa";
    }

    @PostMapping("/pengajuan/submit")
    public String submitPengajuan(
            @RequestParam("dosenEmail") String emailDosen,
            @RequestParam("lokasi") String lokasi,
            @RequestParam("tanggal") String tanggal,
            @RequestParam("waktu") String waktu,
            @RequestParam(value = "catatan", required = false) String catatan,
            HttpSession session,
            Model model
    ) {
        Pengguna mahasiswa = (Pengguna) session.getAttribute("loggedUser");

        if (mahasiswa == null) return "redirect:/login";

        Pengguna dosen = penggunaRepo.findByEmail(emailDosen);

        LocalDate tanggalParsed = LocalDate.parse(tanggal);
        LocalTime waktuParsed = LocalTime.parse(waktu);

        // Cek jadwal bentrok
        if (!jadwalService.isAvailable(mahasiswa.getEmail(), tanggalParsed, waktuParsed)) {
            model.addAttribute("error", "Jadwal bentrok dengan MK atau bimbingan lain.");
            return "pengajuanFormMahasiswa"; // kembali ke halaman form
        }
        PermintaanJadwal p = new PermintaanJadwal();
        p.setMahasiswa(mahasiswa);
        p.setDosen(dosen);
        p.setLokasi(lokasi);
        p.setTanggal(LocalDate.parse(tanggal));
        p.setWaktu(LocalTime.parse(waktu));
        p.setCatatan(catatan);
        p.setStatus("Pending");

        permintaanRepo.save(p);

        return "redirect:/pengajuan?success=true";
    }


    @GetMapping("/kelola")
    public String kelola() {
        return "kelolaPengajuanmahasiswa"; // templates/kelola.html
    }


    @GetMapping("/progress")
    public String progress() {
        return "progressTAMahasiswa"; // templates/progress.html
    }

    @Transactional
    @GetMapping("/jadwal")
    public String jadwalMahasiswa(HttpSession session, Model model) {

        Pengguna sessionUser = (Pengguna) session.getAttribute("loggedUser");
        if (sessionUser == null) return "redirect:/login";

        Pengguna mahasiswa = penggunaRepo.findByEmail(sessionUser.getEmail());

        Map<String, List<Map<String, Object>>> jadwalPerHari = new HashMap<>();

        // Mapping hari Inggris -> Indonesia (untuk bimbingan)
        Map<String, String> hariMapping = Map.of(
                "MONDAY", "Senin",
                "TUESDAY", "Selasa",
                "WEDNESDAY", "Rabu",
                "THURSDAY", "Kamis",
                "FRIDAY", "Jumat",
                "SATURDAY", "Sabtu",
                "SUNDAY", "Minggu"
        );

        // --- MataKuliah ---
        for (MataKuliah mk : mahasiswa.getMataKuliah()) {
            if (mk.getJadwal() != null) {
                for (JadwalMK sesi : mk.getJadwal()) {
                    Map<String,Object> e = new HashMap<>();
                    e.put("type","MK");
                    e.put("name", mk.getNamaMK());
                    e.put("startHour", sesi.getWaktu().getHour());
                    e.put("endHour", sesi.getEndTime().getHour());

                    System.out.println("DEBUG: Menambahkan MK " + mk.getNamaMK() + " ke hari " + sesi.getHari()
                            + " start=" + sesi.getWaktu() + " end=" + sesi.getEndTime());

                    jadwalPerHari.computeIfAbsent(sesi.getHari(), k -> new ArrayList<>()).add(e);
                }
            }
        }

        // --- Bimbingan ---
        List<Bimbingan> bimbinganList = bimbinganService.getBimbinganUntukMahasiswa(mahasiswa.getEmail());
        if (bimbinganList != null) {
            for (Bimbingan b : bimbinganList) {
                if (b.getWaktu() != null) {
                    Map<String,Object> e = new HashMap<>();
                    e.put("type","Bimbingan");
                    e.put("note", b.getPermintaanJadwal() != null ? b.getPermintaanJadwal().getCatatan() : null);
                    e.put("startHour", b.getWaktu().getHour());

                    String hariIndonesia = hariMapping.getOrDefault(b.getHari().toUpperCase(), b.getHari());
                    System.out.println("DEBUG: Menambahkan Bimbingan id=" + b.getIdBimbingan() + " ke hari " + hariIndonesia
                            + " jam=" + b.getWaktu());

                    jadwalPerHari.computeIfAbsent(hariIndonesia, k -> new ArrayList<>()).add(e);
                }
            }
        }

        // --- Cetak jadwalPerHari lengkap untuk debug ---
        System.out.println("===== DEBUG JADWAL PER HARI =====");
        for (String h : jadwalPerHari.keySet()) {
            System.out.println("Hari: " + h);
            for (Map<String,Object> item : jadwalPerHari.get(h)) {
                System.out.println("  " + item);
            }
        }

        List<String> hariList = Arrays.asList("Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu");

        model.addAttribute("hariList", hariList);
        model.addAttribute("jadwalPerHari", jadwalPerHari);
        model.addAttribute("nama", mahasiswa.getNama());

        return "JadwalMahasiswa";
    }





}
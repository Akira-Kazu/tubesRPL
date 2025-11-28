package com.example.demo.Mahasiswa;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MahasiswaController {
    @GetMapping("/mahasiswa")
    public String beranda(){
        return "BerandaMahasiswa";
    }

     @GetMapping("/menu")
    public String menu(){
        return "BerandaMahasiswa";
    }

    @GetMapping("/riwayat")
    public String riwayat() {
        return "riwayatBimbinganMahasiswa"; // templates/riwayat.html
    }

    @GetMapping("/pengajuan")
    public String pengajuan() {
        return "pengajuanFormMahasiswa"; // templates/pengajuan.html
    }

    @GetMapping("/kelola")
    public String kelola() {
        return "kelolaPengajuanmahasiswa"; // templates/kelola.html
    }

    @GetMapping("/progress")
    public String progress() {
        return "progressTAMahasiswa"; // templates/progress.html
    }

    @GetMapping("/jadwal")
    public String jadwal() {
        return "JadwalMahasiswa"; // templates/jadwal.html
    }

     @GetMapping("/inbox")
    public String inbox() {
        return "inboxMahasiswa"; // templates/jadwal.html
    }



}

package com.example.demo.service;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Repository.BimbinganRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BimbinganService {

    private final BimbinganRepository bimbinganRepository;

    @Autowired
    public BimbinganService(BimbinganRepository bimbinganRepository) {
        this.bimbinganRepository = bimbinganRepository;
    }

    public Bimbingan getById(Long id) {
        return bimbinganRepository.findById(id).orElse(null);
    }
 public List<Bimbingan> getBimbinganUntukDosen(String email) {
    return bimbinganRepository.findByPermintaanJadwal_Dosen_Email(email);
}



    public List<Bimbingan> getBimbinganUntukMahasiswa(String email) {
        return bimbinganRepository.findByPermintaanJadwal_Mahasiswa_Email(email);
    }


    public void setKomentar(Long idBimbingan, String komentar) {
        Bimbingan b = bimbinganRepository.findById(idBimbingan).orElse(null);
        if (b != null) {
            b.setKomentarDosen(komentar);
            bimbinganRepository.save(b);
        }
    }

    public Bimbingan getByPermintaanId(Long idPermintaan) {
        return bimbinganRepository.findByPermintaanJadwal_Id(idPermintaan);
                 // atau handle sesuai kebutuhan
    }

    // Simpan bimbingan baru
    public Bimbingan saveBimbingan(Bimbingan bimbingan) {
        return bimbinganRepository.save(bimbingan);
    }

    // Hapus bimbingan berdasarkan id
public void deleteBimbingan(Long idBimbingan) {
    bimbinganRepository.deleteById(idBimbingan);
}


    public Map<String, List<Bimbingan>> getBimbinganPerHari(String emailMahasiswa) {
        List<Bimbingan> bimbinganList = getBimbinganUntukMahasiswa(emailMahasiswa);
        Map<String, List<Bimbingan>> jadwalPerHari = new HashMap<>();
        for (Bimbingan bimbingan : bimbinganList) {
            String hari = bimbingan.getHari();
            jadwalPerHari.computeIfAbsent(hari, k -> new ArrayList<>())
                    .add(bimbingan);
        }
        return jadwalPerHari;
    }

}

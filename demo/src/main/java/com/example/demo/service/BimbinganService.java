package com.example.demo.service;

import com.example.demo.Entity.Bimbingan;
import com.example.demo.Repository.BimbinganRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class BimbinganService {

    private final BimbinganRepository bimbinganRepository;

    @Autowired
    public BimbinganService(BimbinganRepository bimbinganRepository) {
        this.bimbinganRepository = bimbinganRepository;
    }

 public List<Bimbingan> getBimbinganUntukDosen(String email) {
    return bimbinganRepository.findByPermintaanJadwal_Dosen_Email(email);
}


    // Ambil semua bimbingan
    public List<Bimbingan> getAllBimbingan() {
        return bimbinganRepository.findAll();
    }

    // Simpan bimbingan baru
    public Bimbingan saveBimbingan(Bimbingan bimbingan) {
        return bimbinganRepository.save(bimbingan);
    }

    // Hapus bimbingan berdasarkan id
public void deleteBimbingan(Long idBimbingan) {
    bimbinganRepository.deleteById(idBimbingan);
}

}

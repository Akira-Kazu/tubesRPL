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

}

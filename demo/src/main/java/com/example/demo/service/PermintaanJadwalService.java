package com.example.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.Repository.PermintaanJadwalRepository;
import com.example.demo.Entity.PermintaanJadwal;

@Service
public class PermintaanJadwalService {
    private final PermintaanJadwalRepository repository;

    @Autowired
    public PermintaanJadwalService(PermintaanJadwalRepository repository) {
        this.repository = repository;
    }



    // ✅ INI method yang dipanggil DosenController
    public List<PermintaanJadwal> getRiwayatBimbinganDosen(String emailDosen) {
        return repository.findByDosen_Email(emailDosen);
    }

    public List<PermintaanJadwal> getPengajuanUntukDosen(String emailDosen) {
        return repository.findByDosen_Email(emailDosen);
    }

    public PermintaanJadwal getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<PermintaanJadwal> getRiwayatApprovedUntukMahasiswa(String email) {
        return repository.findAllByMahasiswa_EmailAndStatus(email, "Approved");
    }
    public PermintaanJadwal getDetail(Long id) {
        return repository.findById(id).orElse(null);
    }

}

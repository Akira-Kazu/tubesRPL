package com.application.sibita.mahasiswa;
import org.springframework.stereotype.Repository;

@Repository
public interface MahasiswaRepository {
    Mahasiswa getProfilLengkap(String idPengguna);
}
package com.application.sibita.bimbingan;
import org.springframework.stereotype.Repository;

@Repository
public interface BimbinganRepository {
    Bimbingan bimbinganTerdekat(String idMahasiswa);
}
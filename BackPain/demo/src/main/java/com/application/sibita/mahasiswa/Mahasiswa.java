package com.application.sibita.mahasiswa;

import com.application.sibita.pengguna.Pengguna;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Mahasiswa extends Pengguna {
    private String IdMahasiswa;
    private String tahapTA; 

    private int semesterAktif;
    private String tahapSkripsi;
    private String judulpSkripsi;
    private String dosen1;
    private String dosen2;
    private int banyakSesiPraUts;
    private int banyakSesiPascaUts;

    public Mahasiswa(String idPengguna, String username, String password,
                 String nama, boolean statusAktif, String tipeAkun,
                 LocalDateTime lastLogin) {
        super(idPengguna, username, password, nama, statusAktif, tipeAkun, lastLogin);
    }
}
package com.application.sibita.mahasiswa;

import lombok.RequiredArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.application.sibita.pengguna.Pengguna;

@Repository
@RequiredArgsConstructor
public class MahasiswaJdbc implements MahasiswaRepository {
    private final JdbcTemplate jdbcTemplate;

    public Mahasiswa mapRowToPengguna(ResultSet rs, int rowNum) throws SQLException {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setIdPengguna(rs.getString("IdPengguna"));
        mahasiswa.setUsername(rs.getString("username"));
        mahasiswa.setPassword(rs.getString("password"));
        mahasiswa.setNama(rs.getString("nama"));
        mahasiswa.setStatusAktif(rs.getBoolean("statusAktif"));
        mahasiswa.setTipeAkun(rs.getString("tipeAkun"));
        mahasiswa.setLastLogin(rs.getTimestamp("lastLogin").toLocalDateTime());

        mahasiswa.setIdMahasiswa(rs.getString(null));

        return mahasiswa;
    }

    public Mahasiswa getById(String id) {
        String sql = "SELECT * FROM Pengguna WHERE IdPengguna = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToPengguna, id);
    }

    public Mahasiswa getProfilLengkap(String idPengguna){
        String sql = """
                SELECT 
                    p.nama, m.TahapTA, ta.TopikTA, ta.IdTa, ta.TanggalUTS
                FROM Mahasiswa m
                JOIN Pengguna p ON m.IdPengguna = p.IdPengguna
                JOIN TugasAkhir ta ON m.IdPengguna = ta.IdMahasiswa
                WHERE m.IdPengguna = ?
                """
        ;
        List<Mahasiswa> res = jdbcTemplate.query(sql, this::mapRowToPengguna, idPengguna);
        // return null;
        
    }
}
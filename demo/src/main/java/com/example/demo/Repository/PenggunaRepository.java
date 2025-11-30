package com.example.demo.Repository;

import com.example.demo.Entity.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, String> {

    Pengguna findByEmailAndPassword(String email, String password);
}

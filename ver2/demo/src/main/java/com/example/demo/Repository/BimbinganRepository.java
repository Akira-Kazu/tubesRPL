package com.example.demo.Repository;
import com.example.demo.Entity.Bimbingan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BimbinganRepository extends JpaRepository<Bimbingan, Long> {}
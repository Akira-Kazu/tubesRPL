package com.application.sibita.ruangan;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuanganRepository {
    List<Ruangan> getAllRuang();
}
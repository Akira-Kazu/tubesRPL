package com.application.sibita.mahasiswa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MahasiswaService {
    @Autowired
    private MahasiswaRepository mahasiswaRepository;

    
}

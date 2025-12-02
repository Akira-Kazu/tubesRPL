package com.application.sibita.bimbingan;

import com.application.sibita.jadwal.JadwalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BimbinganService {

    private final BimbinganRepository bimbinganRepository;


}
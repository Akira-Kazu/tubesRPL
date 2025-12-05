-- TABLE: Pengguna (MAHASISWA / DOSEN / ADMIN)

CREATE TABLE pengguna (
    email VARCHAR(100) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    nama VARCHAR(100) NOT NULL,
    role INTEGER NOT NULL CHECK (role IN (1, 2, 3))
    -- 1 = Mahasiswa, 2 = Dosen, 3 = Admin
);

-- TABLE: Mata Kuliah

CREATE TABLE mata_kuliah (
    id_mk SERIAL PRIMARY KEY,
    nama_mk VARCHAR(100) NOT NULL,
    hari VARCHAR(20),
    waktu TIME
);

-- TABLE: Terdaftar (Mahasiswa mengambil MK)

CREATE TABLE terdaftar (
    email VARCHAR(100)
        REFERENCES pengguna(email)
        ON DELETE CASCADE,
    id_mk INTEGER
        REFERENCES mata_kuliah(id_mk)
        ON DELETE CASCADE,
    PRIMARY KEY (email, id_mk)
);

-- TABLE: Tugas Akhir

CREATE TABLE tugas_akhir (
    id_ta SERIAL PRIMARY KEY,
    judul TEXT NOT NULL,
    topik TEXT,
    komentar TEXT,
    email VARCHAR(100)
        REFERENCES pengguna(email)
        ON DELETE SET NULL
);


-- TABLE: Permintaan Jadwal

CREATE TABLE permintaan_jadwal (
    id_permintaan SERIAL PRIMARY KEY,
    tanggal DATE NOT NULL,
    catatan TEXT,
    
    email_mahasiswa VARCHAR(100)
        REFERENCES pengguna(email)
        ON DELETE CASCADE,

    email_dosen VARCHAR(100)
        REFERENCES pengguna(email)
        ON DELETE CASCADE
);

-- TABLE: Bimbingan

CREATE TABLE bimbingan (
    id_bimbingan SERIAL PRIMARY KEY,
    is_bimbingan BOOLEAN DEFAULT TRUE,
    lokasi VARCHAR(100),
    hari VARCHAR(20),
    waktu TIME,
    
    id_permintaan INTEGER 
        REFERENCES permintaan_jadwal(id_permintaan)
        ON DELETE CASCADE
);

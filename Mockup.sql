
-- ==========================================
-- INSERT: Pengguna
-- ==========================================
INSERT INTO pengguna (email, password, nama, role) VALUES
-- Mahasiswa
('6182301096@student.unpar.ac.id', 'pass123', 'Nathaniel Pratama', 1),
('6182301100@student.unpar.ac.id', 'pass123', 'Gabriel Wibowo', 1),
('6182301123@student.unpar.ac.id', 'pass123', 'Michelle Caroline', 1),
('6182301155@student.unpar.ac.id', 'pass123', 'Jonathan Lee', 1),
('6182301188@student.unpar.ac.id', 'pass123', 'Celine Anggraini', 1),
('6182301199@student.unpar.ac.id', 'pass123', 'Felix Santoso', 1),

-- Dosen
('dosen001@unpar.ac.id', 'dosenpass', 'Dr. Maria Suryadi', 2),
('dosen002@unpar.ac.id', 'dosenpass', 'Ir. Alvin Hartono', 2),
('dosen003@unpar.ac.id', 'dosenpass', 'Prof. Yudi Kurnia', 2),

-- Admin
('admin@unpar.ac.id', 'adminpass', 'Raisa Putri', 3),
('admin2@unpar.ac.id', 'adminpass', 'Michael Gunawan', 3);

-- ==========================================
-- INSERT: Mata Kuliah
-- ==========================================
INSERT INTO mata_kuliah (nama_mk, hari, waktu) VALUES
('Basis Data', 'Senin', '07:30'),
('Algoritma & Struktur Data', 'Rabu', '09:00'),
('Kecerdasan Buatan', 'Kamis', '13:00'),
('Rekayasa Perangkat Lunak', 'Selasa', '10:00'),
('Jaringan Komputer', 'Jumat', '08:00'),
('Pemrograman Web', 'Senin', '14:00');

-- ==========================================
-- INSERT: Terdaftar (Mahasiswa mengambil MK)
-- ==========================================
INSERT INTO terdaftar (email, id_mk) VALUES
('6182301096@student.unpar.ac.id', 1),
('6182301096@student.unpar.ac.id', 2),
('6182301096@student.unpar.ac.id', 6),

('6182301100@student.unpar.ac.id', 1),
('6182301100@student.unpar.ac.id', 3),

('6182301123@student.unpar.ac.id', 2),
('6182301123@student.unpar.ac.id', 4),

('6182301155@student.unpar.ac.id', 3),
('6182301155@student.unpar.ac.id', 5),

('6182301188@student.unpar.ac.id', 1),
('6182301188@student.unpar.ac.id', 4),
('6182301188@student.unpar.ac.id', 6),

('6182301199@student.unpar.ac.id', 2),
('6182301199@student.unpar.ac.id', 3),
('6182301199@student.unpar.ac.id', 5);

-- ==========================================
-- INSERT: Tugas Akhir
-- ==========================================
INSERT INTO tugas_akhir (judul, topik, komentar, email) VALUES
('Rancang Bangun Sistem Bimbingan TA', 'Sistem Informasi', 'Revisi BAB 2', '6182301096@student.unpar.ac.id'),
('Optimasi Backtracking untuk Puzzle', 'Algoritma', NULL, '6182301100@student.unpar.ac.id'),
('Sistem Prediksi Cuaca Menggunakan ML', 'Machine Learning', 'Perbaiki dataset', '6182301123@student.unpar.ac.id'),
('Deteksi Anomali Jaringan', 'Keamanan Siber', NULL, '6182301155@student.unpar.ac.id'),
('Analisis Sentimen Review Produk', 'Data Mining', 'Tingkatkan akurasi model', '6182301188@student.unpar.ac.id');

-- ==========================================
-- INSERT: Permintaan Jadwal
-- ==========================================
INSERT INTO permintaan_jadwal (tanggal, catatan, email_mahasiswa, email_dosen) VALUES
('2025-11-28', 'Diskusi awal TA', '6182301096@student.unpar.ac.id', 'dosen001@unpar.ac.id'),
('2025-12-03', 'Revisi BAB 1', '6182301096@student.unpar.ac.id', 'dosen001@unpar.ac.id'),

('2025-11-29', 'Bahas model AI', '6182301100@student.unpar.ac.id', 'dosen002@unpar.ac.id'),

('2025-12-01', 'Cek progress dataset', '6182301123@student.unpar.ac.id', 'dosen003@unpar.ac.id'),

('2025-12-04', 'Review arsitektur sistem', '6182301155@student.unpar.ac.id', 'dosen002@unpar.ac.id'),

('2025-12-05', 'Cek hasil preprocessing', '6182301188@student.unpar.ac.id', 'dosen003@unpar.ac.id');

-- ==========================================
-- INSERT: Bimbingan
-- ==========================================
INSERT INTO bimbingan (is_bimbingan, lokasi, hari, waktu, id_permintaan) VALUES
(TRUE, 'Ruang D-402', 'Senin', '10:00', 1),
(TRUE, 'Ruang C-305', 'Rabu', '14:00', 2),

(TRUE, 'Ruang E-201', 'Kamis', '09:00', 3),

(TRUE, 'Ruang D-301', 'Senin', '11:00', 4),

(TRUE, 'Ruang C-110', 'Jumat', '13:00', 5),

(TRUE, 'Ruang H-202', 'Selasa', '15:00', 6);
SELECT * FROM pengguna
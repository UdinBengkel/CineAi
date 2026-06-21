# 🎬 CineAI — Aplikasi Rekomendasi Film

> Aplikasi Android untuk menemukan, menyimpan, dan mendapatkan rekomendasi film secara personal berdasarkan preferensi genre pengguna.

---

## 👤 Identitas Mahasiswa

| Keterangan      | Detail             |
| --------------- | ------------------ |
| **Nama**        | Syafarudiansya     |
| **NIM**         | 312410381          |
| **Kelas**       | I241A              |
| **Mata Kuliah** | Pemrograman Mobile |

---

## 📱 Deskripsi Aplikasi

**CineAI** adalah aplikasi rekomendasi film berbasis Android yang memanfaatkan data real-time dari **TMDB API** (The Movie Database). Aplikasi ini membantu pengguna menemukan, mencari, dan menyimpan film favorit, serta secara otomatis memberikan rekomendasi film baru berdasarkan pola genre dari watchlist pengguna.

---

## 🖥️ Tampilan UI (User Interface)

### 1. Splash Screen

Halaman pembuka aplikasi yang muncul saat pertama kali app dibuka.

**Komponen UI:**
- Logo aplikasi berbentuk kotak merah dengan ikon segitiga play putih di tengah
- Nama aplikasi **"CINE`AI`"** — bagian "CINE" putih, bagian "AI" merah
- Tagline *"YOUR AI MOVIE GUIDE"*
- **Lokasi GPS user** yang ditampilkan secara otomatis (contoh: `📍 Kabupaten Bekasi 🇮🇩`) menggunakan FusedLocationProvider, hasil reverse-geocoding koordinat menjadi nama daerah dan kode negara
- Garis loading merah tipis dengan animasi sebelum masuk ke halaman utama

> Fitur GPS meminta izin lokasi dari user. Jika ditolak, lokasi default "Indonesia 🇮🇩" ditampilkan.

---

### 2. Beranda (Home)

Halaman utama yang menampilkan daftar film dalam beberapa kategori, tanpa toolbar — nama aplikasi tampil langsung di bagian atas konten.

**Komponen UI:**
- **SwipeRefreshLayout** untuk memperbarui data dengan gestur tarik ke bawah
- **Seksi "✦ Film yang Mungkin Kamu Suka"** *(muncul otomatis hanya jika watchlist tidak kosong)* — rekomendasi film dihitung berdasarkan genre yang paling sering muncul di watchlist pengguna, lalu diambil dari TMDB Discover API
- **Seksi "Tayang Sekarang"** — film yang sedang tayang di bioskop
- **Seksi "Trending Now"** — film paling populer
- **Seksi "Rating Tertinggi"** — film dengan rating terbaik
- Setiap seksi memiliki tautan **"Lihat Semua →"** dan menggunakan **RecyclerView horizontal scroll**
- Setiap kartu film menampilkan: poster, judul, tahun rilis, dan badge rating bintang merah
- **Bottom Navigation Bar** dengan 3 tab: Beranda, Cari, Watchlist

---

### 3. Pencarian (Search / Discover)

Halaman untuk menjelajah dan mencari film.

**Komponen UI:**
- Judul halaman **"DISCOVER"** dengan tipografi besar dan tebal
- **Search bar** flat berbentuk pill dengan ikon pencarian dan ikon filter terintegrasi
- **Grid genre berwarna** (Aksi, Sci-Fi, Komedi, Horor, Drama, Animasi, Thriller, Romansa) yang tampil sebagai tampilan default sebelum pengguna mengetik
- **Panel Filter** (collapsible) berisi spinner tahun rilis dan chip group genre
- Pencarian berjalan **real-time** dengan jeda 600ms (debounce)
- Hasil pencarian/filter ditampilkan dalam **RecyclerView grid 2 kolom**

---

### 4. Detail Film

Halaman lengkap informasi satu film.

**Komponen UI:**
- **CollapsingToolbarLayout** dengan backdrop film yang mengecil saat di-scroll, dilapisi gradient gelap
- **Poster film** vertikal di sebelah informasi utama
- Informasi film: **judul**, **tagline**, **rating bintang merah**, **tahun rilis**, **durasi**
- **Chip group genre** sebagai informasi (tidak dapat diklik)
- **Tombol aksi:**
  - `Tonton Sekarang` / `Beli Tiket` — tombol utama merah, otomatis menyesuaikan apakah film tersedia di platform streaming atau masih tayang di bioskop
  - Ikon trailer — membuka YouTube untuk menonton trailer resmi
  - Ikon watchlist (bookmark) — toggle simpan/hapus dari watchlist
- **Sinopsis** lengkap film
- **RecyclerView horizontal** berisi film-film rekomendasi serupa di bagian bawah

---

### 5. Watchlist

Halaman daftar film yang telah disimpan oleh pengguna, murni sebagai daftar simpan tanpa fitur tracking status tontonan.

**Komponen UI:**
- Judul halaman **"MY WATCHLIST"** dengan jumlah film tersimpan di bawahnya
- **RecyclerView list vertikal** menampilkan setiap film: poster, judul, tahun, rating, dan cuplikan sinopsis
- Tombol **hapus (✕)** di setiap item
- **Empty state**: ikon bookmark redup, teks penjelasan, serta info bahwa menambahkan film ke watchlist akan memicu rekomendasi otomatis di halaman Beranda

---

## ⚙️ Fitur Lengkap

| Fitur | Keterangan |
|---|---|
| 🏠 Beranda Multi-Seksi | Tayang Sekarang, Trending Now, Rating Tertinggi |
| 🎯 Rekomendasi Berbasis Genre | Dihitung otomatis dari frekuensi genre pada watchlist, ditampilkan di Beranda |
| 🔍 Pencarian Real-time | Debounce 600ms, ringan untuk server |
| 🎨 Discover by Genre | Grid genre berwarna untuk eksplorasi cepat |
| 🎛️ Filter Lanjutan | Filter berdasarkan genre dan tahun rilis |
| 🎬 Detail Film Lengkap | Backdrop, poster, sinopsis, genre chip, rating |
| ▶️ Trailer YouTube | Prioritas trailer resmi berbahasa Indonesia, fallback ke Inggris |
| 📺 Streaming / Tiket | Deteksi otomatis ketersediaan di platform streaming atau bioskop |
| 🔖 Watchlist Offline | Tersimpan lokal menggunakan Room Database |
| 📍 GPS Lokasi | Menampilkan lokasi pengguna di splash screen via reverse-geocoding |
| 🌙 Dark Cinematic Theme | Palet warna gelap dengan aksen merah |

---

## 🛠️ Tech Stack

| Kategori | Teknologi |
|---|---|
| Bahasa Pemrograman | Java |
| Minimum SDK | API 24 (Android 7.0) |
| UI Framework | XML Layout + Material Design 3 |
| Arsitektur | Repository Pattern + LiveData |
| HTTP Client | Retrofit2 + OkHttp3 |
| JSON Parser | Gson |
| Image Loading | Glide 4 |
| Database Lokal | Room (SQLite) |
| Lokasi | Google Play Services — FusedLocationProvider |
| Data Film | TMDB API v3 |

---

## 🚀 Cara Menjalankan

### Prasyarat
- Android Studio Hedgehog atau lebih baru
- Java Development Kit (JDK) 8+
- Device/Emulator dengan Android 7.0 (API 24) ke atas
- Koneksi internet aktif

### Langkah Setup

1. **Clone repository ini**
   ```bash
   git clone https://github.com/UdinBengkel/CineAi.git
   ```

2. **Buka di Android Studio**
   ```
   File > Open > pilih folder CineAi
   ```

3. **Setup API Key TMDB**

   Copy file `local.properties.example` menjadi `local.properties` di root folder proyek:
   ```bash
   cp local.properties.example local.properties
   ```
   Lalu isi dengan API key TMDB kamu:
   ```properties
   TMDB_API_KEY=isi_api_key_asli_disini
   ```

   > File `local.properties` tidak ikut ter-push ke GitHub (sudah diatur di `.gitignore`) sehingga API key tetap aman.

4. **Sync Gradle** → klik **Sync Now**

5. **Run** → `Shift + F10` atau klik tombol ▶

### Cara Mendapatkan API Key TMDB

Daftar gratis di [themoviedb.org/settings/api](https://www.themoviedb.org/settings/api)

---

## 📦 Dependencies Utama

```gradle
// UI & Material
implementation 'com.google.android.material:material:1.12.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'

// Networking
implementation 'com.squareup.retrofit2:retrofit:2.11.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// Image
implementation 'com.github.bumptech.glide:glide:4.16.0'

// Database
implementation 'androidx.room:room-runtime:2.6.1'

// Location / GPS
implementation 'com.google.android.gms:play-services-location:21.3.0'

// Chip Layout
implementation 'com.google.android.flexbox:flexbox:3.0.0'
```

---

## 📋 Progress SCRUM (ClickUp)

🔗 **Link ClickUp:** [https://app.clickup.com/90181768480/v/li/901811634479](https://app.clickup.com/90181768480/v/li/901811634479)

| Fase | Nama | Status |
|---|---|---|
| Fase 1 | Persiapan & Desain UI | ✅ COMPLETE |
| Fase 2 | Networking & API Integration | ✅ COMPLETE |
| Fase 3 | Tampilan List & Gambar | ✅ COMPLETE |
| Fase 4 | Detail & Finishing | ✅ COMPLETE |

---

## 🎥 Demo Aplikasi

🔗 [Klik di sini untuk menonton demo aplikasi CineAI](https://drive.google.com/file/d/1GIUxYy0OgKcTdPUWwtHvFBxu9_TQKSl7/view?usp=sharing)

---

## 📸 Screenshots

| Home | Search | Detail | Watchlist |
|---|---|---|---|
| ![Home](gambar/home.jpg) | ![Search](gambar/search.jpg) | ![Detail](gambar/detail.jpg) | ![Watchlist](gambar/watchlist.jpg) |

---

## 📄 Lisensi

Project ini dibuat untuk keperluan tugas akademik di **Universitas Pelita Bangsa**.
Data film disediakan oleh [The Movie Database (TMDB)](https://www.themoviedb.org/).

# 🎬 CineAI — Aplikasi Rekomendasi Film Berbasis AI

> Aplikasi Android untuk menemukan, menyimpan, dan mendapatkan rekomendasi film secara personal menggunakan kecerdasan buatan (AI).

---

## 👤 Identitas Mahasiswa

| Keterangan | Detail |

|---|---|

| **Nama** | Syafarudiansya |

| **NIM** | 312410381 |

| **Kelas** | I241A |

| **Mata Kuliah** | Pemrograman Mobile |

---

## 📱 Deskripsi Aplikasi

**CineAI** adalah aplikasi rekomendasi film berbasis Android yang memanfaatkan data real-time dari **TMDB API** (The Movie Database) dan kecerdasan buatan dari **Google Gemini AI**. Aplikasi ini dirancang untuk membantu pengguna menemukan film yang sesuai dengan selera mereka secara personal dan otomatis.

---

## 🖥️ Tampilan UI (User Interface)

### 1. Splash Screen
Halaman pembuka aplikasi yang muncul saat pertama kali app dibuka.

**Komponen UI:**
- Ikon film emoji sebagai logo aplikasi
- Nama aplikasi **"CineAI"** dengan ukuran besar dan bold
- Tagline *"Temukan Film Terbaik Untukmu"*
- **Lokasi GPS user** yang ditampilkan secara otomatis (contoh: `📍 Kabupaten Bekasi 🇮🇩`) menggunakan FusedLocationProvider
- Loading indicator (ProgressBar) selama aplikasi memuat

> Fitur GPS meminta izin lokasi dari user. Jika ditolak, lokasi default "Indonesia 🇮🇩" ditampilkan.

---

### 2. Beranda (Home)
Halaman utama yang menampilkan daftar film dalam beberapa kategori.

**Komponen UI:**
- **Toolbar** dengan judul "CineAI" di bagian atas
- **SwipeRefreshLayout** untuk memperbarui data dengan gestur tarik ke bawah
- **Seksi "🎯 Film [Genre] Untukmu"** *(muncul otomatis jika watchlist tidak kosong)* — menampilkan rekomendasi film berdasarkan genre favorit user dari watchlist, dengan badge "Dari Watchlist"
- **Seksi "🎬 Tayang Sekarang"** — film yang sedang tayang di bioskop
- **Seksi "🔥 Populer"** — film paling banyak ditonton
- **Seksi "⭐ Rating Tertinggi"** — film dengan rating terbaik
- Setiap seksi menggunakan **RecyclerView horizontal scroll**
- Setiap kartu film menampilkan: poster, judul, tahun rilis, dan badge rating bintang emas
- **Bottom Navigation Bar** dengan 3 tab: Beranda, Cari, Watchlist

---

### 3. Pencarian (Search)
Halaman untuk mencari film dan menyaring berdasarkan filter.

**Komponen UI:**
- **Search bar** (TextInputEditText) dengan ikon search dan placeholder "Cari judul film…"
- **Tombol Filter** di sebelah kanan search bar untuk membuka/menutup panel filter
- **Panel Filter** (collapsible) berisi:
  - Spinner dropdown **tahun rilis** (dari tahun sekarang hingga 1990)
  - **Chip group genre** yang bisa dipilih satu genre (Aksi, Drama, Komedi, dll.)
  - Tombol **"Terapkan"** dan **"Reset"**
- **Teks jumlah hasil** (contoh: "245 hasil ditemukan")
- **RecyclerView grid 2 kolom** untuk menampilkan hasil pencarian/filter
- Pencarian berjalan secara **real-time** dengan jeda 600ms (debounce)

---

### 4. Detail Film
Halaman lengkap informasi satu film yang dibuka dari mana saja.

**Komponen UI:**
- **CollapsingToolbarLayout** — backdrop film mengecil saat di-scroll
- **Gambar backdrop** film sebagai header dengan efek parallax
- **Poster film** (vertikal) di pojok kiri bawah header
- Informasi film: **judul**, **tagline** (miring), **rating bintang emas**, **tahun rilis**, **durasi**
- **Chip group genre** (tidak bisa diklik, hanya informasi)
- **3 tombol aksi:**
  - `▶ Trailer` — membuka YouTube untuk menonton trailer
  - `▶ Tonton Sekarang` / `🎟 Beli Tiket` — membuka platform streaming atau situs beli tiket bioskop (otomatis tergantung status film)
  - `+ Watchlist` / `✓ Di Watchlist` — menyimpan atau menghapus dari watchlist
- **Sinopsis** lengkap film
- **RecyclerView horizontal** berisi film-film rekomendasi serupa di bagian bawah

---

### 5. Watchlist
Halaman daftar film yang telah disimpan oleh user.

**Komponen UI:**
- **Header** berisi jumlah film di watchlist (contoh: "5 film dalam watchlist")
- **Tombol "🤖 Rekomendasi AI"** — mengirim data watchlist ke Gemini AI untuk analisis selera
- **Card hasil AI** (muncul setelah tombol ditekan) berisi:
  - Analisis selera film user dalam 2-3 kalimat
  - Daftar 5-8 rekomendasi film beserta alasan dan genre
  - Tombol silang untuk menutup card
- **RecyclerView list vertikal** menampilkan setiap film di watchlist dengan: poster kecil, judul, rating, tahun, dan cuplikan sinopsis
- Tombol **hapus (×)** di setiap item untuk menghapus film dari watchlist
- **Empty state** (jika watchlist kosong): ikon film besar + teks "Watchlist kamu masih kosong"

---

## ⚙️ Fitur Lengkap

| Fitur | Keterangan |
|---|---|
| 🏠 Beranda Multi-Seksi | Now Playing, Popular, Top Rated, dan rekomendasi personal |
| 🎯 Rekomendasi Personal | Otomatis berdasarkan genre dari watchlist user |
| 🔍 Pencarian Real-time | Debounce 600ms, tidak membebani server |
| 🎛️ Filter Lanjutan | Filter berdasarkan genre dan tahun rilis |
| 🎬 Detail Film Lengkap | Backdrop, poster, sinopsis, genre chip, rating |
| ▶️ Trailer YouTube | Prioritas trailer resmi bahasa Indonesia |
| 📺 Streaming / Tiket | Cek otomatis ketersediaan di streaming atau bioskop |
| 🔖 Watchlist Offline | Tersimpan lokal menggunakan Room Database |
| 🤖 Analisis AI | Gemini AI menganalisis selera dari watchlist |
| 📍 GPS Lokasi | Menampilkan lokasi user di splash screen |
| 🌙 Dark Theme | Tema gelap dengan palet warna sinematik |

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
| AI | Google Gemini 2.0 Flash API |
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
   git clone https://github.com/[USERNAME]/CineAI.git
   ```

2. **Buka di Android Studio**
   ```
   File > Open > pilih folder CineAI
   ```

3. **Isi API Keys** di `app/build.gradle`
   ```groovy
   buildConfigField "String", "TMDB_API_KEY", "\"API_KEY_TMDB_KAMU\""
   ```
   Dan di `AiRecommendationHelper.java`:
   ```java
   private static final String GEMINI_API_KEY = "API_KEY_GEMINI_KAMU";
   ```

4. **Sync Gradle** → klik **Sync Now**

5. **Run** → `Shift + F10` atau klik tombol ▶

### Cara Mendapatkan API Key
- **TMDB:** Daftar gratis di [themoviedb.org/settings/api](https://www.themoviedb.org/settings/api)
- **Gemini AI:** Buat key gratis di [aistudio.google.com/apikey](https://aistudio.google.com/apikey)

---

## 📦 Dependencies Utama

```groovy
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

🔗 **Link ClickUp:** [ISI LINK CLICKUP KAMU]

| Fase | Nama | Status |
|---|---|---|
| Fase 1 | Persiapan & Desain UI | ✅ COMPLETE |
| Fase 2 | Networking & API Integration | ✅ COMPLETE |
| Fase 3 | Tampilan List & Gambar | ✅ COMPLETE |
| Fase 4 | Detail & Finishing | ✅ COMPLETE |
| Fase 5 | Fitur Lanjutan (AI, GPS, Streaming) | ✅ COMPLETE |

---

## 📄 Lisensi

Project ini dibuat untuk keperluan tugas akademik di **Universitas Pelita Bangsa**.  
Data film disediakan oleh [The Movie Database (TMDB)](https://www.themoviedb.org/).

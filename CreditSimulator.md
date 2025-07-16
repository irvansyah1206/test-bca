# Credit Simulator

Aplikasi konsol untuk menghitung cicilan bulanan pinjaman kendaraan berdasarkan aturan bisnis yang telah ditentukan.

## Fitur

-   Kalkulasi cicilan untuk Mobil/Motor, Baru/Bekas.
-   Mendukung input interaktif dari konsol.
-   Mendukung input dari file.
-   Memuat data kalkulasi dari web service.
-   Struktur proyek berbasis MVC.
-   CI/CD pipeline dengan GitHub Actions untuk deployment ke Docker Hub.

## Teknologi

-   Java 17
-   Maven
-   JUnit 5 (untuk testing)
-   Docker
-   GitHub Actions

## Prasyarat

-   Java 17 atau lebih tinggi.
-   Maven.

## Cara Build dan Menjalankan Aplikasi

1.  **Clone Repositori**
    ```bash
    git clone <url-repo-anda>
    cd credit-simulator
    ```

2.  **Berikan Izin Eksekusi pada Skrip**
    ```bash
    chmod +x credit_simulator
    ```

3.  **Menjalankan Aplikasi**

    Skrip `credit_simulator` akan otomatis mem-build proyek (jika perlu) sebelum menjalankannya.

    *   **Mode Interaktif:**
        Jalankan tanpa argumen untuk masuk ke mode interaktif di mana Anda dapat memasukkan perintah.
        ```bash
        ./credit_simulator
        ```
        Perintah yang tersedia: `calculate`, `load`, `show`, `exit`.

    *   **Mode Input File:**
        Sediakan path ke file input sebagai argumen.
        ```bash
        ./credit_simulator file_inputs.txt
        ```

        **Format `file_inputs.txt`:**
        Setiap baris mewakili satu nilai input dengan urutan sebagai berikut:
        ```
        MOBIL
        BARU
        2024
        200000000
        4
        70000000
        ```

## Cara Menjalankan Unit Test

Gunakan Maven untuk menjalankan semua unit test yang ada di dalam proyek.

```bash
mvn test
```

## Alur CI/CD

Proyek ini menggunakan GitHub Actions untuk continuous integration and deployment.

1.  **Trigger**: Setiap `push` atau `pull_request` ke `main` branch.
2.  **Build & Test**: Workflow akan mengkompilasi kode dan menjalankan unit test.
3.  **Package & Deploy**: Jika build dan test berhasil, aplikasi akan di-package ke dalam sebuah Docker image dan di-push ke Docker Hub.

Anda dapat melihat image tersebut di: `https://hub.docker.com/r/NAMA_USER_ANDA/credit-simulator`
```

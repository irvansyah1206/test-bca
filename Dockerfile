# Gunakan base image Java 17 yang ringan
FROM eclipse-temurin:17-jre-alpine

# Set direktori kerja di dalam container
WORKDIR /app

# Salin JAR file yang sudah di-build oleh Maven ke dalam container
COPY target/app.jar .

# Perintah yang akan dijalankan saat container启动
ENTRYPOINT ["java", "-jar", "app.jar"]

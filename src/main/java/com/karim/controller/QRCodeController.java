package com.karim.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.karim.util.QRCodeUtil;

@RestController
public class QRCodeController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // ------------------- TEXT / URL QR -------------------
    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQRCode(@RequestParam String text) throws Exception {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }

        return QRCodeUtil.generateQRCode(text, 300, 300);
    }

    // ------------------- IMAGE UPLOAD -------------------
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile image) throws IOException {

        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid image");
        }

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, image.getBytes());

        // URL that will be encoded inside QR
        String imageUrl = "http://localhost:9090/images/" + fileName;

        return ResponseEntity.ok(imageUrl);
    }

    // ------------------- IMAGE VIEW -------------------
    @GetMapping("/images/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws IOException {

        Path path = Paths.get(uploadDir).resolve(fileName);

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }
}

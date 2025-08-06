package com.spring.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubtitleController {

    private final Path uploadDir = Paths.get("/uploads");

    @PostMapping("/upload")
    public ResponseEntity<String> handleUpload(@RequestParam("file") MultipartFile file) {
        try {
            Files.createDirectories(uploadDir);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);

            RestTemplate restTemplate = new RestTemplate();
            String pythonServiceUrl = "http://transcriber:5001/transcribe";
            Map<String, String> reqBody = Map.of("video_path", filePath.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(reqBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(pythonServiceUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String srtPath = (String) response.getBody().get("srt_path");
                return ResponseEntity.ok("Subtitle ready: /api/download?file=" + Paths.get(srtPath).getFileName());
            }

            return ResponseEntity.status(500).body("Transcription failed.");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadSubtitle(@RequestParam("file") String file) throws IOException {
        Path srtPath = uploadDir.resolve(file);
        if (!Files.exists(srtPath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(srtPath.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"")
                .body(resource);
    }
}

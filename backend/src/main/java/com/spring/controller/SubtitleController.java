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
    public ResponseEntity<Map<String, String>> handleUpload(@RequestParam("file") MultipartFile file) {
        try {
            Files.createDirectories(uploadDir);
            String originalName = file.getOriginalFilename();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String videoName = timestamp + "_" + originalName;
            Path videoPath = uploadDir.resolve(videoName);
            file.transferTo(videoPath);

            // Call Python Whisper service
            RestTemplate restTemplate = new RestTemplate();
            String transcriberUrl = "http://transcriber:5001/transcribe";
            Map<String, String> requestBody = Map.of("video_path", videoPath.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(transcriberUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String srtPath = (String) response.getBody().get("srt_path");
                String srtFileName = Paths.get(srtPath).getFileName().toString();

                return ResponseEntity.ok(Map.of(
                        "uploaded", videoName,
                        "subtitle", srtFileName,
                        "download", "/api/download?file=" + srtFileName
                ));
            }

            return ResponseEntity.status(500).body(Map.of("error", "Transcription failed"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
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

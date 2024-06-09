package com.example.licencespring.controller;

import com.example.licencespring.service.APIKeyService;
import com.example.licencespring.service.APIRequestLogService;
import com.example.licencespring.service.LicensePlateRecognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIController {

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private APIKeyService apiKeyService;

    @Autowired
    private LicensePlateRecognitionService licensePlateRecognitionService;

    @Autowired
    private APIRequestLogService apiRequestLogService;

    @PostMapping("/recognize")
    public ResponseEntity<?> recognizeLicensePlate(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("apiKey") String apiKey,
                                                   @RequestParam(value = "json", required = false) Boolean json) {
        logger.info("Received request to /api/recognize with API key: {}", apiKey);

        if (!apiKeyService.isValidAPIKey(apiKey)) {
            logger.warn("Invalid API Key: {}", apiKey);
            return ResponseEntity.status(403).body("Invalid API Key");
        }

        try {
            Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            Map<String, Object> result = licensePlateRecognitionService.recognizePlate(tempFile.toString());

            Files.delete(tempFile);

            logger.info("Recognition result: {}", result);

            String resultString = new ObjectMapper().writeValueAsString(result);
            apiRequestLogService.logRequest(apiKeyService.getCurrentUserId(apiKey), apiKey, resultString);

            if (Boolean.TRUE.equals(json)) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.ok(resultString);
            }

        } catch (IOException e) {
            logger.error("Failed to process the file", e);
            return ResponseEntity.status(500).body("Failed to process the file");
        }
    }
}

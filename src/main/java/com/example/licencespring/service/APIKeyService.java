package com.example.licencespring.service;

import com.example.licencespring.model.APIKey;
import com.example.licencespring.model.User;
import com.example.licencespring.repository.APIKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class APIKeyService {

    @Autowired
    private APIKeyRepository apiKeyRepository;

    @Autowired
    private UserService userService;

    public boolean isValidAPIKey(String apiKey) {
        return apiKeyRepository.findByKey(apiKey) != null;
    }

    public String getCurrentUserId(String apiKey) {
        APIKey key = apiKeyRepository.findByKey(apiKey);
        return key != null ? key.getUser().getId() : null;
    }

    public APIKey getAPIKeyForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return apiKeyRepository.findFirstByUser(currentUser);
    }

    public APIKey getAPIKeyForUser(User user) {
        return apiKeyRepository.findFirstByUser(user);
    }

    public APIKey generateAndSaveAPIKeyForUser(User user) {
        APIKey existingApiKey = apiKeyRepository.findFirstByUser(user);
        if (existingApiKey != null) {
            apiKeyRepository.delete(existingApiKey);
        }

        APIKey apiKey = new APIKey();
        apiKey.setUser(user);
        apiKey.setKey(generateRandomAPIKey());
        return apiKeyRepository.save(apiKey);
    }

    private String generateRandomAPIKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

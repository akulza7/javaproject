package com.example.licencespring.service;

import com.example.licencespring.model.APIRequestLog;
import com.example.licencespring.repository.APIRequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIRequestLogService {

    @Autowired
    private APIRequestLogRepository apiRequestLogRepository;

    public void logRequest(String userId, String apiKey, String requestDetails) {
        APIRequestLog log = new APIRequestLog(userId, getCurrentTimestamp(), apiKey, requestDetails);
        apiRequestLogRepository.save(log);
    }

    public List<APIRequestLog> getRequestsByUserId(String userId) {
        return apiRequestLogRepository.findByUserId(userId);
    }

    public long getTotalRequestCount() {
        return apiRequestLogRepository.count();
    }

    public long getSuccessfulRecognitionCount() {
        return apiRequestLogRepository.countByRequestDetailsContaining("Recognized Plate");
    }

    public List<APIRequestLog> getAllRequests() {
        return apiRequestLogRepository.findAll();
    }

    private String getCurrentTimestamp() {
        return java.time.LocalDateTime.now().toString();
    }
}
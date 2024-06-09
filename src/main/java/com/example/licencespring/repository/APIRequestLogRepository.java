package com.example.licencespring.repository;

import com.example.licencespring.model.APIRequestLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface APIRequestLogRepository extends MongoRepository<APIRequestLog, String> {
    List<APIRequestLog> findByUserId(String userId);
    long countByRequestDetailsContaining(String keyword);
}

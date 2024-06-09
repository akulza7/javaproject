package com.example.licencespring.repository;

import com.example.licencespring.model.APIKey;
import com.example.licencespring.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface APIKeyRepository extends MongoRepository<APIKey, String> {
    APIKey findByKey(String key);
    APIKey findByUser(User user);
    APIKey findFirstByUser(User user);
}

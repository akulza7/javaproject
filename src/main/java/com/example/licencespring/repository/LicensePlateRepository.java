package com.example.licencespring.repository;

import com.example.licencespring.model.LicensePlate;
import com.example.licencespring.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LicensePlateRepository extends MongoRepository<LicensePlate, String> {
    List<LicensePlate> findByUser(User user);
}

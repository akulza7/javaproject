package com.example.licencespring.controller;

import com.example.licencespring.model.APIKey;
import com.example.licencespring.model.User;
import com.example.licencespring.service.APIKeyService;
import com.example.licencespring.service.APIRequestLogService;
import com.example.licencespring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class APIPageController {

    @Autowired
    private APIKeyService apiKeyService;

    @Autowired
    private UserService userService;

    @Autowired
    private APIRequestLogService apiRequestLogService;

    @GetMapping("/api-page")
    public String getAPIPage(Model model) {
        User currentUser = userService.getCurrentUser();
        APIKey apiKey = apiKeyService.getAPIKeyForUser(currentUser);
        if (apiKey == null) {
            apiKey = apiKeyService.generateAndSaveAPIKeyForUser(currentUser);
        }
        model.addAttribute("apiKey", apiKey.getKey());
        return "api-page";
    }

    @GetMapping("/api-requests")
    public String getAPIRequestsPage(Model model) {
        String currentUserId = userService.getCurrentUser().getId();
        model.addAttribute("requests", apiRequestLogService.getRequestsByUserId(currentUserId));
        return "api-requests";
    }

    @PostMapping("/api-page/reset-apikey")
    public String resetApiKey() {
        User currentUser = userService.getCurrentUser();
        apiKeyService.generateAndSaveAPIKeyForUser(currentUser);
        return "redirect:/api-page";
    }
}

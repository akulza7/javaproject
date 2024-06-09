package com.example.licencespring.controller;

import com.example.licencespring.model.User;
import com.example.licencespring.model.APIKey;
import com.example.licencespring.service.APIKeyService;
import com.example.licencespring.service.APIRequestLogService;
import com.example.licencespring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private APIKeyService apiKeyService;

    @Autowired
    private APIRequestLogService apiRequestLogService;

    @GetMapping
    public String adminDashboard(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalApiRequests", apiRequestLogService.getTotalRequestCount());
        model.addAttribute("totalSuccessfulRecognitions", apiRequestLogService.getSuccessfulRecognitionCount());
        return "admin";
    }

    @GetMapping("/user/{id}")
    public String getUserDetails(@PathVariable("id") String userId, Model model) {
        User user = userService.findById(userId);
        APIKey apiKey = apiKeyService.getAPIKeyForUser(user);
        model.addAttribute("user", user);
        model.addAttribute("apiKey", apiKey != null ? apiKey.getKey() : "No API Key found");
        return "user-details";
    }

    @PostMapping("/user/update")
    public String updateUserRoles(@RequestParam("id") String userId, @RequestParam("roles") List<String> roles, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        user.setRoles(new HashSet<>(roles));
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "Roles updated successfully.");
        return "redirect:/admin/user/" + userId;
    }

    @PostMapping("/user/reset-password")
    public String resetUserPassword(@RequestParam("id") String userId, @RequestParam("newPassword") String newPassword, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "Password reset successfully.");
        return "redirect:/admin/user/" + userId;
    }

    @PostMapping("/user/reset-apikey")
    public String resetApiKey(@RequestParam("id") String userId, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        apiKeyService.generateAndSaveAPIKeyForUser(user);
        redirectAttributes.addFlashAttribute("message", "API Key reset successfully.");
        return "redirect:/admin/user/" + userId;
    }
}

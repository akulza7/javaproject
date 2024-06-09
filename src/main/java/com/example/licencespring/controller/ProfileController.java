package com.example.licencespring.controller;

import com.example.licencespring.model.User;
import com.example.licencespring.service.UserService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @PostMapping("/profile/resetPassword")
    public String resetPassword(@RequestParam("currentPassword") String currentPassword,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmNewPassword") String confirmNewPassword,
                                Model model) {
        User currentUser = userService.getCurrentUser();

        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            model.addAttribute("user", currentUser);
            model.addAttribute("error", "Current password is incorrect");
            return "profile";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("user", currentUser);
            model.addAttribute("error", "New passwords do not match");
            return "profile";
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        currentUser.setPassword(encodedPassword);
        userService.save(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("message", "Password successfully updated");
        return "profile";
    }

    @PostMapping("/profile/enable2fa")
    public String enable2FA(Model model) {
        User currentUser = userService.getCurrentUser();

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();

        currentUser.setTwoFactorSecret(key.getKey());
        currentUser.setTwoFactorEnabled(true);
        userService.save(currentUser);

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL("LicensePlateApp", currentUser.getUsername(), key);
        model.addAttribute("qrCodeUrl", otpAuthURL);
        model.addAttribute("user", currentUser);
        model.addAttribute("otpSetup", true);

        return "profile";
    }

    @PostMapping("/profile/verify2fa")
    public String verify2FA(@RequestParam("otp") Integer otp, Model model) {
        User currentUser = userService.getCurrentUser();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();

        if (gAuth.authorize(currentUser.getTwoFactorSecret(), otp)) {
            model.addAttribute("message", "Two-Factor Authentication has been enabled");
            model.addAttribute("user", currentUser);
            return "profile";
        } else {
            currentUser.setTwoFactorSecret(null);
            currentUser.setTwoFactorEnabled(false);
            userService.save(currentUser);
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("user", currentUser);
            return "profile";
        }
    }

    @PostMapping("/profile/disable2fa")
    public String disable2FA(Model model) {
        User currentUser = userService.getCurrentUser();

        currentUser.setTwoFactorSecret(null);
        currentUser.setTwoFactorEnabled(false);
        userService.save(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("message", "Two-Factor Authentication has been disabled");
        return "profile";
    }
}

package com.example.licencespring.controller;

import com.example.licencespring.model.LicensePlate;
import com.example.licencespring.model.User;
import com.example.licencespring.repository.LicensePlateRepository;
import com.example.licencespring.service.FileStorageService;
import com.example.licencespring.service.LicensePlateRecognitionService;
import com.example.licencespring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LicensePlateRecognitionService licensePlateRecognitionService;

    @Autowired
    private LicensePlateRepository licensePlateRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = userService.getCurrentUser();
        List<LicensePlate> licensePlates = licensePlateRepository.findByUser(currentUser);
        model.addAttribute("licensePlates", licensePlates);

        model.addAttribute("twoFactorStatus", currentUser.isTwoFactorEnabled());

        return "dashboard";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        User currentUser = userService.getCurrentUser();
        String uniqueFilename = fileStorageService.storeFile(file);
        Path imagePath = Paths.get("uploads").resolve(uniqueFilename);
        Map<String, Object> resultData = licensePlateRecognitionService.recognizePlate(imagePath.toString());
        String result = (String) resultData.get("alprResult");

        LicensePlate licensePlate = new LicensePlate((String) resultData.get("recognizedPlate"), imagePath.toString(), currentUser);
        licensePlateRepository.save(licensePlate);

        List<LicensePlate> licensePlates = licensePlateRepository.findByUser(currentUser);
        model.addAttribute("licensePlates", licensePlates);
        model.addAttribute("result", result);

        return "dashboard";
    }
}

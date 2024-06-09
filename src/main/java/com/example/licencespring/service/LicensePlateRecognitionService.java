package com.example.licencespring.service;

import com.example.licencespring.model.LicensePlate;
import com.example.licencespring.model.User;
import com.example.licencespring.repository.LicensePlateRepository;
import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprException;
import com.openalpr.jni.AlprPlate;
import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LicensePlateRecognitionService {

    @Value("${openalpr.configFilePath}")
    private String configFilePath;

    @Value("${openalpr.runtimeDataDir}")
    private String runtimeDataDir;

    @Autowired
    private LicensePlateRepository licensePlateRepository;

    @Autowired
    private UserService userService;

    public Map<String, Object> recognizePlate(String imagePath) {
        Alpr alpr = new Alpr("eu", configFilePath, runtimeDataDir);

        alpr.setTopN(10);
        alpr.setDefaultRegion("md");

        Map<String, Object> resultData = new HashMap<>();
        StringBuilder fullResult = new StringBuilder();

        try {
            AlprResults results = alpr.recognize(imagePath);
            if (results.getPlates().size() > 0) {
                AlprPlateResult plateResult = results.getPlates().get(0);
                fullResult.append("Recognized Plate: ")
                        .append(plateResult.getBestPlate().getCharacters())
                        .append("\nFull ALPR Result:\n");

                for (int i = 0; i < plateResult.getTopNPlates().size(); i++) {
                    AlprPlate plate = plateResult.getTopNPlates().get(i);
                    fullResult.append("    - ").append(plate.getCharacters())
                            .append("\t confidence: ").append(plate.getOverallConfidence()).append("\r\n");
                }

                resultData.put("recognizedPlate", plateResult.getBestPlate().getCharacters());
                resultData.put("alprResult", fullResult.toString());
            } else {
                resultData.put("recognizedPlate", "No plate detected");
                resultData.put("alprResult", "No plate detected in the image");
            }
        } catch (AlprException e) {
            e.printStackTrace();
            resultData.put("error", "Error: " + e.getMessage());
        } finally {
            alpr.unload();
        }

        return resultData;
    }
}

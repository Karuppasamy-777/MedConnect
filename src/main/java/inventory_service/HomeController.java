
package com.medichain.inventory_service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appName", "MedConnect");
        model.addAttribute("message",
                "Essential Medicine Stock-Out Prediction & Redistribution");

        return "index";
    }
}

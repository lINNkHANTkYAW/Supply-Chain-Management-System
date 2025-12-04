package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.service.RatingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RatingPageController {

    private final RatingService ratingService;

    public RatingPageController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/rating")
    public String getRatingPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
        	return "redirect:/userRegistration?form=login"; // Redirect to login if not authenticated
        }
        model.addAttribute("notifications", ratingService.getPendingNotifications(user.getUserId()));
        return "rating"; // Template name matching your HTML file
    }
}
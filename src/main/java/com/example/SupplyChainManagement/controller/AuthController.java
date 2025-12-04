package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    SupplierRepository supplierRepository;
    
    @GetMapping("/")
    public String index() {
        return "index"; // Ensures `index.html` is loaded when browsing `localhost:8080`
    }

    
    @GetMapping("/userRegistration")
    public String role() {
        return "userRegistration"; // Serves role.html from templates
    }
    @GetMapping("/aboutus")
    public String aboutus() {
        return "aboutus"; // Serves role.html from templates
    }
    @GetMapping("/logistics")
    public String logistics() {
        return "logistics"; // Serves role.html from templates
    }
    @GetMapping("/termsandservice")
    public String termsandservice() {
        return "termsandservice"; // Serves role.html from templates
    }
    @GetMapping("/ourservice")
    public String ourservice() {
        return "ourservice"; // Serves role.html from templates
    }
    @GetMapping("/partnerprogram")
    public String partnerprogram() {
        return "partnerprogram"; // Serves role.html from templates
    }
    @GetMapping("/privacyandpolicy")
    public String privacyandpolicy() {
        return "privacyandpolicy"; // Serves role.html from templates
    }
    
    
    
    
    
    
    

    @PostMapping("/register")
    public String registerUser(
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String role,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String company_name,
        @RequestParam(required = false) String contact_info,
        @RequestParam(required = false) String address,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        if (userService.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("signupError", "Email already registered.");
            return "redirect:/userRegistration?form=signup";
        }

        String nameToUse = role.equalsIgnoreCase("customer") ? name : company_name;
        User registeredUser = userService.registerUser(nameToUse, password, email, role, contact_info, address);

        session.setAttribute("loggedInUser", registeredUser); // ✅ Store user in session

        return "redirect:/userRegistration?form=login";
    }

    
    @PostMapping("/login")
    public String loginUser(
        @RequestParam String email,
        @RequestParam String password,
        @RequestParam String role,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        Optional<User> authenticatedUser = userService.authenticateUser(email, password, role);

        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            session.setAttribute("loggedInUser", user);
            return "redirect:/" + role.toLowerCase() + "-dashboard"; // Redirect to role-based dashboard
        } else {
            Optional<User> userByEmail = userService.findByEmail(email);
            
            if (userByEmail.isEmpty()) {
                redirectAttributes.addFlashAttribute("loginError", "❌ No account found with this email.");
            } else {
                Optional<User> userByEmailAndRole = userService.findUserByEmailAndRole(email, role);
                if (userByEmailAndRole.isEmpty()) {
                    redirectAttributes.addFlashAttribute("loginError", "❌ Role mismatch! Please select the correct role.");
                } else {
                    redirectAttributes.addFlashAttribute("loginError", "❌ Incorrect password. Please try again.");
                }
            }
            
            return "redirect:/userRegistration?form=login"; // Redirect back to login form
        }
    }

    
    /* @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/"; // Redirects to home page
    }  */


    
    
    @GetMapping("/{role}-dashboard")
    public String dashboard(@PathVariable String role, Model model) {
        // Add any necessary attributes to the model
    	String viewName = role.toLowerCase() + "-dashboard"; // Ensure it matches Thymeleaf templates
        System.out.println("Loading view: " + viewName);

        return viewName; // This must match the Thymeleaf file name
    }
    
    /* @GetMapping("/supplierChat")
    public String supplierChat(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        
		Optional<Supplier> supplierOptional = supplierRepository.findByUser(user);
        supplierOptional.ifPresent(supplier -> model.addAttribute("supplier", supplier));

        model.addAttribute("user", user);
        return "supplierChat";
    } */
}


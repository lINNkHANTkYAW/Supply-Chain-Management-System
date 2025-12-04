package com.example.SupplyChainManagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.SupplyChainManagement.model.DistriProduct;

@RestController
@RequestMapping("/auth")  
public class AuthApiController {

	@PostMapping("/api/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    if (session != null) {
	        session.invalidate(); // Destroy session
	    }
	    return ResponseEntity.ok("<script>window.location.href='/index.html';</script>");
	}
	
	

}

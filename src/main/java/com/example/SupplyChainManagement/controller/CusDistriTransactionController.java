package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.CusDistriTransaction;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.service.CusDistriTransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:8080")
public class CusDistriTransactionController {

    @Autowired
    private CusDistriTransactionService transactionService;

    @GetMapping("/customer")
    public ResponseEntity<List<CusDistriTransaction>> getCustomerTransactions(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        List<CusDistriTransaction> transactions = transactionService.getTransactionsByCustomerId(user.getUserId());
        return ResponseEntity.ok(transactions);
    }
}

package com.example.SupplyChainManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Keep only if encoding passwords
import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CustomerRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.repository.UserRepository;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private ManufacturerRepository manufacturerRepository;
    @Autowired private DistributorRepository distributorRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private PasswordEncoder passwordEncoder; // Keep only if encoding passwords

    public User registerUser(String username, String password, String email, String role, 
                             String contactInfo, String address) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Keep encoding if needed
        user.setRole(role);

        User savedUser = userRepository.save(user);

        switch (role.toLowerCase()) {
            case "supplier":
                Supplier supplier = new Supplier();
                supplier.setUser(savedUser);
                supplier.setCompanyName(username);
                supplier.setContactInfo(contactInfo);
                supplier.setAddress(address);
                supplierRepository.save(supplier);
                break;
            case "manufacturer":
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setUser(savedUser);
                manufacturer.setCompanyName(username);
                manufacturer.setContactInfo(contactInfo);
                manufacturer.setAddress(address);
                manufacturerRepository.save(manufacturer);
                break;
            case "distributor":
                Distributor distributor = new Distributor();
                distributor.setUser(savedUser);
                distributor.setCompanyName(username);
                distributor.setContactInfo(contactInfo);
                distributor.setAddress(address);
                distributorRepository.save(distributor);
                break;
            case "customer":
                Customer customer = new Customer();
                customer.setUser(savedUser);
                customer.setName(username);  
                customer.setContactInfo(contactInfo);
                customer.setAddress(address);
                customerRepository.save(customer);
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }

        return savedUser; // ✅ Return user to store in session
    }
    
    public Optional<User> authenticateUser(String email, String password, String role) {
        Optional<User> user = userRepository.findByEmailAndRole(email, role);

        if (user.isPresent()) {
            String storedPassword = user.get().getPassword();
            
            // ✅ Check if password matches
            if (passwordEncoder.matches(password, storedPassword)) {
                return user;
            } else {
                System.out.println("❌ Password Mismatch - Entered: " + password + ", Stored: " + storedPassword);
            }
        } else {
            System.out.println("❌ User Not Found - Email: " + email + ", Role: " + role);
        }
            
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    /** ✅ Fetch customer by User ID */
    public Optional<Customer> getCustomerByUserId(Long userId) {
        return customerRepository.findByUser_UserId(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public Distributor getDistributorByUser(User user) {
        return distributorRepository.findByUser(user).orElse(null);
    }
    
    public Optional<User> findUserByEmailAndRole(String email, String role) {
        return userRepository.findByEmailAndRole(email, role);
    }

}

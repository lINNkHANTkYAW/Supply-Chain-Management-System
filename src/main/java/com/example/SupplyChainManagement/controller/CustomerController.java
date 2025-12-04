package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.CusDistriTransaction;
import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.repository.CusOrderItemRepository;
import com.example.SupplyChainManagement.repository.CusOrderRepository;
import com.example.SupplyChainManagement.repository.CustomerRepository;
import com.example.SupplyChainManagement.repository.UserRepository;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.service.CategoryService;
import com.example.SupplyChainManagement.service.CusDistriTransactionService;
import com.example.SupplyChainManagement.service.CustomerOrderService;
import com.example.SupplyChainManagement.service.CustomerService;

import com.example.SupplyChainManagement.service.DistributorService;
import com.example.SupplyChainManagement.service.ProductService;
import com.example.SupplyChainManagement.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CustomerController {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CustomerOrderService customerOrderService;
    @Autowired
    private DistributorService distributorService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired 
    private CusDistriTransactionService transactionService;
    /** 
     * ✅ Customer Dashboard 
     */
    @GetMapping("/customer-dashboard")
    public String customerDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        List<Category> categories = categoryService.getAllCategories();
        // System.out.println("Categories: " + categories);
        List<DistriProduct> trendingProducts = productService.getTrendingProducts();
        List<DistriProduct> newArrivals = productService.getNewArrivals();

        // Convert image bytes to Base64 string for each product
        List<Map<String, Object>> trendingProductsWithImages = trendingProducts.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getProductId());
            map.put("name", product.getName());
            map.put("price", product.getPrice());
            map.put("rating", product.getRating());
            map.put("image", product.getImage() != null ? product.getImage() : "/img/default.png");
            if (product.getDistributor() != null) {
                map.put("distributorId", product.getDistributor().getDistributorId());
                map.put("distributorName", product.getDistributor().getCompanyName());
            } else {
                map.put("distributorId", null);
                map.put("distributorName", "Unknown Distributor");
            }
            return map;
        }).collect(Collectors.toList());

        List<Map<String, Object>> newArrivalsWithImages = newArrivals.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getProductId());
            map.put("name", product.getName());
            map.put("price", product.getPrice());
            map.put("rating", product.getRating());
            map.put("image", product.getImage() != null ? product.getImage() : "/img/default.png");
            if (product.getDistributor() != null) {
                map.put("distributorId", product.getDistributor().getDistributorId());
                map.put("distributorName", product.getDistributor().getCompanyName());
            } else {
                map.put("distributorId", null);
                map.put("distributorName", "Unknown Distributor");
            }
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("categories", categories);
        model.addAttribute("trendingProducts", trendingProductsWithImages);
        model.addAttribute("newArrivals", newArrivalsWithImages);
        model.addAttribute("user", user);

        return "customer-dashboard";
    }

    /** 
     * ✅ Customer Order History 
     */

    
    @GetMapping("/customerorderhistory")
    public String customerOrderHistory(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        // Fetch orders using the correct user_id
        List<CusOrder> orderHistory = customerOrderService.getCustomerOrders(user.getUserId());
        model.addAttribute("orders", orderHistory);

        // Fetch order items and calculate total cost per order
        Map<Long, List<CusOrderItem>> orderItemsMap = new HashMap<>();
        Map<Long, BigDecimal> orderTotalMap = new HashMap<>();

        for (CusOrder order : orderHistory) {
            List<CusOrderItem> items = customerOrderService.getOrderItems(order.getOrderId());
            orderItemsMap.put(order.getOrderId(), items);
            orderTotalMap.put(order.getOrderId(), customerOrderService.calculateTotalForOrder(order.getOrderId()));
        }

        model.addAttribute("orderItems", orderItemsMap);
        model.addAttribute("orderTotals", orderTotalMap);

        return "customerorderhistory"; // This matches the Thymeleaf template name
    }


    /** 
     * ✅ Customer Transaction History 
     */
    @GetMapping("/customertransaction")
    public String customerTransaction(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        // Retrieve customer transactions
        Optional<Customer> customer = customerService.findByUserId(user.getUserId());
        List<CusDistriTransaction> transactions = transactionService.getTransactionsByCustomerId(customer.get().getCustomerId());
        
        for (CusDistriTransaction transaction : transactions) {
            // Add the total amount to the transaction object (via a method or a getter in the template)
            transaction.setTotalAmount(transaction.getTotalAmount());
        }
        
        model.addAttribute("transactions", transactions); // Pass transactions to Thymeleaf
        model.addAttribute("user", user); // Pass user data
        model.addAttribute("customer", customer);
        return "customertransaction"; // Thymeleaf template name
    }
    
    @GetMapping("/customerprofile")
    public String customerProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        // Fetch customer profile details from the database (you can add extra service calls if needed)
        Optional<Customer> customer = customerService.findByUserId(user.getUserId());

        // Add user and customer profile information to the model
        model.addAttribute("user", user);
        model.addAttribute("customer", customer.orElse(null));

        return "customerprofile"; // This will resolve to customer-profile.html (Thymeleaf template)
    }

    
    
    @GetMapping("/customerseedistributor")
    public String showDistributor(@RequestParam("distributorId") Long distributorId, Model model, HttpSession session) {
        // Fetch distributor details based on distributorId from the database
        Optional<Distributor> distributor = distributorService.getDistributorById(distributorId);
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }
        model.addAttribute("user", user);
        
        // Add the distributor object to the model to pass it to the view
        model.addAttribute("distributor", distributor);
        
        // Return the name of the Thymeleaf template (without .html extension)
        return "customerseedistributor";
    }

    @GetMapping("/cusChat")
    public String distributorChat(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        Optional<Customer> customerOptional = customerRepository.findByUser(user);
        customerOptional.ifPresent(customer -> model.addAttribute("customer", customer));

        model.addAttribute("user", user);
        return "cusChat";
    } 
    
    /* @GetMapping("/rating")
    public String cusRating(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        Optional<Customer> customerOptional = customerRepository.findByUser(user);
        customerOptional.ifPresent(customer -> model.addAttribute("customer", customer));

        model.addAttribute("user", user);
        return "rating";
    } */
    
 // ✅ Update Distributor Profile via API (For AJAX Requests)
 	@PutMapping("/cus/api/profile")
 	@ResponseBody
 	public ResponseEntity<?> updateCustomerProfileAPI(@RequestBody Customer updatedCustomer,
 			HttpSession session) {
 		User user = (User) session.getAttribute("loggedInUser");
 		if (user == null) {
 			return ResponseEntity.badRequest().body("User not logged in");
 		}
 		Long userId = user.getUserId();
 		Optional<Customer> cusOptional = customerRepository.findByUser_UserId(userId);
 		if (cusOptional.isEmpty()) {
 			return ResponseEntity.notFound().build();
 		}
 		Customer cus = cusOptional.get();
 		cus.setName(updatedCustomer.getName());
 		cus.setAddress(updatedCustomer.getAddress());
 		cus.setContactInfo(updatedCustomer.getContactInfo());
 		cus.setBio(updatedCustomer.getBio());
 		customerRepository.save(cus);
 		return ResponseEntity.ok(cus);
 	}

 	// ✅ Upload Profile Image API (For AJAX Requests)
 	@PostMapping("/cus/api/profile/upload/profile")
 	@ResponseBody
 	public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpSession session) {
 		User user = (User) session.getAttribute("loggedInUser");
 		if (user == null) {
 			return ResponseEntity.badRequest().body("User not logged in");
 		}
 		if (file.isEmpty()) {
 			return ResponseEntity.badRequest().body("No file uploaded");
 		}
 		String fileName = customerService.uploadProfileImage(user.getUserId(), file);
 		if (fileName != null) {
 			return ResponseEntity.ok(fileName);
 		} else {
 			return ResponseEntity.status(500).body("Image upload failed");
 		}
 	}

 	@PostMapping("/cus/profile/update")
 	@ResponseBody
 	@Transactional
 	public ResponseEntity<?> updateCustomerProfile(@RequestParam("username") String username,
 			@RequestParam("address") String address,
 			@RequestParam("contactInfo") String contactInfo, @RequestParam(value = "bio", required = false) String bio,
 			HttpSession session) {

 		User user = (User) session.getAttribute("loggedInUser");
 		if (user == null) {
 			System.out.println("Error: User not logged in.");
 			return ResponseEntity.badRequest().body("User not logged in");
 		}

 		Long userId = user.getUserId();
 		Optional<Customer> cusOptional = customerRepository.findByUser_UserId(userId);
 		if (cusOptional.isEmpty()) {
 			return ResponseEntity.notFound().build();
 		}

 		// Fetch the managed entities
 		User managedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
 		Customer managedCustomer = cusOptional.get();

 		// Update fields
 		managedUser.setUsername(username);
 		
 		managedCustomer.setAddress(address);
 		managedCustomer.setContactInfo(contactInfo);
 		managedCustomer.setBio(bio != null ? bio : "");

 		// Log after update

 		try {
 			// Save to database
 			User savedUser = userRepository.save(managedUser);
 			Customer savedCustomer = customerRepository.save(managedCustomer);

 			System.out.println("Data saved successfully.");

 			// Return updated data
 			return ResponseEntity
 					.ok(Map.of("username", savedUser.getUsername(), "contactInfo", savedCustomer.getContactInfo(),
 							"name", savedCustomer.getName(), "address", savedCustomer.getAddress(),
 							"bio", savedCustomer.getBio() != null ? savedCustomer.getBio() : ""));
 		} catch (Exception e) {
 			System.err.println("Error saving user or Distributor: " + e.getMessage());
 			e.printStackTrace();
 			return ResponseEntity.status(500).body("Error saving data");
 		}
 	}

 	@GetMapping("/cusprofile")
 	public String CustomerProfile(Model model, HttpSession session) {
 		User user = (User) session.getAttribute("loggedInUser");

 		if (user == null) {
 			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
 		}
 		Long userId = user.getUserId();
 		Optional<Customer> cusOptional = customerRepository.findByUser_UserId(userId);
 		if (cusOptional.isEmpty()) {
 			System.out.println("Customer not found for user: " + user.getEmail());
 			return "redirect:/customer-dashboard"; // ✅ Fixed Redirect
 		}

 		model.addAttribute("user", user);
 		model.addAttribute("customer", cusOptional.get());
 		return "cusprofile"; // ✅ Ensure this matches your `templates/Distributorprofile.html`
 	}
}

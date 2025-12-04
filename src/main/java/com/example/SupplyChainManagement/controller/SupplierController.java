package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.dto.CusOrderDTO;
import com.example.SupplyChainManagement.dto.ManuOrderDTO;
import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.ManuOrder;
import com.example.SupplyChainManagement.model.OrderSummary;
import com.example.SupplyChainManagement.model.ProductSales;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.repository.UserRepository;
import com.example.SupplyChainManagement.service.ManuOrderService;
import com.example.SupplyChainManagement.service.ProductService;
import com.example.SupplyChainManagement.service.SupplierDashboardService;
import com.example.SupplyChainManagement.service.SupplierService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class SupplierController {

    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ManuOrderService manuOrderService;
    @Autowired
    private SupplierDashboardService supplierDashboardService;

    public SupplierController(SupplierRepository supplierRepository, ProductService productService, 
    		CategoryRepository categoryRepository, UserRepository userRepository) {
    	
        this.supplierRepository = supplierRepository;
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/supplier-dashboard")
    public String supplierDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        Optional<Supplier> supplierOptional = supplierRepository.findByUser(user);
        Supplier supplier = supplierOptional.get();
        // supplierOptional.ifPresent(supplier -> model.addAttribute("supplier", supplier));

        model.addAttribute("user", user);
        model.addAttribute("supplier", supplier);
        
        Map<String, Object> stats = supplierDashboardService.getSupplierStats(supplier.getSupplierId());
        List<ProductSales> productSales = supplierDashboardService.getProductSalesBySupplierId(supplier.getSupplierId());
        List<OrderSummary> orders = supplierDashboardService.getOrdersBySupplierId(supplier.getSupplierId());

        List<Integer> productSalesData = supplierDashboardService.getMonthlyProductSalesData(supplier.getSupplierId());
        List<String> productSalesLabels = supplierDashboardService.getMonthlyProductSalesLabels(supplier.getSupplierId());
        Map<String, Integer> orderCompletionRate = supplierDashboardService.getOrderCompletionRate(supplier.getSupplierId());

        model.addAttribute("stats", stats);
        model.addAttribute("productSales", productSales);
        model.addAttribute("orders", orders);
        model.addAttribute("productSalesData", productSalesData);
        model.addAttribute("productSalesLabels", productSalesLabels);
        model.addAttribute("orderCompletionRate", orderCompletionRate);
        
        System.out.println("Product Sales Data: " + productSalesData);
        System.out.println("Product Sales Labels: " + productSalesLabels);
        System.out.println("Order Completion Rate: " + orderCompletionRate);

        return "supplier-dashboard";
    } 

    @GetMapping("/suppliermanageproduct")
    public String testForm(Model model, HttpSession session) {
        // Get the logged-in user from the session
        User user = (User) session.getAttribute("loggedInUser");

        // Check if the user is logged in
        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        // Fetch the supplier associated with the logged-in user
        Optional<Supplier> supplierOpt = supplierRepository.findByUser(user);
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();
            model.addAttribute("supplier", supplier);

            // Fetch existing products for the supplier
            List<DistriProduct> products = productService.findBysupplierId(supplier.getSupplierId());
            model.addAttribute("products", products);
        }

        // Fetch all categories from the database and add to the model
        List<Category> categories = categoryRepository.findAll();  // Fetch categories
        model.addAttribute("categories", categories);  // Add categories to the model

        // Create a new product object to bind to the form
        DistriProduct newProduct = new DistriProduct(); // Initialize a new product
        model.addAttribute("newProduct", newProduct); // Add the new product to the model

        return "suppliermanageproduct"; // Return the view name
    }
    
    /* @GetMapping("/supplierprofile")
    public String showSupplierProfile(@RequestParam("supplierId") Long supplierId, Model model, HttpSession session) {
        // Fetch distributor details based on distributorId from the database
        Optional<Supplier> supplier = supplierService.getSupplierById(supplierId);
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }
        model.addAttribute("user", user);
        
        // Add the distributor object to the model to pass it to the view
        model.addAttribute("supplier", supplier);
        
        // Return the name of the Thymeleaf template (without .html extension)
        return "supplierprofile";
    } */
    
    @GetMapping("/supplierChat")
    public String supplierChat(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        Optional<Supplier> supplierOptional = supplierRepository.findByUser(user);
        supplierOptional.ifPresent(supplier -> model.addAttribute("supplier", supplier));

        model.addAttribute("user", user);
        return "supplierChat";
    } 
    
 // ✅ Update Supplier Profile via API (For AJAX Requests)
    @PutMapping("/supplier/api/profile")
    @ResponseBody
    public ResponseEntity<?> updateSupplierProfileAPI(@RequestBody Supplier updatedSupplier, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }
        Long userId = user.getUserId();
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Supplier supplier = supplierOptional.get();
        supplier.setCompanyName(updatedSupplier.getCompanyName());
        supplier.setAddress(updatedSupplier.getAddress());
        supplier.setContactInfo(updatedSupplier.getContactInfo());
        supplier.setBio(updatedSupplier.getBio());
        supplierRepository.save(supplier);
        return ResponseEntity.ok(supplier);
    }

    // ✅ Upload Profile Image API (For AJAX Requests)
    @PostMapping("/supplier/api/profile/upload/profile")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }
        try {
            String fileName = supplierService.uploadProfileImage(user.getUserId(), file);
            if (fileName != null) {
                return ResponseEntity.ok(fileName);
            } else {
                return ResponseEntity.status(500).body("Image upload failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading profile image: " + e.getMessage());
        }
    }

    
    @PostMapping("/supplier/profile/update")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateSupplierProfile(
            @RequestParam("username") String username,
            @RequestParam("companyName") String companyName,
            @RequestParam("address") String address,
            @RequestParam("contactInfo") String contactInfo,
            @RequestParam(value = "bio", required = false) String bio,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            System.out.println("Error: User not logged in.");
            return ResponseEntity.badRequest().body("User not logged in");
        }

        Long userId = user.getUserId();
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Fetch the managed entities
        User managedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Supplier managedSupplier = supplierOptional.get();

        

        // Update fields
        managedUser.setUsername(username);
        managedSupplier.setCompanyName(companyName);
        managedSupplier.setAddress(address);
        managedSupplier.setContactInfo(contactInfo);
        managedSupplier.setBio(bio != null ? bio : "");

        // Log after update
       
        try {
            // Save to database
            User savedUser = userRepository.save(managedUser);
            Supplier savedSupplier = supplierRepository.save(managedSupplier);

            System.out.println("Data saved successfully.");

            // Return updated data
            return ResponseEntity.ok(Map.of(
                "username", savedUser.getUsername(),
                "contactInfo", savedSupplier.getContactInfo(),
                "companyName", savedSupplier.getCompanyName(),
                "address", savedSupplier.getAddress(),
                "bio", savedSupplier.getBio() != null ? savedSupplier.getBio() : ""
            ));
        } catch (Exception e) {
            System.err.println("Error saving user or supplier: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error saving data");
        }
    }
    
    @GetMapping("/supplierprofile")
    public String supplierProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }
        Long userId = user.getUserId();
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isEmpty()) {
            System.out.println("Supplier not found for user: " + user.getEmail());
            return "redirect:/supplier-dashboard";  // ✅ Fixed Redirect
        }

        model.addAttribute("user", user);
        model.addAttribute("supplier", supplierOptional.get());
        return "supplierprofile";  // ✅ Ensure this matches your `templates/supplierprofile.html`
    } 

    @GetMapping("/supplierordernoti")
    public String getOrdersFromManufacturers(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/userRegistration?form=login";
        }

        Optional<Supplier> supplierOpt = supplierRepository.findByUser(user);
        if (supplierOpt.isEmpty()) {
            System.out.println("Supplier not found for user: " + user.getEmail());
            return "redirect:/supplier-dashboard";  // ✅ Fixed Redirect
        }

        Supplier supplier = supplierOpt.get();
        List<ManuOrder> orders = manuOrderService.getOrdersForSupplier(supplier.getUser().getUserId());

        // Prepare data for Thymeleaf template
        List<ManuOrderDTO> orderDTOs = orders.stream().map(order -> {
        	ManuOrderDTO dto = new ManuOrderDTO();
            dto.setId(order.getOrderId());
            dto.setManuName(order.getManufacturer().getUser().getUsername());
            dto.setOrderDate(order.getOrderDate());
            dto.setDeliverDate(order.getDeliverDate());
            dto.setStatus(order.getStatus());
            dto.setTransactionStatus(order.getTransactionStatus());
            dto.setDeliverStatus(order.getDeliverStatus());
            dto.setItemNames(order.getOrderItems().stream()
                .map(item -> item.getSupplierRawMaterial().getName())
                .collect(Collectors.joining(", ")));
            dto.setManuId(order.getManufacturer().getManufacturerId());
            dto.setManuUserId(order.getManufacturer().getUser().getUserId());
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("orders", orderDTOs);
        model.addAttribute("supplier", supplier);
        // Add placeholder for messages and notifications if needed
        model.addAttribute("messages", List.of()); // Replace with actual data if available
        model.addAttribute("notifications", List.of()); // Replace with actual data if available

        return "supplierordernoti"; // Matches the Thymeleaf template name
    }
}

package com.example.SupplyChainManagement.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

import com.example.SupplyChainManagement.dto.DistriOrderDTO;
import com.example.SupplyChainManagement.dto.ManuOrderDTO;
import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.DistriOrder;
import com.example.SupplyChainManagement.model.DistriOrderItem;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ItemType;
import com.example.SupplyChainManagement.model.ManuInventoryItem;
import com.example.SupplyChainManagement.model.ManuOrder;
import com.example.SupplyChainManagement.model.ManuOrderItem;
import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.ManufacturerRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.repository.UserRepository;
import com.example.SupplyChainManagement.service.CategoryService;
import com.example.SupplyChainManagement.service.DistriOrderService;
import com.example.SupplyChainManagement.service.ItemTypeService;
import com.example.SupplyChainManagement.service.ManuInventoryService;
import com.example.SupplyChainManagement.service.ManuOrderService;
import com.example.SupplyChainManagement.service.ManuProductService;
import com.example.SupplyChainManagement.service.ManufacturerService;
import com.example.SupplyChainManagement.service.ProductService;
import com.example.SupplyChainManagement.service.SupplierService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class ManufacturerController {
	
	private final ManufacturerRepository manufacturerRepository;
	private final UserRepository userRepository;
	private final SupplierRepository supplierRepository;
	private final CategoryRepository categoryRepository;
	
	@Autowired
    private ManufacturerService manuService;
	
	@Autowired
    private ManuProductService manuProductService;
	
	@Autowired
    private SupplierService supplierService;
	
	@Autowired
    private DistriOrderService distriOrderService;
	
	@Autowired
    private ManuOrderService manuOrderService;
	
	@Autowired
    private ManuInventoryService inventoryService;
	
	@Autowired
    private ItemTypeService itemTypeService;
	
	@Autowired
    private CategoryService categoryService;
	
	public ManufacturerController(ManufacturerRepository manufacturerRepository, UserRepository userRepository, 
			SupplierRepository supplierRepository, CategoryRepository categoryRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.userRepository = userRepository;
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
    }

	@GetMapping("/manufacturer-dashboard")
    public String manufacturerDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        Optional<Manufacturer> manufacturerOptional = manufacturerRepository.findByUser(user);
        manufacturerOptional.ifPresent(manufacturer -> model.addAttribute("manufacturer", manufacturer));

        model.addAttribute("user", user);
        return "manufacturer-dashboard";
    }
	
	@GetMapping("/manuChat")
    public String manufacturerChat(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }

        Optional<Manufacturer> manufacturerOptional = manufacturerRepository.findByUser(user);
        manufacturerOptional.ifPresent(manufacturer -> model.addAttribute("manufacturer", manufacturer));

        model.addAttribute("user", user);
        return "manuChat";
    } 
	
	@GetMapping("/suppliermarketplace")
	public String supplierMarketplace(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		Optional<Supplier> supplierOptional = supplierRepository.findByUser(user);
		Optional<Manufacturer> manuOpt = manufacturerRepository.findByUser(user);
		Manufacturer manu = manuOpt.get();
		supplierOptional.ifPresent(supplier -> model.addAttribute("supplier", supplier));

		model.addAttribute("user", user);
		model.addAttribute("manufacturer", manu);
		return "suppliermarketplace";
	}
	
	
	@PutMapping("/manu/api/profile")
    @ResponseBody
    public ResponseEntity<?> updateManuProfileAPI(@RequestBody Manufacturer updatedManufacturer, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }
        Long userId = user.getUserId();
        Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser_UserId(userId);
        if (manuOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Manufacturer manufacturer = manuOptional.get();
        manufacturer.setCompanyName(updatedManufacturer.getCompanyName());
        manufacturer.setAddress(updatedManufacturer.getAddress());
        manufacturer.setContactInfo(updatedManufacturer.getContactInfo());
        manufacturer.setBio(updatedManufacturer.getBio());
        manufacturerRepository.save(manufacturer);
        return ResponseEntity.ok(manufacturer);
    }

    // ✅ Upload Profile Image API (For AJAX Requests)
    @PostMapping("/manu/api/profile/upload/profile")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }
        String fileName = manuService.uploadProfileImage(user.getUserId(), file);
        if (fileName != null) {
            return ResponseEntity.ok(fileName);
        } else {
            return ResponseEntity.status(500).body("Image upload failed");
        }
    }
    
    @PostMapping("/manu/profile/update")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateManuProfile(
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
        Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser_UserId(userId);
        if (manuOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Fetch the managed entities
        User managedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Manufacturer managedManufacturer = manuOptional.get();

        

        // Update fields
        managedUser.setUsername(username);
        managedManufacturer.setCompanyName(companyName);
        managedManufacturer.setAddress(address);
        managedManufacturer.setContactInfo(contactInfo);
        managedManufacturer.setBio(bio != null ? bio : "");

        // Log after update
       
        try {
            // Save to database
            User savedUser = userRepository.save(managedUser);
            Manufacturer savedManufacturer = manufacturerRepository.save(managedManufacturer);

            System.out.println("Data saved successfully.");

            // Return updated data
            return ResponseEntity.ok(Map.of(
                "username", savedUser.getUsername(),
                "contactInfo", savedManufacturer.getContactInfo(),
                "companyName", savedManufacturer.getCompanyName(),
                "address", savedManufacturer.getAddress(),
                "bio", savedManufacturer.getBio() != null ? savedManufacturer.getBio() : ""
            ));
        } catch (Exception e) {
            System.err.println("Error saving user or supplier: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error saving data");
        }
    }
    
    @GetMapping("/manuprofile")
    public String supplierProfile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/userRegistration?form=login"; // Redirect if not logged in
        }
        Long userId = user.getUserId();
        Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser_UserId(userId);
        if (manuOptional.isEmpty()) {
            System.out.println("Manufacturer not found for user: " + user.getEmail());
            return "redirect:/manufacturer-dashboard";  // ✅ Fixed Redirect
        }

        model.addAttribute("user", user);
        model.addAttribute("manufacturer", manuOptional.get());
        return "manuprofile";  // ✅ Ensure this matches your `templates/supplierprofile.html`
    }
    
    @GetMapping("/manuordernoti")
    public String getOrdersFromDistributors(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/userRegistration?form=login";
        }

        Optional<Manufacturer> manuOpt = manufacturerRepository.findByUser(user);
        if (manuOpt.isEmpty()) {
            System.out.println("Manufacturer not found for user: " + user.getEmail());
            return "redirect:/manufacturer-dashboard";  // ✅ Fixed Redirect
        }

        Manufacturer manu = manuOpt.get();
        List<DistriOrder> orders = distriOrderService.getOrdersForManufacturer(manu.getUser().getUserId());

        // Prepare data for Thymeleaf template
        List<DistriOrderDTO> orderDTOs = orders.stream().map(order -> {
        	DistriOrderDTO dto = new DistriOrderDTO();
            dto.setId(order.getOrderId());
            dto.setDistriName(order.getDistributor().getUser().getUsername());
            dto.setOrderDate(order.getOrderDate());
            dto.setDeliverDate(order.getDeliverDate());
            dto.setStatus(order.getStatus());
            dto.setTransactionStatus(order.getTransactionStatus());
            dto.setDeliverStatus(order.getDeliverStatus());
            dto.setItemNames(order.getOrderItems().stream()
                .map(item -> item.getManuProduct().getName())
                .collect(Collectors.joining(", ")));
            dto.setDistriId(order.getDistributor().getDistributorId());
            dto.setDistriUserId(order.getDistributor().getUser().getUserId());
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("orders", orderDTOs);
        model.addAttribute("manufacturer", manu);
        // Add placeholder for messages and notifications if needed
        model.addAttribute("messages", List.of()); // Replace with actual data if available
        model.addAttribute("notifications", List.of()); // Replace with actual data if available

        return "manuordernoti"; // Matches the Thymeleaf template name
    }
    
    @GetMapping("/manuseesupplier")
	public String showSupplier(@RequestParam("supplierId") Long supplierId, Model model,
			HttpSession session) {
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
		return "manuseesupplier";
	}
    
    @GetMapping("/manufacturerInventory")
    public String showInventoryPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
        	return "redirect:/userRegistration?form=login";
        }

        Long userId = user.getUserId();
        Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser_UserId(userId);
        if (manuOptional.isEmpty()) {
            return "redirect:/manufacturer-dashboard";
        }

        Manufacturer manu = manuOptional.get();
        Long manufacturerId = manu.getManufacturerId();

        List<ManuInventoryItem> items = inventoryService.getItemsByManufacturerId(manufacturerId);
        List<ItemType> itemTypes = itemTypeService.getAllItemTypes();
        List<Category> categories = categoryRepository.findAll(); // ✅ Fetch categories

        model.addAttribute("user", user);
        model.addAttribute("manufacturer", manu);
        model.addAttribute("inventoryItems", items);
        model.addAttribute("itemTypes", itemTypes);
        model.addAttribute("categories", categories);  // ✅ Add categories to model
        model.addAttribute("manufacturerId", manufacturerId);
        model.addAttribute("inventoryItem", new ManuInventoryItem());

        return "manufacturerInventory";
    }
    
    /* @GetMapping("/manufacturer-inventory")
    public String showManufacturerInventoryPage(HttpSession session, Model model) {
        // Fetch the logged-in user from the session
        User user = (User) session.getAttribute("loggedInUser");

        // Redirect to login page if the user is not logged in
        if (user == null) {
            return "redirect:/userRegistration?form=login";
        }

        // Fetch the manufacturer associated with the logged-in user
        Optional<Manufacturer> manufacturer = manuService.getManufacturerByUserId(user.getUserId());

        if (manufacturer.isEmpty()) {
            // Handle case where the user is not associated with a manufacturer
            return "redirect:/error?message=Manufacturer not found";
        }
        
        List<Category> categories = categoryService.getAllCategories();

        // Add the manufacturerId to the model so it can be accessed in the Thymeleaf template
        model.addAttribute("manufacturerId", manufacturer.get().getManufacturerId());
        model.addAttribute("user", user);
        model.addAttribute("categories", categories);

        // Return the Thymeleaf template name (without .html)
        return "manufacturerInventory";
    } */
    
    @GetMapping("/manu-product-management")
    public String showManuInventory(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/userRegistration?form=login";
        }
        Long userId = user.getUserId();
        Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser(user);
        if (manuOptional.isEmpty()) {
            return "redirect:/manufacturer-dashboard";
        }
        Manufacturer manu = manuOptional.get();

        List<ManuProduct> manuProducts = manuProductService.getProductsByManufacturerId(manuOptional.get().getManufacturerId());
        List<Category> categories = categoryService.getAllCategories();
        List<ManuInventoryItem> inventoryItems = inventoryService.getManufacturerInventory();

        Set<String> existingProductNames = manuProductService.getExistingProductsNamesByManufacturerId(manuOptional.get().getManufacturerId());
        List<ManuInventoryItem> availableInventoryItems = inventoryItems.stream()
                .filter(item -> !existingProductNames.contains(item.getName()))
                .collect(Collectors.toList());

        model.addAttribute("manuProducts", manuProducts);
        model.addAttribute("categories", categories);
        model.addAttribute("inventoryItems", availableInventoryItems);
        model.addAttribute("manuProduct", new ManuProduct());
        model.addAttribute("isEditMode", false);
        model.addAttribute("loggedInManufacturer", manuOptional);
        model.addAttribute("manufacturer", manu);
        return "manuAddProduct";
    }
    
    @GetMapping("/manufacturerorderhistory")
	public String manuOrderHistory(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}
		Optional<Manufacturer> manuOptional = manufacturerRepository.findByUser(user);
        if (manuOptional.isEmpty()) {
            return "redirect:/manufacturer-dashboard";
        }
        Manufacturer manu = manuOptional.get();

		// Fetch orders using the correct user_id
		List<ManuOrder> orderHistory = manuOrderService.getManufacturerOrders(user.getUserId());
		model.addAttribute("orders", orderHistory);

		// Fetch order items and calculate total cost per order
		Map<Long, List<ManuOrderItem>> orderItemsMap = new HashMap<>();
		Map<Long, BigDecimal> orderTotalMap = new HashMap<>();

		for (ManuOrder order : orderHistory) {
			List<ManuOrderItem> items = manuOrderService.getOrderItems(order.getOrderId());
			orderItemsMap.put(order.getOrderId(), items);
			orderTotalMap.put(order.getOrderId(), manuOrderService.calculateTotalForOrder(order.getOrderId()));
		}

		model.addAttribute("orderItems", orderItemsMap);
		model.addAttribute("orderTotals", orderTotalMap);
		model.addAttribute("manufacturer", manu);

		return "manufacturerorderhistory"; // This matches the Thymeleaf template name
	}
}

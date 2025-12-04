package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.dto.CusOrderDTO;
import com.example.SupplyChainManagement.dto.ManuOrderDTO;
import com.example.SupplyChainManagement.model.Category;
import com.example.SupplyChainManagement.model.CusOrder;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.DistriInventory;
import com.example.SupplyChainManagement.model.DistriOrder;
import com.example.SupplyChainManagement.model.DistriOrderItem;
import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.ManuOrder;
import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.repository.CategoryRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;
import com.example.SupplyChainManagement.repository.UserRepository;
import com.example.SupplyChainManagement.service.CustomerOrderService;
import com.example.SupplyChainManagement.service.DistriInventoryService;
import com.example.SupplyChainManagement.service.DistriOrderService;
import com.example.SupplyChainManagement.service.DistributorService;
import com.example.SupplyChainManagement.service.FileStorageService;
import com.example.SupplyChainManagement.service.ManufacturerService;
import com.example.SupplyChainManagement.service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class DistributorController {

	@Autowired
	private ProductService productService; // Inject your product service

	@Autowired
	private final FileStorageService fileStorageService;

	@Autowired
	private DistributorRepository distributorRepository; // Inject your distributor repository

	@Autowired
	private final CustomerOrderService customerOrderService;

	@Autowired
	private final ManufacturerService manufacturerService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private DistriOrderService distriOrderService;
	
	@Autowired
	private DistributorService distributorService;
	
	@Autowired
	private DistriInventoryService inventoryService; 
	
	@Autowired
	private final UserRepository userRepository;

	public DistributorController(ProductService productService, FileStorageService fileStorageService,
			DistributorRepository distributorRepository, CategoryRepository categoryRepository,
			CustomerOrderService customerOrderService, ManufacturerService manufacturerService,
			UserRepository userRepository) {
		this.productService = productService;
		this.fileStorageService = fileStorageService;
		this.distributorRepository = distributorRepository;
		this.categoryRepository = categoryRepository;
		this.customerOrderService = customerOrderService;
		this.manufacturerService = manufacturerService;
		this.userRepository = userRepository;
	}

	@GetMapping("/testform")
	public String testForm(Model model, HttpSession session) {
		// Get the logged-in user from the session
		User user = (User) session.getAttribute("loggedInUser");

		// Check if the user is logged in
		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		// Fetch the distributor associated with the logged-in user
		Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
		if (distributorOpt.isPresent()) {
			Distributor distributor = distributorOpt.get();
			model.addAttribute("distributor", distributor);

			// Fetch existing products for the distributor
			List<DistriProduct> products = productService.findByDistributorId(distributor.getDistributorId());
			model.addAttribute("products", products);
		}

		// Fetch all categories from the database and add to the model
		List<Category> categories = categoryRepository.findAll(); // Fetch categories
		model.addAttribute("categories", categories); // Add categories to the model

		// Create a new product object to bind to the form
		DistriProduct newProduct = new DistriProduct(); // Initialize a new product
		model.addAttribute("newProduct", newProduct); // Add the new product to the model

		return "testform"; // Return the view name
	}

	@GetMapping("/distributor-dashboard")
	public String distributorDashboard(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		Optional<Distributor> distributorOptional = distributorRepository.findByUser(user);
		distributorOptional.ifPresent(distributor -> model.addAttribute("distributor", distributor));

		model.addAttribute("user", user);
		return "distributor-dashboard";
	}

	@GetMapping("/distributoranalytics")
	public String distributorAnalytics(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		Optional<Distributor> distributorOptional = distributorRepository.findByUser(user);
		distributorOptional.ifPresent(distributor -> model.addAttribute("distributor", distributor));

		model.addAttribute("user", user);
		return "distributoranalytics";
	}
	
	/* @GetMapping("/distributorinventory")
	public String distributorInventory(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		Optional<Distributor> distributorOptional = distributorRepository.findByUser(user);
		distributorOptional.ifPresent(distributor -> model.addAttribute("distributor", distributor));

		model.addAttribute("user", user);
		return "distributorinventory";
	} */

	@GetMapping("/manufacturermarketplace")
	public String distributorMarketplace(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		Optional<Distributor> distributorOptional = distributorRepository.findByUser(user);
		distributorOptional.ifPresent(distributor -> model.addAttribute("distributor", distributor));

		model.addAttribute("user", user);
		return "manufacturermarketplace";
	}

	@GetMapping("/distriChat")
	public String distributorChat(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		Optional<Distributor> distributorOptional = distributorRepository.findByUser(user);
		distributorOptional.ifPresent(distributor -> model.addAttribute("distributor", distributor));

		model.addAttribute("user", user);
		return "distriChat";
	}
	
	@GetMapping("/distributorinventory")
	public String distributorInventory(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		Optional<Distributor> distributorOptional = distributorRepository.findByUser(user);
		distributorOptional.ifPresent(distributor -> model.addAttribute("distributor", distributor));
		Long distributorId = distributorOptional.get().getDistributorId();
		List<DistriInventory> inventoryItems = inventoryService.getInventoryItems(distributorId);
		List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("inventoryItems", inventoryItems);

		model.addAttribute("user", user);
		return "distributorinventory";
	}

	/*
	 * @GetMapping("/testform") public String showTestForm(Model model, HttpSession
	 * session) { User user = (User ) session.getAttribute("user");
	 * List<Distributor> distributors = distributorRepository.findAll();
	 * model.addAttribute("distributors", distributors);
	 * model.addAttribute("newProduct", new DistriProduct());
	 * model.addAttribute("products", productService.getAllProducts()); return
	 * "testform"; }
	 */

	@GetMapping("/notifications")
	public String getCustomerOrderNotifications(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");
		if (user == null) {
			return "redirect:/userRegistration?form=login";
		}

		Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
		if (distributorOpt.isEmpty()) {
			return "redirect:/dashboard"; // Redirect if no distributor found
		}

		Distributor distributor = distributorOpt.get();
		List<CusOrder> orders = customerOrderService.getOrdersForDistributor(distributor.getUser().getUserId());

		// Prepare data for Thymeleaf template
		List<CusOrderDTO> orderDTOs = orders.stream().map(order -> {
			CusOrderDTO dto = new CusOrderDTO();
			dto.setId(order.getOrderId());
			dto.setCustomerName(order.getCustomer().getUser().getUsername());
			dto.setOrderDate(order.getOrderDate());
			dto.setItemNames(order.getOrderItems().stream().map(item -> item.getDistriProduct().getName())
					.collect(Collectors.joining(", ")));
			dto.setCustomerId(order.getCustomer().getCustomerId());
			dto.setCustomerUserId(order.getCustomer().getUser().getUserId());
			return dto;
		}).collect(Collectors.toList());

		model.addAttribute("user", user);
		model.addAttribute("orders", orderDTOs);
		model.addAttribute("distributor", distributor);
		// Add placeholder for messages and notifications if needed
		model.addAttribute("messages", List.of()); // Replace with actual data if available
		model.addAttribute("notifications", List.of()); // Replace with actual data if available

		return "notifications"; // Matches the Thymeleaf template name
	}

	@GetMapping("/distributororderfromcustomer")
	public String getOrdersFromCustomers(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");
		if (user == null) {
			return "redirect:/userRegistration?form=login";
		}

		Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
		if (distributorOpt.isEmpty()) {
			return "redirect:/dashboard"; // Redirect if no distributor found
		}

		Distributor distributor = distributorOpt.get();
		List<CusOrder> orders = customerOrderService.getOrdersForDistributor(distributor.getUser().getUserId());

		// Prepare data for Thymeleaf template
		List<CusOrderDTO> orderDTOs = orders.stream().map(order -> {
			CusOrderDTO dto = new CusOrderDTO();
			dto.setId(order.getOrderId());
			dto.setCustomerName(order.getCustomer().getUser().getUsername());
			dto.setOrderDate(order.getOrderDate());
			dto.setDeliverDate(order.getDeliverDate());
			dto.setStatus(order.getStatus());
			dto.setTransactionStatus(order.getTransactionStatus());
			dto.setDeliverStatus(order.getDeliverStatus());
			dto.setItemNames(order.getOrderItems().stream().map(item -> item.getDistriProduct().getName())
					.collect(Collectors.joining(", ")));
			dto.setCustomerId(order.getCustomer().getCustomerId());
			dto.setCustomerUserId(order.getCustomer().getUser().getUserId());
			return dto;
		}).collect(Collectors.toList());

		model.addAttribute("user", user);
		model.addAttribute("orders", orderDTOs);
		model.addAttribute("distributor", distributor);
		// Add placeholder for messages and notifications if needed
		model.addAttribute("messages", List.of()); // Replace with actual data if available
		model.addAttribute("notifications", List.of()); // Replace with actual data if available

		return "distributororderfromcustomer"; // Matches the Thymeleaf template name
	}

	@GetMapping("/distributorseemanufacturer")
	public String showManufacturer(@RequestParam("manufacturerId") Long manufacturerId, Model model,
			HttpSession session) {
		// Fetch distributor details based on distributorId from the database
		Optional<Manufacturer> manufacturer = manufacturerService.getManufacturerById(manufacturerId);
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}
		model.addAttribute("user", user);

		// Add the distributor object to the model to pass it to the view
		model.addAttribute("manufacturer", manufacturer);

		// Return the name of the Thymeleaf template (without .html extension)
		return "distributorseemanufacturer";
	}

	/*
	 * @GetMapping("/distributor/order-history") public String
	 * getOrderHistoryPage(Model model, HttpSession session) { User user = (User)
	 * session.getAttribute("loggedInUser"); if (user == null) { return
	 * "redirect:/userRegistration?form=login"; }
	 * 
	 * Optional<Distributor> distributorOpt =
	 * distributorRepository.findByUser(user); List<DistriOrder> orders =
	 * distriOrderService.getOrderHistory(distributorOpt.get().getUser().getUserId()
	 * ); model.addAttribute("orders", orders); return "distributororderhistory"; }
	 */

	/*
	 * @GetMapping("/distributororderhistory") public String
	 * distributorOrderHistory(Model model, HttpSession session) { User user =
	 * (User) session.getAttribute("loggedInUser");
	 * 
	 * if (user == null) { return "redirect:/userRegistration?form=login"; }
	 * 
	 * List<DistriOrder> orderHistory =
	 * distriOrderService.getOrderHistory(user.getUserId());
	 * model.addAttribute("orders", orderHistory);
	 * 
	 * Map<Long, List<DistriOrderItem>> orderItemsMap = new HashMap<>(); Map<Long,
	 * BigDecimal> orderTotalMap = new HashMap<>();
	 * 
	 * for (DistriOrder order : orderHistory) { List<DistriOrderItem> items =
	 * distriOrderService.getOrderItems(order.getOrderId()); // Ensure items is
	 * never null orderItemsMap.put(order.getOrderId(), items != null ? items :
	 * Collections.emptyList()); orderTotalMap.put(order.getOrderId(),
	 * calculateTotalForOrder(order.getOrderId())); }
	 * 
	 * model.addAttribute("orderItems", orderItemsMap);
	 * model.addAttribute("orderTotals", orderTotalMap);
	 * 
	 * return "distributororderhistory"; // Matches the Thymeleaf template name }
	 */

	@GetMapping("/distributororderhistory")
	public String customerOrderHistory(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}

		// Fetch orders using the correct user_id
		Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
		if (distributorOpt.isPresent()) {
			Distributor distributor = distributorOpt.get();
			model.addAttribute("distributor", distributor);
		}
		List<DistriOrder> orderHistory = distriOrderService.getDistributorOrders(user.getUserId());
		model.addAttribute("orders", orderHistory);

		// Fetch order items and calculate total cost per order
		Map<Long, List<DistriOrderItem>> orderItemsMap = new HashMap<>();
		Map<Long, BigDecimal> orderTotalMap = new HashMap<>();

		for (DistriOrder order : orderHistory) {
			List<DistriOrderItem> items = distriOrderService.getOrderItems(order.getOrderId());
			orderItemsMap.put(order.getOrderId(), items);
			orderTotalMap.put(order.getOrderId(), distriOrderService.calculateTotalForOrder(order.getOrderId()));
		}

		model.addAttribute("orderItems", orderItemsMap);
		model.addAttribute("orderTotals", orderTotalMap);

		return "distributororderhistory"; // This matches the Thymeleaf template name
	}

	// Helper method to calculate total for an order (move to service if complex)
	private BigDecimal calculateTotalForOrder(Long orderId) {
		List<DistriOrderItem> items = distriOrderService.getOrderItems(orderId);
		// Handle null items
		if (items == null || items.isEmpty()) {
			return BigDecimal.ZERO;
		}
		return items.stream()
				.map(item -> item.getManuProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	// API endpoint for JS fetching
	@GetMapping("/distributor/orders/history")
	@ResponseBody
	public ResponseEntity<List<DistriOrder>> getOrderHistoryApi(HttpSession session) {
		// Get the logged-in user from session
		User user = (User) session.getAttribute("loggedInUser");
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401 Unauthorized
		}

		// Fetch the Distributor entity for the logged-in user
		Optional<Distributor> distributorOpt = distributorRepository.findByUser(user);
		if (distributorOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 Forbidden if user isn’t a distributor
		}

		Long distributorUserId = distributorOpt.get().getUser().getUserId();
		List<DistriOrder> orders = distriOrderService.getOrderHistory(distributorUserId);
		return ResponseEntity.ok(orders);
	}

	// ✅ Update Distributor Profile via API (For AJAX Requests)
	@PutMapping("/distri/api/profile")
	@ResponseBody
	public ResponseEntity<?> updateDistributorProfileAPI(@RequestBody Distributor updatedDistributor,
			HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");
		if (user == null) {
			return ResponseEntity.badRequest().body("User not logged in");
		}
		Long userId = user.getUserId();
		Optional<Distributor> distributorOptional = distributorRepository.findByUser_UserId(userId);
		if (distributorOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Distributor distributor = distributorOptional.get();
		distributor.setCompanyName(updatedDistributor.getCompanyName());
		distributor.setAddress(updatedDistributor.getAddress());
		distributor.setContactInfo(updatedDistributor.getContactInfo());
		distributor.setBio(updatedDistributor.getBio());
		distributorRepository.save(distributor);
		return ResponseEntity.ok(distributor);
	}

	// ✅ Upload Profile Image API (For AJAX Requests)
	@PostMapping("/distri/api/profile/upload/profile")
	@ResponseBody
	public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");
		if (user == null) {
			return ResponseEntity.badRequest().body("User not logged in");
		}
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("No file uploaded");
		}
		String fileName = distributorService.uploadProfileImage(user.getUserId(), file);
		if (fileName != null) {
			return ResponseEntity.ok(fileName);
		} else {
			return ResponseEntity.status(500).body("Image upload failed");
		}
	}

	@PostMapping("/distri/profile/update")
	@ResponseBody
	@Transactional
	public ResponseEntity<?> updateDistributorProfile(@RequestParam("username") String username,
			@RequestParam("companyName") String companyName, @RequestParam("address") String address,
			@RequestParam("contactInfo") String contactInfo, @RequestParam(value = "bio", required = false) String bio,
			HttpSession session) {

		User user = (User) session.getAttribute("loggedInUser");
		if (user == null) {
			System.out.println("Error: User not logged in.");
			return ResponseEntity.badRequest().body("User not logged in");
		}

		Long userId = user.getUserId();
		Optional<Distributor> distributorOptional = distributorRepository.findByUser_UserId(userId);
		if (distributorOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		// Fetch the managed entities
		User managedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		Distributor managedDistributor = distributorOptional.get();

		// Update fields
		managedUser.setUsername(username);
		managedDistributor.setCompanyName(companyName);
		managedDistributor.setAddress(address);
		managedDistributor.setContactInfo(contactInfo);
		managedDistributor.setBio(bio != null ? bio : "");

		// Log after update

		try {
			// Save to database
			User savedUser = userRepository.save(managedUser);
			Distributor savedDistributor = distributorRepository.save(managedDistributor);

			System.out.println("Data saved successfully.");

			// Return updated data
			return ResponseEntity
					.ok(Map.of("username", savedUser.getUsername(), "contactInfo", savedDistributor.getContactInfo(),
							"companyName", savedDistributor.getCompanyName(), "address", savedDistributor.getAddress(),
							"bio", savedDistributor.getBio() != null ? savedDistributor.getBio() : ""));
		} catch (Exception e) {
			System.err.println("Error saving user or Distributor: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(500).body("Error saving data");
		}
	}

	@GetMapping("/distriprofile")
	public String DistributorProfile(Model model, HttpSession session) {
		User user = (User) session.getAttribute("loggedInUser");

		if (user == null) {
			return "redirect:/userRegistration?form=login"; // Redirect if not logged in
		}
		Long userId = user.getUserId();
		Optional<Distributor> DistributorOptional = distributorRepository.findByUser_UserId(userId);
		if (DistributorOptional.isEmpty()) {
			System.out.println("Distributor not found for user: " + user.getEmail());
			return "redirect:/distributor-dashboard"; // ✅ Fixed Redirect
		}

		model.addAttribute("user", user);
		model.addAttribute("distributor", DistributorOptional.get());
		return "distriprofile"; // ✅ Ensure this matches your `templates/Distributorprofile.html`
	}

}
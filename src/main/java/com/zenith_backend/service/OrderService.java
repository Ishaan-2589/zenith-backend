package com.zenith_backend.service;

import com.zenith_backend.model.*;
import com.zenith_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductService productService;
    private final EmailService emailService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public OrderService(OrderRepository orderRepo,
                        CartRepository cartRepo,
                        ProductRepository productRepo,
                        UserRepository userRepo,
                        OrderItemRepository orderItemRepo,
                        ProductService productService,
                        EmailService emailService) {

        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.orderItemRepo = orderItemRepo;
        this.productService = productService;
        this.emailService = emailService;
    }
    @Transactional
    public Order placeOrder(String email, Map<String, Object> orderData) {
        // 1. Fetch the user and their cart items
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Your cart is empty. Add some products before checking out!");
        }

        // 2. Initialize the Order object with payment tracking
        Order order = new Order();
        order.setUserId(user.getId());
        order.setStatus("Processing");
        order.setOrderDate(java.time.LocalDateTime.now());

        if (orderData != null) {
            order.setPaymentId(String.valueOf(orderData.get("paymentId")));
            order.setPaymentStatus(String.valueOf(orderData.get("paymentStatus")));

            if (orderData.containsKey("paymentMethod")) {
                order.setPaymentMethod(String.valueOf(orderData.get("paymentMethod")));
            }
            order.setAddress(String.valueOf(orderData.get("address")));
            order.setCity(String.valueOf(orderData.get("city")));
            order.setZip(String.valueOf(orderData.get("zip")));
        }

        // Save first to generate an ID for OrderItems
        Order savedOrder = orderRepo.save(order);

        double total = 0;
        StringBuilder itemRows = new StringBuilder(); // For the email receipt

        // 3. Process each item: Calculate total, update stock, and create OrderItems
        for (CartItem item : cartItems) {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // Decrease inventory stock
            productService.decreaseStock(product.getId(), item.getQuantity());

            double price = product.getPrice();
            double subtotal = price * item.getQuantity();
            total += subtotal;

            // Create OrderItem entry
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(price);
            orderItemRepo.save(orderItem);

            // Add a row to the HTML receipt table
            itemRows.append(String.format("""
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #eee;">%s</td>
                <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: center;">%d</td>
                <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: right;">₹%.2f</td>
            </tr>
            """, product.getName(), item.getQuantity(), subtotal));
        }

        // 4. Update the final order total and save
        savedOrder.setTotalAmount(total);
        orderRepo.save(savedOrder);

        // 5. Build and send the HTML Receipt
        String receiptHtml = """
        <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; color: #333;">
            <div style="text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 20px;">
                <h1 style="margin: 0; color: #000;">ZENITH</h1>
                <p style="font-size: 12px; color: #666;">Order Confirmation</p>
            </div>
            
            <p>Hi <strong>%s</strong>,</p>
            <p>Thank you for your order! We're getting it ready for shipment.</p>
            
            <div style="background: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px;">
                <p style="margin: 0;"><strong>Order ID:</strong> #%d</p>
                <p style="margin: 0;"><strong>Status:</strong> %s</p>
            </div>

            <table style="width: 100%%; border-collapse: collapse;">
                <thead>
                    <tr style="background: #333; color: #fff;">
                        <th style="padding: 10px; text-align: left;">Product</th>
                        <th style="padding: 10px; text-align: center;">Qty</th>
                        <th style="padding: 10px; text-align: right;">Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    %s
                </tbody>
            </table>

            <div style="text-align: right; margin-top: 20px;">
                <h2 style="margin: 0;">Total: ₹%.2f</h2>
            </div>

            <div style="margin-top: 30px; border-top: 1px solid #eee; padding-top: 10px; font-size: 12px; color: #999; text-align: center;">
                <p>Zenith Inc. | 123 Fashion Street, Tech City</p>
            </div>
        </div>
        """.formatted(user.getName(), savedOrder.getId(), savedOrder.getStatus(), itemRows.toString(), total);

        try {
            emailService.sendHtmlEmail(email, "Zenith Order Confirmation - #" + savedOrder.getId(), receiptHtml);
        } catch (Exception e) {
            // We don't throw an error here because the order is already saved in DB
            System.out.println("Order email failed to send: " + e.getMessage());
        }

        // 6. Clear the user's cart after successful order
        cartRepo.deleteAll(cartItems);

        return savedOrder;
    }

    public List<Map<String, Object>> getOrdersByUser(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepo.findByUserId(user.getId());

        List<Map<String, Object>> result = new ArrayList<>();

        for (Order order : orders) {

            List<OrderItem> items = orderItemRepo.findByOrderId(order.getId());
            List<Map<String, Object>> itemList = new ArrayList<>();

            for (OrderItem item : items) {
                Product product = productRepo.findById(item.getProductId()).orElse(null);

                Map<String, Object> itemMap = new HashMap<>();

                // If product exists, use its name. If it was deleted, label it as Archived.
                itemMap.put("name", product != null ? product.getName() : "Archived/Deleted Object");

                itemMap.put("price", item.getPrice());
                itemMap.put("quantity", item.getQuantity());

                itemMap.put("imageUrl", product != null ? product.getImageUrl() : null);

                itemList.add(itemMap);
            }

            Map<String, Object> orderMap = new HashMap<>();

            orderMap.put("id", order.getId());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("status", order.getStatus());
            orderMap.put("date", order.getOrderDate()); // Ensure date is sent!
            orderMap.put("items", itemList);

            result.add(orderMap);
        }

        return result;
    }

    public Order updateOrderStatus(int orderId, String status) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        Order savedOrder = orderRepo.save(order);

        // Fetch the user's details and trigger the background email timer
        userRepo.findById(order.getUserId()).ifPresent(user -> {
            scheduleStatusEmail(user.getEmail(), user.getName(), order.getId(), status);
        });

        return savedOrder;
    }

    // THE DELAYED NOTIFICATION ENGINE
    private void scheduleStatusEmail(String email, String name, int orderId, String newStatus) {

        // This block tells the server to execute the code inside it after a set delay
        scheduler.schedule(() -> {
            String subject = "Zenith Acquisition Update - #" + orderId;
            String html = """
            <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: auto; border: 1px solid #434842; padding: 30px; background-color: #0e0e0e; color: #e5e2e1;">
                <h1 style="color: #b8ccb5; text-transform: uppercase; letter-spacing: 0.2em; font-size: 18px; border-bottom: 1px solid #434842; padding-bottom: 10px;">Zenith Archives</h1>
                
                <p style="margin-top: 30px;">Dear <strong>%s</strong>,</p>
                <p>The status of your acquisition (Order <strong>#%d</strong>) has been officially updated to:</p>
                
                <div style="background-color: #1c1b1b; padding: 15px; text-align: center; margin: 25px 0; border: 1px solid #434842;">
                    <h2 style="margin: 0; color: #b8ccb5; text-transform: uppercase; letter-spacing: 0.1em;">%s</h2>
                </div>
                
                <p>You may track further developments by accessing your Member Profile in the Zenith gateway.</p>
                
                <div style="margin-top: 40px; border-top: 1px solid #434842; padding-top: 20px; font-size: 10px; color: #8d928b; text-transform: uppercase; letter-spacing: 0.1em; text-align: center;">
                    Strictly Confidential | Zenith Operations
                </div>
            </div>
            """.formatted(name, orderId, newStatus);

            try {
                emailService.sendHtmlEmail(email, subject, html);
            } catch (Exception e) {
                System.out.println("Failed to dispatch delayed email: " + e.getMessage());
            }

            // Set the delay here: 5 Minutes
        }, 5, TimeUnit.MINUTES);
    }

    public List<?> getAllOrders() {
        return orderRepo.findAll();
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        Double revenue = orderRepo.getTotalRevenue();
        stats.put("totalRevenue", revenue != null ? revenue : 0.0);
        stats.put("totalOrders", orderRepo.count());
        stats.put("pendingOrders", orderRepo.countPendingOrders());

        return stats;
    }

    public Order updatePaymentStatus(int orderId, String paymentId, String status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentId(paymentId);
        order.setPaymentStatus(status);

        // If payment is successful, we might want to change order status too
        if ("Paid".equalsIgnoreCase(status)) {
            order.setStatus("Confirmed");
        }

        return orderRepo.save(order);
    }
    // 1. Fetch All Orders with formatted DTO
    public List<Map<String, Object>> getAdminOrders() {
        List<Order> orders = orderRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order order : orders) {
            Map<String, Object> dto = new HashMap<>();

            // Map ID exactly as JS expects
            dto.put("id", order.getId());

            dto.put("orderDate", order.getOrderDate());

            String customerName = "Guest";
            Optional<User> userOpt = userRepo.findById(order.getUserId());
            if (userOpt.isPresent()) {
                customerName = userOpt.get().getName();
            }
            dto.put("customerName", customerName);
            dto.put("userId", order.getUserId());

            List<OrderItem> items = orderItemRepo.findByOrderId(order.getId());
            List<Map<String, Object>> itemsList = new ArrayList<>();

            for (OrderItem item : items) {
                Product product = productRepo.findById(item.getProductId()).orElse(null);

                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", product != null ? product.getName() : "Archived Object");
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());

                itemsList.add(itemMap);
            }
            dto.put("items", itemsList);

            dto.put("totalAmount", order.getTotalAmount());
            dto.put("status", order.getStatus());

            result.add(dto);
        }
        return result;
    }

    // 2. Fetch Financial Ledger (Successful Payments Only)
    public List<Map<String, Object>> getFinancialLedger() {
        // Fetch only orders where Razorpay returned "Paid"
        List<Order> paidOrders = orderRepo.findByPaymentStatusIgnoreCase("Paid");
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order order : paidOrders) {
            Map<String, Object> dto = new HashMap<>();

            dto.put("transactionDate", order.getOrderDate());
            dto.put("paymentId", order.getPaymentId());
            dto.put("amount", order.getTotalAmount());

            // Since it's in the Paid list, we can assume it's settled/cleared
            dto.put("settlementStatus", "Settled");

            result.add(dto);
        }
        return result;
    }
}

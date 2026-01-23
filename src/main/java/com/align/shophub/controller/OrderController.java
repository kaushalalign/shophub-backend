package com.align.shophub.controller;


import com.align.shophub.dto.OrderDtos.OrderItemResponse;
import com.align.shophub.dto.OrderDtos.OrderRequest;
import com.align.shophub.dto.OrderDtos.OrderResponse;
import com.align.shophub.dto.OrderStatusDTO;
import com.align.shophub.entity.*;
import com.align.shophub.repository.*;
import com.align.shophub.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserInfoRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private JwtService jwtService;
    @Autowired private AddressRepository addressRepository;
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional // Important: Ensures Order is saved AND Cart is cleared in one go
    public OrderResponse placeOrder(@RequestHeader("Authorization") String token, @RequestBody OrderRequest request) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();

        // 1. Fetch and Validate Address
        Address address = addressRepository.findById(request.getAddressId()) // [cite: 46]
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Security Check: Ensure address belongs to the current user
        if (address.getUser().getId() != user.getId()) {
            throw new RuntimeException("Access Denied: Address does not belong to user");
        }

        // 2. Initialize Order with Address Details
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());

        // --- FIX STARTS HERE ---
        // Map fields from the fetched Address entity to the Order entity
        order.setShippingAddress(address.getAddressLine());
        order.setShippingCity(address.getCity());
        order.setShippingPincode(address.getPincode());

        // Set Payment ID from request
        order.setPaymentId(request.getPaymentId());
        // --- FIX ENDS HERE ---

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // 3. Logic Split: From Cart vs. Buy Now
        if (request.isFromCart()) {
            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Cart is empty"));
            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }
    
            for (CartItem cartItem : cart.getItems()) {
                Product p = cartItem.getProduct();
                
                // Stock Check & Update
                if (cartItem.getQuantity() > p.getTotalStock()) {
                    throw new RuntimeException("Product " + p.getTitle() + " is out of stock or requested quantity unavailable.");
                }
                p.setTotalStock(p.getTotalStock() - cartItem.getQuantity()); // Decrease Stock
                productRepository.save(p);
    
                OrderItem orderItem = createOrderItem(order, p, cartItem.getQuantity());
                orderItems.add(orderItem);
                total = total.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }
            cartItemRepository.deleteByCartId(cart.getId());
        } else {
            // Buy Now Logic
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new RuntimeException("No items provided");
            }
    
            for (var itemReq : request.getItems()) {
                Product p = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                
                // Stock Check & Update
                if (itemReq.getQuantity() > p.getTotalStock()) {
                     throw new RuntimeException("Product " + p.getTitle() + " is out of stock.");
                }
                p.setTotalStock(p.getTotalStock() - itemReq.getQuantity()); // Decrease Stock
                productRepository.save(p);
    
                OrderItem orderItem = createOrderItem(order, p, itemReq.getQuantity());
                orderItems.add(orderItem);
                total = total.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }
        }

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    // Helper method (if not already present)
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder() // Assuming you have a builder or constructor
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .shippingAddress(order.getShippingAddress() + ", " + order.getShippingCity() + " - " + order.getShippingPincode())
                // Map items...
                .build();
    }

    // Helper: Convert OrderItem Entity -> OrderItemResponse DTO
    private OrderItemResponse mapToItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setProductName(item.getProduct().getTitle());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPriceAtPurchase());
        return response;
    }
    // Helper method to create OrderItem
    private OrderItem createOrderItem(Order order, Product product, Integer quantity) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        // Lock in the price at time of purchase
        item.setPriceAtPurchase(product.getSalePrice() != null ? product.getSalePrice() : product.getPrice());
        return item;
    }
    // ADMIN: Update Order and Payment Status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public OrderResponse updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusDTO statusRequest
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Only update if the value is provided (not null)
        if (statusRequest.getOrderStatus() != null && !statusRequest.getOrderStatus().isEmpty()) {
            order.setOrderStatus(statusRequest.getOrderStatus());
        }

        if (statusRequest.getPaymentStatus() != null && !statusRequest.getPaymentStatus().isEmpty()) {
            order.setPaymentStatus(statusRequest.getPaymentStatus());
        }
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<OrderResponse> getMyOrders(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();
        List<Order> orders = orderRepository.findByUserId(user.getId());

        // CHANGE 2: Map the list of entities to a list of DTOs
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
}
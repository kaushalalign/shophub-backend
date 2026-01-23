package com.align.shophub.controller;

import com.align.shophub.dto.CartItemDto;
import com.align.shophub.dto.CartRequest;
import com.align.shophub.dto.CartResponse;
import com.align.shophub.entity.Cart;
import com.align.shophub.entity.CartItem;
import com.align.shophub.entity.Product;
import com.align.shophub.entity.UserInfo;
import com.align.shophub.repository.CartItemRepository;
import com.align.shophub.repository.CartRepository;
import com.align.shophub.repository.ProductRepository;
import com.align.shophub.repository.UserInfoRepository;
import com.align.shophub.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class CartController {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserInfoRepository userRepository;
    @Autowired private JwtService jwtService;

    // Helper to get current user's cart
    private Cart getOrCreateCart(String token) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // 1. View Cart
    @GetMapping
    public CartResponse getCart(@RequestHeader("Authorization") String token) {
        Cart cart = getOrCreateCart(token);
        return mapToCartResponse(cart);
    }

    // 2. Add Item to Cart
    @PostMapping("/add")
    @Transactional
    public CartResponse addToCart(@RequestHeader("Authorization") String token, @RequestBody CartRequest request) {
        Cart cart = getOrCreateCart(token);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check Stock
        if (request.getQuantity() > product.getTotalStock()) {
             throw new RuntimeException("Insufficient stock. Only " + product.getTotalStock() + " available.");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            // Check stock for total quantity
            if (newQuantity > product.getTotalStock()) {
                throw new RuntimeException("Cannot add more. Max stock is " + product.getTotalStock());
            }
            
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }
        
        // Refresh to get latest state
        return mapToCartResponse(cartRepository.findById(cart.getId()).get());
    }
// 3. Update Item Quantity
@PutMapping("/item/{itemId}")
public CartResponse updateQuantity(@RequestHeader("Authorization") String token,
                                   @PathVariable Long itemId,
                                   @RequestBody CartRequest request) {
    Cart cart = getOrCreateCart(token);
    CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
    
    if (!item.getCart().getId().equals(cart.getId())) {
        throw new RuntimeException("Access Denied");
    }

    Product product = item.getProduct();

    if (request.getQuantity() <= 0) {
        cart.getItems().remove(item); // Update in-memory list before return
        cartItemRepository.delete(item);
    } else {
        // Check Stock
        if (request.getQuantity() > product.getTotalStock()) {
            throw new RuntimeException("Cannot increase quantity. Max available: " + product.getTotalStock());
        }
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
    }

    return mapToCartResponse(cartRepository.findById(cart.getId()).get());
}

    // 4. Remove Item
    @DeleteMapping("/item/{itemId}")
    public CartResponse removeItem(@RequestHeader("Authorization") String token, @PathVariable Long itemId) {
        Cart cart = getOrCreateCart(token);

        // Ensure the item belongs to the user's cart
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartItemRepository.deleteById(itemId);

        return mapToCartResponse(cartRepository.findById(cart.getId()).get());
    }

    // 5. Clear Cart
    @DeleteMapping("/clear")
    @Transactional
    public String clearCart(@RequestHeader("Authorization") String token) {
        Cart cart = getOrCreateCart(token);
        cartItemRepository.deleteByCartId(cart.getId());
        return "Cart cleared";
    }

    // Mapper Utility
    private CartResponse mapToCartResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .totalAmount(cart.getTotalAmount())
                .items(cart.getItems().stream().map(item -> {
                    BigDecimal price = item.getProduct().getSalePrice() != null ? item.getProduct().getSalePrice() : item.getProduct().getPrice();
                    return CartItemDto.builder()
                            .itemId(item.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getTitle())
                            .image(item.getProduct().getImage())
                            .price(price)
                            .quantity(item.getQuantity())
                            .subTotal(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                            .availableStock(item.getProduct().getTotalStock()) // Map Stock
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
}
package com.align.shophub.controller;

import com.align.shophub.dto.ProductReviewDto; // Import the new DTO
import com.align.shophub.dto.ReviewRequest;
import com.align.shophub.entity.Product;
import com.align.shophub.entity.ProductReview;
import com.align.shophub.entity.UserInfo;
import com.align.shophub.repository.ProductRepository;
import com.align.shophub.repository.ReviewRepository;
import com.align.shophub.repository.UserInfoRepository;
import com.align.shophub.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserInfoRepository userRepository;
    @Autowired private JwtService jwtService;

    // PUBLIC: Get reviews for a product
    @GetMapping("/product/{productId}")
    public List<ProductReviewDto> getProductReviews(@PathVariable Long productId) {
        // Fetch entities and map to DTOs
        return reviewRepository.findByProductId(productId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // USER: Write a review + Auto-Update Average
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Transactional
    public ProductReviewDto addReview(@RequestHeader("Authorization") String token, @RequestBody ReviewRequest request) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();
        Product product = productRepository.findById(request.getProductId()).orElseThrow();

        // 1. Save the new Review
        ProductReview review = new ProductReview();
        review.setUser(user);
        review.setUserName(user.getName());
        review.setProduct(product);
        review.setReviewMessage(request.getReviewMessage());
        review.setReviewValue(request.getReviewValue());
        
        ProductReview savedReview = reviewRepository.save(review);

        // 2. Automatically Calculate New Average
        List<ProductReview> allReviews = reviewRepository.findByProductId(product.getId());
        
        double totalRating = allReviews.stream()
                .mapToInt(ProductReview::getReviewValue)
                .sum();
        
        double newAverage = totalRating / allReviews.size();
        
        // 3. Update Product Entity
        product.setAverageReview(newAverage);
        productRepository.save(product);

        // 4. Return DTO
        return mapToDto(savedReview);
    }

    // Helper method to convert Entity -> DTO
    private ProductReviewDto mapToDto(ProductReview entity) {
        return ProductReviewDto.builder()
                .id(entity.getId())
                .userName(entity.getUserName())
                .reviewMessage(entity.getReviewMessage())
                .reviewValue(entity.getReviewValue())
                .build();
    }
}
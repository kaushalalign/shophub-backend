package com.align.shophub.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserInfoRepository userRepository;
    @Autowired private JwtService jwtService;

    // PUBLIC: Get reviews for a product
    @GetMapping("/product/{productId}")
    public List<ProductReview> getProductReviews(@PathVariable Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    // USER: Write a review
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ProductReview addReview(@RequestHeader("Authorization") String token, @RequestBody ReviewRequest request) {
        String username = jwtService.extractUsername(token.substring(7));
        UserInfo user = userRepository.findByName(username).orElseThrow();
        Product product = productRepository.findById(request.getProductId()).orElseThrow();

        ProductReview review = new ProductReview();
        review.setUser(user);
        review.setUserName(user.getName());
        review.setProduct(product);
        review.setReviewMessage(request.getReviewMessage());
        review.setReviewValue(request.getReviewValue());

        // Logic to update average rating could go here

        return reviewRepository.save(review);
    }
}
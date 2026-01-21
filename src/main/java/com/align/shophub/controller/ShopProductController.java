package com.align.shophub.controller;



import com.align.shophub.dto.ProductDtos.ProductRequest;
import com.align.shophub.entity.Feature;
import com.align.shophub.entity.Product;
import com.align.shophub.repository.FeatureRepository;
import com.align.shophub.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.align.shophub.service.ProductService;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ShopProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private FeatureRepository featureRepository;
    // PUBLIC: Filter and Search
    @GetMapping("/features")
    public List<Feature> getFeatures() {
        return featureRepository.findAll();
    }
    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String query) {
        return productRepository.searchProducts(category, minPrice, maxPrice, query);
    }
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }
    // PUBLIC: Get Single Product
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ADMIN: Create Product
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Product createProduct(@RequestBody ProductRequest request) {
        Product p = new Product();
        p.setTitle(request.getTitle());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setSalePrice(request.getSalePrice());
        p.setCategory(request.getCategory());
        p.setBrand(request.getBrand());
        p.setTotalStock(request.getTotalStock());
        p.setImage(request.getImage());
        p.setAverageReview(0.0);
        return productRepository.save(p);
    }

    // ADMIN: Update Product
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        Product p = productRepository.findById(id).orElseThrow();
        p.setTitle(request.getTitle());
        p.setPrice(request.getPrice());
        p.setSalePrice(request.getSalePrice());
        p.setTotalStock(request.getTotalStock());
        p.setImage(request.getImage());
        return productRepository.save(p);
    }

    // ADMIN: Delete Product
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "Product deleted";
    }
}
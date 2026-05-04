package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.request.*;
import com.ecommerce.productservice.dto.response.ProductResponse;
import com.ecommerce.productservice.service.impl.ProductServiceImpl;
import com.ecommerce.security_lib.model.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products",
        description = "Product Catalog Management")
public class ProductController {

    private final ProductServiceImpl productService;

    // Public Endpoints

    @GetMapping
    @Operation(summary = "Search products",
            description = "Filter by keyword, category, price")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(defaultValue = "")   String keyword,
            @RequestParam(required = false)    Long categoryId,
            @RequestParam(required = false)    Double minPrice,
            @RequestParam(required = false)    Double maxPrice,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                productService.searchProducts(
                        keyword, categoryId,
                        minPrice, maxPrice, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                productService.getProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<Page<ProductResponse>> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                productService.getProductsByCategory(
                        categoryId, page, size));
    }

    // Protected Endpoints

    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Create product",
            description = "Only SELLER or ADMIN")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {

        return ResponseEntity.ok(
                productService.createProduct(
                        request, user.getUserId()));
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Get my products",
            description = "Seller sees their own products")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                productService.getProductsBySeller(
                        user.getUserId(), page, size));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(
                productService.updateProduct(
                        id, request, user.getUserId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Delete product (soft delete)")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        productService.deleteProduct(id, user.getUserId());
        return ResponseEntity.ok(
                "Product deleted successfully");
    }

    // Internal Endpoints (Order Service)

    @PutMapping("/{id}/stock/reduce")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Reduce stock",
            description = "Called by Order Service internally")
    public ResponseEntity<String> reduceStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        productService.reduceStock(id, request.getQuantity());
        return ResponseEntity.ok("Stock reduced successfully");
    }

    @PutMapping("/{id}/stock/restore")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Restore stock",
            description = "Called when order is cancelled")
    public ResponseEntity<String> restoreStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        productService.restoreStock(id, request.getQuantity());
        return ResponseEntity.ok("Stock restored successfully");
    }
}
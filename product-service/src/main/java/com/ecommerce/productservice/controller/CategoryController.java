package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.request.CategoryRequest;
import com.ecommerce.productservice.dto.response.CategoryResponse;
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
@RequestMapping("/api/products/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category apis")
public class CategoryController {

    private final ProductServiceImpl productService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Category", description = "Only ADMIN can create categories")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal AuthenticatedUser user){

        return ResponseEntity.ok(productService.createCategory(request));
    }

    @GetMapping
    @Operation(summary = "Get All Categories", description = "Public endpoint")
    public ResponseEntity<Page<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        return ResponseEntity.ok(productService.getAllCategories(page, size));
    }
}

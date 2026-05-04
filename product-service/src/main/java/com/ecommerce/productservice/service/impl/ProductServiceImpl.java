package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.dto.request.CategoryRequest;
import com.ecommerce.productservice.dto.request.ProductRequest;
import com.ecommerce.productservice.dto.response.CategoryResponse;
import com.ecommerce.productservice.dto.response.ProductResponse;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.BadRequestException;
import com.ecommerce.productservice.exception.ResourceNotFoundException;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Category methods

    public CategoryResponse createCategory(CategoryRequest req){

        if(categoryRepository.existsByName((req.getName())))
                   throw new BadRequestException(
                       "Category already exists:" + req.getName());

        Category category = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .build();

        return toCategoryResponse(categoryRepository.save(category));

    }

    public Page<CategoryResponse> getAllCategories(int page, int size){
        return categoryRepository
                .findAll(PageRequest.of(page, size))
                .map(this::toCategoryResponse);
    }

    // Product method

    public ProductResponse createProduct(ProductRequest req, Long sellerId){

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .stock(req.getStock())
                .imageUrl(req.getImageUrl())
                .category(category)
                .sellerId(sellerId)
                .active(true)
                .build();

        return toProductResponse(productRepository.save(product));
    }

    public Page<ProductResponse> searchProducts(
            String keyword, Long categoryId,
            Double minPrice, Double maxPrice,
            int page, int size){

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        return productRepository.searchProduct(keyword, categoryId,
                minPrice, maxPrice, pageable)
                .map(this::toProductResponse);
    }

    public ProductResponse getProductById(Long id){
        return toProductResponse(findActiveProduct(id));
    }

    public Page<ProductResponse> getProductsBySeller(Long sellerId, int page, int size){

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        return productRepository
                .findBySellerIdAndActiveTrue(sellerId, pageable)
                .map(this::toProductResponse);
    }

    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size){

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        return productRepository
                .findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(this::toProductResponse);
    }

    public ProductResponse updateProduct(Long id, ProductRequest req, Long sellerId){

        Product product = findActiveProduct(id);

        if(!product.getSellerId().equals(sellerId))
            throw new BadRequestException("You only can update your own products");

        Category category = categoryRepository
                .findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        product.setImageUrl(req.getImageUrl());
        product.setCategory(category);

        return toProductResponse(productRepository.save(product));

    }

    @Transactional
    public void deleteProduct(Long id, Long sellerId){
        Product product = findActiveProduct(id);

        if(!product.getSellerId().equals(sellerId))
            throw new BadRequestException("You can only delete your own products");

        product.setActive(false);
        productRepository.save(product);
        log.info("Product soft deleted: {}", id);
    }

    @Transactional
    public void reduceStock(Long id, int quantity){
        Product product = findActiveProduct(id);

        if(product.getStock() < quantity)
            throw new BadRequestException(
                    "Insufficient stock for: "
                    +product.getName()
                    + " (available: "
                    + product.getStock() + ")");

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        log.info("Stock reduced for product {} : {}", id, product.getStock());
    }

    public void restoreStock(Long id, int quantity){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + id));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
        log.info("Stock restored for product {} : {} remaining",
                id, product.getStock());
    }

    // Helper methods

    private Product findActiveProduct(Long id){
        return productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: "+ id));
    }

    private ProductResponse toProductResponse(Product p){
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .imageUrl(p.getImageUrl())
                .categoryName(p.getCategory() != null ?
                        p.getCategory().getName() : null)
                .categoryId(p.getCategory() != null ?
                        p.getCategory().getId() : null)
                .sellerId(p.getSellerId())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private CategoryResponse toCategoryResponse(Category c){
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }
}

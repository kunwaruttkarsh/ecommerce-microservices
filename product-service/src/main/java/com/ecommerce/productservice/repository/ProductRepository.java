package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Long> {

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProduct(
            @Param("keyword")    String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice")   Double minPrice,
            @Param("maxPrice")   Double maxPrice,
            Pageable pageable);

    Page<Product> findBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
}

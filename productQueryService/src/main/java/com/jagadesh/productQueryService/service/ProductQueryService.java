package com.jagadesh.productQueryService.service;

import com.jagadesh.productQueryService.dto.ProductResponseDto;
import com.jagadesh.productQueryService.exception.ProductDoesNotExistException;
import com.jagadesh.productQueryService.model.Product;
import com.jagadesh.productQueryService.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductQueryService {

    private ProductRepo productRepo;

    public ProductQueryService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<ProductResponseDto> getAllProducts() {

        List<ProductResponseDto> products = productRepo.findAll().stream()
                .map(this::toDto)
                .toList();

        return products;
    }

    public ProductResponseDto getProductById(Long id) {
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ProductDoesNotExistException("Product with id " + id + " does not exist"));

        return toDto(existingProduct);
    }

    public ProductResponseDto getProductByName(String productName) {
        Product existingProduct = productRepo.findByName(productName)
                .orElseThrow(() -> new ProductDoesNotExistException("Product with name " + productName + " does not exist"));

        return toDto(existingProduct);
    }

    private ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().doubleValue()
        );
    }
}

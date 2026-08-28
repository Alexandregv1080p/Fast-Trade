package com.fasttrade.api.product.controller;

import com.fasttrade.api.common.dto.PageResponse;
import com.fasttrade.api.product.entity.Product;
import com.fasttrade.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping
    public ResponseEntity<PageResponse<Product>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.getAll(page, pageSize, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(
            @jakarta.validation.Valid @RequestBody com.fasttrade.api.product.dto.ProductRequest data) {
        return ResponseEntity.ok(service.create(data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody com.fasttrade.api.product.dto.ProductRequest data) {
        return ResponseEntity.ok(service.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Product> activate(@PathVariable Long id) {
        return ResponseEntity.ok(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Product> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(service.deactivate(id));
    }
}

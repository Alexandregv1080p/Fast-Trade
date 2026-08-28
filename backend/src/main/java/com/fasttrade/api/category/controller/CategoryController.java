package com.fasttrade.api.category.controller;

import com.fasttrade.api.category.entity.Category;
import com.fasttrade.api.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(service.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<Category> create(
            @jakarta.validation.Valid @RequestBody com.fasttrade.api.category.dto.CategoryRequest data) {
        return ResponseEntity.ok(service.createCategory(data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> update(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody com.fasttrade.api.category.dto.CategoryRequest data) {
        return ResponseEntity.ok(service.updateCategory(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

package com.fasttrade.api.category.controller;

import com.fasttrade.api.category.entity.Subcategory;
import com.fasttrade.api.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subcategory")
@RequiredArgsConstructor
public class SubcategoryController {

    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<Subcategory>> getAll() {
        return ResponseEntity.ok(service.getAllSubcategories());
    }

    @PostMapping
    public ResponseEntity<Subcategory> create(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(service.createSubcategory(data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Subcategory> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(service.updateSubcategory(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteSubcategory(id);
        return ResponseEntity.noContent().build();
    }
}

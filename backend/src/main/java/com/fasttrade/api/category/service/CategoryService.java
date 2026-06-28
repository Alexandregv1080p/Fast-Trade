package com.fasttrade.api.category.service;

import com.fasttrade.api.category.entity.Category;
import com.fasttrade.api.category.entity.Subcategory;
import com.fasttrade.api.category.repository.CategoryRepository;
import com.fasttrade.api.category.repository.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final SubcategoryRepository subcategoryRepo;

    // ── Categories ───────────────────────────────────────────────────────────

    public List<Category> getAllCategories() { return categoryRepo.findAll(); }

    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

    public Category createCategory(Map<String, String> data) {
        var c = new Category();
        c.setName(data.get("name"));
        c.setSlug(data.get("slug"));
        return categoryRepo.save(c);
    }

    public Category updateCategory(Long id, Map<String, String> data) {
        Category c = getCategoryById(id);
        if (data.containsKey("name")) c.setName(data.get("name"));
        if (data.containsKey("slug")) c.setSlug(data.get("slug"));
        return categoryRepo.save(c);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        categoryRepo.deleteById(id);
    }

    // ── Subcategories ─────────────────────────────────────────────────────────

    public List<Subcategory> getAllSubcategories() { return subcategoryRepo.findAll(); }

    public Subcategory getSubcategoryById(Long id) {
        return subcategoryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subcategoria não encontrada"));
    }

    public Subcategory createSubcategory(Map<String, Object> data) {
        var s = new Subcategory();
        s.setName((String) data.get("name"));
        s.setSlug((String) data.get("slug"));
        if (data.get("categoryId") != null) {
            Long catId = Long.valueOf(data.get("categoryId").toString());
            s.setCategory(getCategoryById(catId));
        }
        return subcategoryRepo.save(s);
    }

    public Subcategory updateSubcategory(Long id, Map<String, Object> data) {
        Subcategory s = getSubcategoryById(id);
        if (data.containsKey("name")) s.setName((String) data.get("name"));
        if (data.containsKey("slug")) s.setSlug((String) data.get("slug"));
        if (data.containsKey("categoryId") && data.get("categoryId") != null) {
            Long catId = Long.valueOf(data.get("categoryId").toString());
            s.setCategory(getCategoryById(catId));
        }
        return subcategoryRepo.save(s);
    }

    public void deleteSubcategory(Long id) {
        if (!subcategoryRepo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        subcategoryRepo.deleteById(id);
    }
}

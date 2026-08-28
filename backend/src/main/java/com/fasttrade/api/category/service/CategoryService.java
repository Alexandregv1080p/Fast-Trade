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

    public Category createCategory(com.fasttrade.api.category.dto.CategoryRequest data) {
        var c = new Category();
        c.setName(data.getName());
        c.setSlug(data.getSlug());
        return categoryRepo.save(c);
    }

    public Category updateCategory(Long id, com.fasttrade.api.category.dto.CategoryRequest data) {
        Category c = getCategoryById(id);
        if (data.getName() != null) c.setName(data.getName());
        if (data.getSlug() != null) c.setSlug(data.getSlug());
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

    public Subcategory createSubcategory(com.fasttrade.api.category.dto.SubcategoryRequest data) {
        var s = new Subcategory();
        s.setName(data.getName());
        s.setSlug(data.getSlug());
        if (data.getCategoryId() != null) {
            s.setCategory(getCategoryById(data.getCategoryId()));
        }
        return subcategoryRepo.save(s);
    }

    public Subcategory updateSubcategory(Long id, com.fasttrade.api.category.dto.SubcategoryRequest data) {
        Subcategory s = getSubcategoryById(id);
        if (data.getName() != null) s.setName(data.getName());
        if (data.getSlug() != null) s.setSlug(data.getSlug());
        if (data.getCategoryId() != null) {
            s.setCategory(getCategoryById(data.getCategoryId()));
        }
        return subcategoryRepo.save(s);
    }

    public void deleteSubcategory(Long id) {
        if (!subcategoryRepo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        subcategoryRepo.deleteById(id);
    }
}

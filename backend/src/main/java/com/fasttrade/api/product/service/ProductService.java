package com.fasttrade.api.product.service;

import com.fasttrade.api.category.repository.CategoryRepository;
import com.fasttrade.api.category.repository.SubcategoryRepository;
import com.fasttrade.api.common.dto.PageResponse;
import com.fasttrade.api.product.entity.Product;
import com.fasttrade.api.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;
    private final CategoryRepository categoryRepo;
    private final SubcategoryRepository subcategoryRepo;
    private final com.fasttrade.api.config.FeeConfig feeConfig;

    /** Preenche a comissão de exibição a partir da config. */
    private Product withCommission(Product p) {
        if (p != null) p.setCommission(feeConfig.commissionOn(p.getPrice()));
        return p;
    }

    public PageResponse<Product> getAll(int page, int pageSize, String search) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        var result = (search == null || search.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNameContainingIgnoreCase(search, pageable);
        result.getContent().forEach(this::withCommission);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }

    public Product getById(Long id) {
        return withCommission(repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado")));
    }

    public Product create(com.fasttrade.api.product.dto.ProductRequest data) {
        var p = new Product();
        applyData(p, data);
        return withCommission(repo.save(p));
    }

    public Product update(Long id, com.fasttrade.api.product.dto.ProductRequest data) {
        Product p = getById(id);
        applyData(p, data);
        return withCommission(repo.save(p));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repo.deleteById(id);
    }

    public Product activate(Long id) {
        Product p = getById(id);
        p.setIsActive(true);
        return repo.save(p);
    }

    public Product deactivate(Long id) {
        Product p = getById(id);
        p.setIsActive(false);
        return repo.save(p);
    }

    private void applyData(Product p, com.fasttrade.api.product.dto.ProductRequest d) {
        if (d.getName() != null) p.setName(d.getName());
        if (d.getDescription() != null) p.setDescription(d.getDescription());
        if (d.getPrice() != null) p.setPrice(d.getPrice());
        if (d.getStock() != null) p.setStock(d.getStock());
        if (d.getIsActive() != null) p.setIsActive(d.getIsActive());
        if (d.getSku() != null) p.setSku(d.getSku());
        if (d.getCondition() != null) p.setCondition(d.getCondition());
        if (d.getImageUrl() != null) p.setImageUrl(d.getImageUrl());
        if (d.getCategoryId() != null) {
            categoryRepo.findById(d.getCategoryId()).ifPresent(p::setCategory);
        }
        if (d.getSubcategoryId() != null) {
            subcategoryRepo.findById(d.getSubcategoryId()).ifPresent(p::setSubcategory);
        }
    }
}

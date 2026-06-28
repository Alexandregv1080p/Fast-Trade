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

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;
    private final CategoryRepository categoryRepo;
    private final SubcategoryRepository subcategoryRepo;

    public PageResponse<Product> getAll(int page, int pageSize, String search) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        var result = (search == null || search.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNameContainingIgnoreCase(search, pageable);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }

    public Product getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    public Product create(Map<String, Object> data) {
        var p = new Product();
        applyData(p, data);
        return repo.save(p);
    }

    public Product update(Long id, Map<String, Object> data) {
        Product p = getById(id);
        applyData(p, data);
        return repo.save(p);
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

    private void applyData(Product p, Map<String, Object> data) {
        if (data.containsKey("name")) p.setName((String) data.get("name"));
        if (data.containsKey("description")) p.setDescription((String) data.get("description"));
        if (data.containsKey("price")) p.setPrice(new BigDecimal(data.get("price").toString()));
        if (data.containsKey("stock")) p.setStock(Integer.valueOf(data.get("stock").toString()));
        if (data.containsKey("isActive")) p.setIsActive((Boolean) data.get("isActive"));
        if (data.containsKey("sku")) p.setSku((String) data.get("sku"));
        if (data.containsKey("condition")) p.setCondition((String) data.get("condition"));
        if (data.containsKey("imageUrl")) p.setImageUrl((String) data.get("imageUrl"));
        if (data.containsKey("categoryId") && data.get("categoryId") != null) {
            Long catId = Long.valueOf(data.get("categoryId").toString());
            categoryRepo.findById(catId).ifPresent(p::setCategory);
        }
        if (data.containsKey("subcategoryId") && data.get("subcategoryId") != null) {
            Long subId = Long.valueOf(data.get("subcategoryId").toString());
            subcategoryRepo.findById(subId).ifPresent(p::setSubcategory);
        }
    }
}

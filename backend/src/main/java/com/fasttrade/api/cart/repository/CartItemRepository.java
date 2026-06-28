package com.fasttrade.api.cart.repository;

import com.fasttrade.api.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserEmailOrderByCreatedAtAsc(String email);

    Optional<CartItem> findByUserEmailAndProductId(String email, Long productId);

    // Subquery-based bulk deletes (evita path expression inválido em DELETE JPQL)
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user.id IN (SELECT u.id FROM User u WHERE u.email = :email)")
    void deleteByUserEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.product.id = :productId AND c.user.id IN (SELECT u.id FROM User u WHERE u.email = :email)")
    void deleteByUserEmailAndProductId(String email, Long productId);
}

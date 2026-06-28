package com.fasttrade.api.user.service;

import com.fasttrade.api.common.dto.PageResponse;
import com.fasttrade.api.user.entity.User;
import com.fasttrade.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<User> getAll(int page, int pageSize, String search) {
        var pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        var result = (search == null || search.isBlank())
                ? repo.findAll(pageable)
                : repo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        return PageResponse.of(result.getContent(), page, pageSize, result.getTotalElements());
    }

    public User getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public User update(Long id, Map<String, Object> data) {
        User user = getById(id);
        if (data.containsKey("name")) user.setName((String) data.get("name"));
        if (data.containsKey("phone")) user.setPhone((String) data.get("phone"));
        if (data.containsKey("cpfCnpj")) user.setCpfCnpj((String) data.get("cpfCnpj"));
        return repo.save(user);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        repo.deleteById(id);
    }

    public User block(Long id) {
        User user = getById(id);
        user.setBlocked(true);
        return repo.save(user);
    }

    public User unblock(Long id) {
        User user = getById(id);
        user.setBlocked(false);
        return repo.save(user);
    }

    public User giveTrades(Long id, Integer value) {
        User user = getById(id);
        user.setTrades(user.getTrades() + value);
        return repo.save(user);
    }

    public User updateSelf(String email, Map<String, Object> data) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (data.containsKey("name"))      user.setName((String) data.get("name"));
        if (data.containsKey("phone"))     user.setPhone((String) data.get("phone"));
        if (data.containsKey("birthDate")) user.setBirthDate((String) data.get("birthDate"));
        if (data.containsKey("addressStreet")) user.setAddressStreet((String) data.get("addressStreet"));
        if (data.containsKey("addressCity"))   user.setAddressCity((String) data.get("addressCity"));
        if (data.containsKey("addressState"))  user.setAddressState((String) data.get("addressState"));
        if (data.containsKey("addressZip"))    user.setAddressZip((String) data.get("addressZip"));
        return repo.save(user);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha atual incorreta");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha deve ter no mínimo 8 caracteres");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
    }
}

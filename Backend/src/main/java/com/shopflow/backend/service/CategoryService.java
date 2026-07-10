package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.CategoryRequest;
import com.shopflow.backend.dto.response.CategoryResponse;
import com.shopflow.backend.entity.Category;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.exception.DataBaseException;
import com.shopflow.backend.exception.DuplicateException;
import com.shopflow.backend.mapper.CategoryMapper;
import com.shopflow.backend.repository.CategoryRepository;

import com.shopflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    private final CloudinaryService cloudinaryService;


    public CategoryResponse create(CategoryRequest request) {
        String normalizedName = normalize(request.name());

        if (categoryRepository.existsByNameIgnoreCaseAndActiveTrue(normalizedName)) {
            throw new DuplicateException("Já existe uma categoria com o nome " + normalizedName);
        }

        User authenticatedUser = getAuthenticatedUser();

        Category newCategory = Category.builder()
                .name(normalizedName)
                .description(request.description())
                .slug(gerarSlug(normalizedName))
                .imageUrl(null)
                .user(authenticatedUser)
                .active(true)
                .build();
        return categoryMapper.toResponse(categoryRepository.save(newCategory));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        if (isAdmin()){
            return categoryMapper.toResponseList(categoryRepository.findAll());
        }
        return categoryMapper.toResponseList(categoryRepository.findAllByActiveTrue());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedName = normalize(name);

        if(isAdmin()){
            return categoryMapper.toResponseList(categoryRepository.findByNameForAdmin(normalizedName));
        }
        return categoryMapper.toResponseList(categoryRepository.findByNameForUser(normalizedName));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(UUID id) {
        Category category = getCategoryById(id);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public void deactivate(UUID id) {
        Category category = getCategoryById(id);
        if(!category.isActive()){
            throw new DataBaseException("Esta categoria já está inativa");
        }
        category.setActive(false);
        categoryRepository.save(category);
    }

    public void reactivate(UUID id){
        Category category = getCategoryById(id);
        if(category.isActive()){
            throw new DataBaseException("Essa categoria já está ativa");
        }
        category.setActive(true);
        categoryRepository.save(category);
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = getCategoryById(id);

        String normalizedName = normalize(request.name());
        if (categoryRepository.existsByNameIgnoreCaseAndActiveTrue(normalizedName)
                && !category.getName().equalsIgnoreCase(normalizedName)) {
            throw new DuplicateException("Já existe uma categoria com o nome " + normalizedName);
        }
        if (!category.isActive()) {
            throw new DataBaseException("Está categoria está inativa");
        }

        category.setName(normalizedName);
        category.setDescription(request.description());
        category.setSlug(gerarSlug(normalizedName));
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse uploadImage(UUID id, MultipartFile file) {
        Category category = getCategoryById(id);
        if(!category.isActive()){
            throw new DataBaseException("Esta categoria não está ativa");
        }
        if(category.getImageUrl() != null && !category.getImageUrl().isBlank()){
            cloudinaryService.deleteImage(category.getImageUrl());
        }
        String imageUrl = cloudinaryService.uploadImage(file, "categories");
        category.setImageUrl(imageUrl);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

   private boolean isAdmin(){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       return authentication != null && authentication.getAuthorities().stream()
               .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
   }

   private Category getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataBaseException("Nenhuma categoria encontrada com o id: " + id));
        if(isAdmin()) {
            return category;
        }
        if(!category.isActive()){
            throw new DataBaseException("Essa categoria não está ativa");
        }
        return category;
    }

   private User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new DataBaseException("Usuário não está autenticado");
        }
       JWTUserData jwtUserData = (JWTUserData) authentication.getPrincipal();
        return userRepository.findUserByEmail(jwtUserData.email())
                .orElseThrow(() -> new DataBaseException("O usuário não foi encontrado no banco de dados"));
   }

   private String normalize(String text) {
        return Normalizer.normalize(text.trim().replaceAll("\\s+", " "), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
    }

   private String gerarSlug(String normalizedName) {
        return normalizedName
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }
}

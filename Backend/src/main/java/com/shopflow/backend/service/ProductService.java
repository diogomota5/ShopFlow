package com.shopflow.backend.service;

import com.shopflow.backend.dto.request.ProductRequest;
import com.shopflow.backend.dto.request.ProductUpdateRequest;
import com.shopflow.backend.dto.response.ProductResponse;
import com.shopflow.backend.entity.Product;
import com.shopflow.backend.exception.DataBaseException;
import com.shopflow.backend.exception.DuplicateException;
import com.shopflow.backend.mapper.ProductMapper;
import com.shopflow.backend.repository.CategoryRepository;
import com.shopflow.backend.repository.ProductRepository;
import com.shopflow.backend.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;

    public ProductResponse create(ProductRequest request) {
        String normalizedName = normalize(request.name());

        if(productRepository.existsByNameIgnoreCaseAndActiveTrue(normalizedName)){
            throw new DuplicateException("Já existe um produto com esse nome");
        }
        if(productRepository.existsBySku(request.sku())) {
            throw new DuplicateException("Já existe um produto com esse sku");
        }

        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new DataBaseException("Nenhuma categoria encontrada com o id: " + request.categoryId()));

        if(!category.isActive()){
            throw new DataBaseException("A categoria não está ativa");
        }

        Product newProduct = Product.builder()
                .name(normalizedName)
                .description(request.description())
                .price(request.price())
                .originalPrice(request.originalPrice())
                .stock(request.stock())
                .sku(request.sku().toUpperCase())
                .imageUrl(null)
                .active(true)
                .category(category)
                .build();

        return productMapper.toResponse(productRepository.save(newProduct));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String name, UUID categoryId, BigDecimal precoMin, BigDecimal precoMax, Pageable pageable) {
        String searchName = (name != null && !name.isBlank()) ? normalize(name) : null;

        Boolean active = isAdmin() ? null : true;

        Specification<Product> spec = ProductSpecification
                .comFiltros(searchName, categoryId, precoMin, precoMax, active);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id){
        Product product = getProductById(id);

        if(!isAdmin() && !product.getActive()){
            throw new DataBaseException("Produto não encontrado");
        }
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findLowStock(Integer limitStock) {
        return productRepository.findByStockLessThanEqualAndActiveTrue(limitStock)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
/*
    @Transactional(readOnly = true)
    public List<ProductResponse> findMostSold(Pageable pageable) {
        return productRepository.findMostSold(pageable)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
*/
    @Transactional(readOnly = true)
    public long countActiveProducts() {
        return productRepository.countByActiveTrue();
    }

    @Transactional(readOnly = true)
    public long countActiveProductsByCategoryId(UUID categoryId) {
        return productRepository.countByCategoryIdAndActiveTrue(categoryId);
    }

    @Transactional
    public void deactivate(UUID id) {
        Product product = getProductById(id);
        if(product.getActive().equals(false)) {
            throw new DataBaseException("Este produto já está inativo");
        }
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public void reactivate(UUID id){
        Product product = getProductById(id);
        if (product.getActive().equals(true)) {
            throw new DataBaseException("Este produto já está ativo");
        }
        product.setActive(true);
        productRepository.save(product);
    }

    @Transactional
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        Product productUpdate = getProductById(id);

        String normalizedName = normalize(request.name());
        if (productRepository.existsByNameIgnoreCaseAndActiveTrue(normalizedName)
                && !productUpdate.getName().equalsIgnoreCase(normalizedName)) {
            throw new DuplicateException("Já existe um produto com esse nome");
        }

        if(productRepository.existsBySkuAndIdNot(request.sku(), id)) {
            throw new DuplicateException("Este SKU já está sendo usado por outro produto");
        }

        if(!productUpdate.getActive()){
            throw new DataBaseException("Este produto está inativo");
        }

        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new DataBaseException("Nenhuma categoria encontrada com o id: " + request.categoryId()));

        if(!category.isActive()){
            throw new DataBaseException("A categoria não está ativa");
        }

        productUpdate.setName(normalizedName);
        productUpdate.setDescription(request.description());
        productUpdate.setPrice(request.price());
        productUpdate.setOriginalPrice(request.originalPrice());
        productUpdate.setStock(request.stock());
        productUpdate.setSku(request.sku().toUpperCase());
        productUpdate.setCategory(category);
        productRepository.save(productUpdate);

        return productMapper.toResponse(productUpdate);
    }

    @Transactional
    public ProductResponse uploadImage(UUID id, MultipartFile file) {
        Product product = getProductById(id);

        if(product.getActive().equals(false)) {
            throw new DataBaseException("Este produto não está ativo");
        }

        if(product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
            cloudinaryService.deleteImage(product.getImageUrl());
        }

        String imageUrl = cloudinaryService.uploadImage(file, "products");

        product.setImageUrl(imageUrl);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    private boolean isAdmin(){
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataBaseException("Nenhum produto encontrado com o id: " + id));
    }

    private String normalize(String text) {
        return Normalizer.normalize(text.trim().replaceAll("\\s+", " "), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
    }
}

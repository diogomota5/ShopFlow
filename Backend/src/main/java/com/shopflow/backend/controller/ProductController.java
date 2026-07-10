package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.ProductRequest;
import com.shopflow.backend.dto.request.ProductUpdateRequest;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.dto.response.ProductResponse;
import com.shopflow.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request){
        ProductResponse created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Produto criado com sucesso", created, HttpStatus.CREATED));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @PageableDefault(size = 12, sort = "name") Pageable pageable){

        Page<ProductResponse> products = productService.findAll(name, categoryId, precoMin, precoMax, pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Produtos encontrados com sucesso", products, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/lowStock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findLowStock(@RequestParam(defaultValue = "5") Integer limitStock){
        List<ProductResponse> products = productService.findLowStock(limitStock);
        return ResponseEntity.ok(
                ApiResponse.success("Produtos com estoque baixo encontrados com sucesso", products, HttpStatus.OK));
    }
/*
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/most-sold")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findMostSold(@PageableDefault(size = 5) Pageable pageable){
        List<ProductResponse> products = productService.findMostSold(pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Produtos mais vendidos encontrados com sucessos", products, HttpStatus.OK));
    }
*/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/active")
    public ResponseEntity<ApiResponse<Long>> countActive(){
        return ResponseEntity.ok(
                ApiResponse.success("Total de produtos ativos", productService.countActiveProducts(),HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/category/{categoryId}")
    public ResponseEntity<ApiResponse<Long>> countByCategory(@PathVariable UUID categoryId){
        return ResponseEntity.ok(
                ApiResponse.success("Total de produtos ativos na categoria", productService.countActiveProductsByCategoryId(categoryId), HttpStatus.OK));
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable UUID id){
        ProductResponse product = productService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Produto encontrado com sucesso", product, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id){
        productService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Produto desativado com sucesso", HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reactivate/{id}")
    public ResponseEntity<ApiResponse<Void>> reactivate(@PathVariable UUID id){
        productService.reactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Produto reativado com sucesso", HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request){
        ProductResponse updated = productService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Produto atualizado com sucesso", updated, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload/image/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file){
        ProductResponse updated = productService.uploadImage(id, file);
        return ResponseEntity.ok(
                ApiResponse.success("Imagem do produto atualizada com sucesso", updated, HttpStatus.OK));
    }
}
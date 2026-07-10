package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.CategoryRequest;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.dto.response.CategoryResponse;
import com.shopflow.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Categoria criada com sucesso", created, HttpStatus.CREATED));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll(){
        List<CategoryResponse> response = categoryService.findAll();
        return ResponseEntity.ok(
                ApiResponse.success("Categorias encontradas com sucesso", response, HttpStatus.OK));
    }

    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findByName(@RequestParam String name){
            List<CategoryResponse> response = categoryService.findByName(name);
            return ResponseEntity.ok(
                    ApiResponse.success("Categorias encontradas com sucesso", response, HttpStatus.OK));
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable UUID id){
        CategoryResponse response = categoryService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Categorias encontradas com sucesso", response, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id){
        categoryService.deactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Categoria desativada com sucesso", HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reactivate/{id}")
    public ResponseEntity<ApiResponse<Void>> reactivate(@PathVariable UUID id){
        categoryService.reactivate(id);
        return ResponseEntity.ok(
                ApiResponse.success("Categoria reativada com sucesso", HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request){
        CategoryResponse update = categoryService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Categoria atualizada com sucesso", update, HttpStatus.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload/image/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> uploadImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file){
        CategoryResponse updated = categoryService.uploadImage(id, file);
        return ResponseEntity.ok(
                ApiResponse.success("Imagem da categoria atualizada com sucesso", updated, HttpStatus.OK));
    }
}

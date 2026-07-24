package com.shopflow.backend.controller;

import com.shopflow.backend.dto.request.AssessmentRequest;
import com.shopflow.backend.dto.response.ApiResponse;
import com.shopflow.backend.dto.response.AssessmentResponse;
import com.shopflow.backend.dto.response.ProductRatingResponse;
import com.shopflow.backend.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AssessmentResponse>> createAssessment(@Valid @RequestBody AssessmentRequest request){
        AssessmentResponse response = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Avaliação enviada com sucesso", response, HttpStatus.CREATED));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<AssessmentResponse>>> getProductAssessments(
            @PathVariable UUID productId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        Page<AssessmentResponse> response = assessmentService.getProductAssessments(productId, pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Avaliações recuperadas", response, HttpStatus.OK));
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ApiResponse<ProductRatingResponse>> getProductStats(@PathVariable UUID productId){
        ProductRatingResponse stats = assessmentService.getProductStats(productId);
        return ResponseEntity.ok(
                ApiResponse.success("Estatísticas calculadas", stats, HttpStatus.OK));
    }
}

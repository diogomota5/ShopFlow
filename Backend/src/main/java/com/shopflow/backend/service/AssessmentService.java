package com.shopflow.backend.service;

import com.shopflow.backend.config.JWTUserData;
import com.shopflow.backend.dto.request.AssessmentRequest;
import com.shopflow.backend.dto.response.AssessmentResponse;
import com.shopflow.backend.dto.response.ProductRatingResponse;
import com.shopflow.backend.entity.Assessment;
import com.shopflow.backend.entity.Product;
import com.shopflow.backend.entity.User;
import com.shopflow.backend.exception.SecurityAuthenticationException;
import com.shopflow.backend.mapper.AssessmentMapper;
import com.shopflow.backend.repository.AssessmentRepository;
import com.shopflow.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final ProductRepository productRepository;
    private final AssessmentMapper assessmentMapper;

    @Transactional
    public AssessmentResponse createAssessment(AssessmentRequest request){
        UUID userId = getLoggedUserId();

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + request.productId()));

        boolean isVerified = assessmentRepository.hasUserPurchasedProduct(userId, product.getId());

        Assessment assessment = assessmentMapper.toEntity(request);
        assessment.setUser(User.builder().id(userId).build());
        assessment.setProduct(product);
        assessment.setVerified(isVerified);

        return assessmentMapper.toResponse(assessmentRepository.save(assessment));
    }

    @Transactional(readOnly = true)
    public Page<AssessmentResponse> getProductAssessments(UUID productId, Pageable pageable){
        return assessmentRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(assessmentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductRatingResponse getProductStats(UUID productId){
        if (!productRepository.existsById(productId)){
            throw new EntityNotFoundException("Produto não encontrado: " + productId);
        }
        return assessmentRepository.getProductRatingStats(productId);
    }

    private UUID getLoggedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken){
            throw new SecurityAuthenticationException("Usuário não está autenticado");
        }
        return ((JWTUserData) authentication.getPrincipal()).userId();
    }
}

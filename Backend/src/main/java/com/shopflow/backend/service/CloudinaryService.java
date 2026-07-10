package com.shopflow.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shopflow.backend.exception.DataBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        if(file == null || file.isEmpty()) {
            throw new DataBaseException("Nenhuma imagem foi enviada");
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new DataBaseException("O arquivo enviado não é uma imagem");
        }

        if(file.getSize() > 5 * 1024 * 1024){
            throw new DataBaseException("A imagem deve ter no máximo 5 MB");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "shopflow/" + folder,
                            "public_id", UUID.randomUUID().toString(),
                            "transformation", "w_800,h_800,c_limit,q_auto,f_auto"
                    )
            );
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new DataBaseException("Erro ao fazer upload da imagem: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl){
        if(imageUrl == null || imageUrl.isBlank()) {
            throw new DataBaseException("Não há imagem para deletar");
        }

        try{
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch(IOException e){
            throw new DataBaseException("Erro ao deletar a imagem: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        int uploadIndex = imageUrl.indexOf("/upload");
        if (uploadIndex == -1) return imageUrl;

        String afterUpload = imageUrl.substring(uploadIndex + 8);

        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1); // +1 pula a barra da versão
        }

        if (afterUpload.startsWith("/")) {
            afterUpload = afterUpload.substring(1);
        }

        int dotIndex = afterUpload.lastIndexOf(".");
        return dotIndex != -1 ? afterUpload.substring(0, dotIndex) : afterUpload;
    }
}

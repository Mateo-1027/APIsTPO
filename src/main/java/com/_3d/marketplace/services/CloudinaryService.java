package com._3d.marketplace.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map<?, ?> uploadFile(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", "marketplace_products")
        );
    }

    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.warn("No se pudo borrar {} de Cloudinary: {}", publicId, e.getMessage());
        }
    }
}

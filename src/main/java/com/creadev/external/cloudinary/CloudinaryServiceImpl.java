package com.creadev.external.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.creadev.util.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException(UPLOAD_FAILED, e);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(DELETE_FAILED, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(FILE_EMPTY_OR_NULL);
        }

        long maxFileSize = 5 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(String.format(FILE_SIZE_EXCEEDS_LIMIT, maxFileSize));
        }

        String contentType = file.getContentType();
        if (contentType == null ||
            !(contentType.equalsIgnoreCase("image/jpeg") ||
                contentType.equalsIgnoreCase("image/png"))) {
            throw new IllegalArgumentException(FILE_UNSUPPORTED_TYPE);
        }
    }
} 

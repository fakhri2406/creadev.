package com.creadev.external.cloudinary;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
     * Validate and upload the given file to Cloudinary using the Cloudinary SDK, returning the secure URL of the uploaded file.
     *
     * @param file the file to upload
     * @return the secure URL of the uploaded file
     */
    String uploadFile(MultipartFile file);

    /**
     * Delete the file with the specified public ID from Cloudinary via the Cloudinary SDK.
     *
     * @param publicId the public ID of the file to delete
     */
    void deleteFile(String publicId);
}

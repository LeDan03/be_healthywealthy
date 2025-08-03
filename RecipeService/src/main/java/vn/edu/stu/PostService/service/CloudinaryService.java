package vn.edu.stu.PostService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            logger.info("Xóa image thất bại: {}", e.getMessage());
            throw new RuntimeException("Xóa ảnh thất bại: " + e.getMessage());
        }
    }
}

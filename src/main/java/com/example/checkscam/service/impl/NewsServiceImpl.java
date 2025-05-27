package com.example.checkscam.service.impl;

import com.example.checkscam.component.FileUtils;
import com.example.checkscam.constant.ErrorCodeEnum;
import com.example.checkscam.dto.request.NewsRequestDto;
import com.example.checkscam.entity.Attachment;
import com.example.checkscam.entity.News;
import com.example.checkscam.exception.CheckScamException;
import com.example.checkscam.exception.FileUploadValidationException;
import com.example.checkscam.exception.InvalidParamException;
import com.example.checkscam.repository.AttachmentRepository;
import com.example.checkscam.repository.NewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl {
    private final NewsRepository newsRepository;
    private final FileUtils fileUtils;
    private final AttachmentRepository attachmentRepository;
    public static final int MAXIMUM_ATTACHMENTS_PER_REPORT = 5;
    //get all news
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    // get news by id
    public Optional<News> getNewsById(Long id) {
        return newsRepository.findById(id);
    }

    //post news
    public News createNews(NewsRequestDto news) {
        News newsEntity = new News();
        newsEntity.setName(news.getName());
        newsEntity.setContent(news.getContent());
        newsEntity.setShortDescription(news.getShortDescription());
        newsEntity.setUrl(news.getUrl() != null ? news.getUrl() : ""); // Set URL with default value
        return newsRepository.save(newsEntity);
    }

    // put news
    public News updateNews(Long id, NewsRequestDto news) {
        Optional<News> existingNews = newsRepository.findById(id);
        if (existingNews.isPresent()) {
            News updatedNews = existingNews.get();
            updatedNews.setName(news.getName());
            updatedNews.setShortDescription(news.getShortDescription());
            updatedNews.setContent(news.getContent());
            updatedNews.setUrl(news.getUrl() != null ? news.getUrl() : ""); // Set URL with default value
            updatedNews.setAttachments(existingNews.get().getAttachments());
            return newsRepository.save(updatedNews); // Lưu bản ghi đã cập nhật
        } else {
            return null;
        }
    }

    // DELETE news
    @Transactional // Đảm bảo toàn bộ thao tác là một giao dịch atomic
    public boolean deleteNews(Long id) throws CheckScamException {
        Optional<News> newsOptional = newsRepository.findById(id);
        if (newsOptional.isPresent()) {
            News newsToDelete = newsOptional.get();

            // 1. Lấy tất cả các attachments liên quan đến tin tức này
            // Sử dụng newsToDelete.getAttachments() nếu bạn đã FetchType.EAGER hoặc đã load chúng.
            // Nếu không, bạn cần một phương thức findByNewsId trong AttachmentRepository.
            List<Attachment> attachments = attachmentRepository.findByNewsId(id);

            // 2. Xóa các file vật lý trên server TRƯỚC khi xóa bản ghi database
            for (Attachment attachment : attachments) {
                try {
                    fileUtils.deleteFile(attachment.getUrl()); // Giả sử getUrl() trả về tên file/đường dẫn cần để xóa
                    log.info("Deleted physical file: {}", attachment.getUrl());
                } catch (IOException e) {
                    log.error("Failed to delete physical file {}: {}", attachment.getUrl(), e.getMessage());
                    // Tùy chọn: bạn có thể chọn throw exception ở đây hoặc chỉ log lỗi
                    // và tiếp tục xóa các bản ghi database.
                }
            }

            // 3. Xóa tất cả các bản ghi attachment khỏi database
            attachmentRepository.deleteAll(attachments);
            log.info("Deleted {} attachments for news ID: {}", attachments.size(), id);


            // 4. Cuối cùng, xóa bản ghi news khỏi database
            newsRepository.delete(newsToDelete);
            log.info("Deleted news with ID: {}", id);
            return true;
        } else {
            // Nếu không tìm thấy tin tức, có thể throw một ngoại lệ hoặc trả về false
            // Tùy thuộc vào yêu cầu nghiệp vụ của bạn.
            throw new CheckScamException(ErrorCodeEnum.NOT_FOUND);
        }
    }

    @Transactional
    public List<Attachment> uploadAndCreateAttachments(Long newsId,
                                                       List<MultipartFile> files) throws Exception {

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CheckScamException(ErrorCodeEnum.NOT_FOUND));

        List<MultipartFile> validFiles =
                (files == null ? List.<MultipartFile>of() : files).stream()
                        .filter(f -> f != null && !f.isEmpty() && f.getSize() > 0)
                        .toList();

        if (validFiles.isEmpty()) {
            return List.of();
        }

        int current = attachmentRepository.countByNewsId(newsId);
        if (current + validFiles.size() > MAXIMUM_ATTACHMENTS_PER_REPORT) {
            throw new InvalidParamException(String.format(
                    "newsId %d đã có %d file, tối đa %d. Không thể thêm %d file.",
                    newsId, current, MAXIMUM_ATTACHMENTS_PER_REPORT, validFiles.size()));
        }

        List<Attachment> saved = new ArrayList<>();

        for (MultipartFile file : validFiles) {
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new FileUploadValidationException(
                        "Kích thước file vượt quá 10MB: " + file.getOriginalFilename(),
                        HttpStatus.PAYLOAD_TOO_LARGE);
            }
            if (file.getContentType() == null
                    || !file.getContentType().startsWith("image/")) {
                throw new FileUploadValidationException(
                        "File phải là hình ảnh: " + file.getOriginalFilename(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }

            String storedName = fileUtils.storeFile(file);

            Attachment toSave = Attachment.builder()
                    .news(news)
                    .url(storedName)
                    .build();

            saved.add(attachmentRepository.save(toSave));
        }

        return saved;
    }

    public Resource loadImage(String imageName) throws IOException {
        validateImageName(imageName);

        Path imagePath = fileUtils.resolve(imageName);

        if (!Files.exists(imagePath)) {
            Path fallback = fileUtils.resolve("notfound.jpeg");
            if (Files.exists(fallback)) {
                return new UrlResource(fallback.toUri());
            }
            throw new FileNotFoundException("Image not found: " + imageName);
        }
        return new UrlResource(imagePath.toUri());
    }

    public String getImageMimeType(String imageName) throws IOException {
        Path path = fileUtils.resolve(imageName);
        if (!Files.exists(path)) {
            path = fileUtils.resolve("notfound.jpeg");
        }
        return Files.probeContentType(path);
    }

    private void validateImageName(String imageName) {
        if (imageName.contains("..") || imageName.contains("/") || imageName.contains("\\")) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }
    }


}

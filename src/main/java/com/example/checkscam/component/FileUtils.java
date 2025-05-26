package com.example.checkscam.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects; // Vẫn giữ Objects nếu có sử dụng ở chỗ nào khác, hoặc có thể xóa nếu không dùng

@Component
@Slf4j
public class FileUtils {

    /**
     * Đường dẫn gốc để lưu file – có thể cấu hình ngoài application.yml
     * Ví dụ:
     * app:
     * upload-dir: /opt/checkscam/uploads
     */
    @Value("${app.upload-dir:uploads}")
    private String uploadDirRaw;

    /** Đảm bảo thư mục tồn tại, trả về path tuyệt đối & chuẩn hoá. */
    private Path getUploadDir() throws IOException {
        Path dir = Paths.get(uploadDirRaw).toAbsolutePath().normalize();
        Files.createDirectories(dir);        // tạo nếu chưa có
        return dir;
    }

    /**
     * Lưu file upload vào thư mục uploads và trả về TÊN file (đã random).
     *
     * @param file MultipartFile nhận từ client
     * @return String tên file đã lưu (VD: 1715063501123_avatar.png)
     * @throws IOException lỗi I/O nếu copy thất bại
     */
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File rỗng hoặc không tồn tại");
        }

        // Lấy tên file gốc & “sanitize”
        String originalName = Path.of(file.getOriginalFilename())
                .getFileName()       // loại bỏ path client
                .toString();

        // Sinh tên mới: <timestamp>_<originalName>
        String newName = System.currentTimeMillis() + "_" + originalName;

        Path target = getUploadDir().resolve(newName);

        // Sao chép luồng dữ liệu
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Đã lưu file [{}] vào {}", newName, target);
        }

        return newName; // chỉ trả tên file, không phải full path
    }

    /**
     * Trả về Path tuyệt đối tới file (nếu cần).
     * @param fileName Tên file đã lưu (vd: 1715063501123_avatar.png)
     * @return Path đối tượng Path của file
     * @throws IOException nếu thư mục upload không thể truy cập
     */
    public Path resolve(String fileName) throws IOException {
        return getUploadDir().resolve(fileName).normalize();
    }

    /**
     * Xóa file vật lý khỏi thư mục uploads.
     *
     * @param fileName Tên file cần xóa (vd: 1715063501123_avatar.png)
     * @throws IOException nếu có lỗi I/O trong quá trình xóa
     */
    public void deleteFile(String fileName) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            log.warn("Attempted to delete a null or empty file name.");
            return; // Không làm gì nếu tên file rỗng
        }

        Path filePath = resolve(fileName); // Lấy đường dẫn tuyệt đối của file

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            try {
                Files.delete(filePath);
                log.info("Đã xóa file vật lý: {}", filePath.getFileName());
            } catch (IOException e) {
                log.error("Lỗi khi xóa file vật lý {}: {}", filePath.getFileName(), e.getMessage());
                throw e; // Ném lại ngoại lệ để phương thức gọi (NewsServiceImpl) có thể xử lý
            }
        } else {
            log.warn("File không tồn tại hoặc không phải là file thông thường, không thể xóa: {}", filePath.getFileName());
            // Có thể chọn throw FileNotFoundException nếu muốn nghiêm ngặt hơn
        }
    }
}
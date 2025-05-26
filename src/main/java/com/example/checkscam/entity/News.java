package com.example.checkscam.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
// Xóa import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String shortDescription;
    @Column(columnDefinition = "TEXT")
    private String content;
    @OneToMany(mappedBy = "news")
    @JsonManagedReference(value = "news-attachments")
    private List<Attachment> attachments;
    @Column(name = "author", length = 100)
    private String author;

    // Xóa @CreatedDate ở đây

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // THÊM PHƯƠNG THỨC NÀY
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
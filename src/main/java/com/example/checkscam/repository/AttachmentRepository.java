package com.example.checkscam.repository;

import com.example.checkscam.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByNewsId(Long newsId);
    int countByNewsId(Long reportId);
}
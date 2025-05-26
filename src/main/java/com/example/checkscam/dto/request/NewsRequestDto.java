package com.example.checkscam.dto.request;

import com.example.checkscam.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequestDto {
    @NotBlank(message = "Tên tin tức không được để trống")
    @Size(max = 255, message = "Tên tin tức không được vượt quá 255 ký tự")
    private String name;
    private String shortDescription;
    @NotBlank(message = "Nội dung tin tức không được để trống")
    private String content;
    private List<Attachment> attachments;

}

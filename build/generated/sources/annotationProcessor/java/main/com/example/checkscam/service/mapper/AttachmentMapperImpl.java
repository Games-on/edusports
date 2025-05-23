package com.example.checkscam.service.mapper;

import com.example.checkscam.dto.AttachmentDto;
import com.example.checkscam.dto.NewsDto;
import com.example.checkscam.entity.Attachment;
import com.example.checkscam.entity.News;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-23T14:27:32+0700",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class AttachmentMapperImpl implements AttachmentMapper {

    @Override
    public AttachmentDto entityToDto(Attachment attachment) {
        if ( attachment == null ) {
            return null;
        }

        AttachmentDto.AttachmentDtoBuilder attachmentDto = AttachmentDto.builder();

        attachmentDto.id( attachment.getId() );
        attachmentDto.url( attachment.getUrl() );
        attachmentDto.news( newsToNewsDto( attachment.getNews() ) );

        return attachmentDto.build();
    }

    protected List<AttachmentDto> attachmentListToAttachmentDtoList(List<Attachment> list) {
        if ( list == null ) {
            return null;
        }

        List<AttachmentDto> list1 = new ArrayList<AttachmentDto>( list.size() );
        for ( Attachment attachment : list ) {
            list1.add( entityToDto( attachment ) );
        }

        return list1;
    }

    protected NewsDto newsToNewsDto(News news) {
        if ( news == null ) {
            return null;
        }

        NewsDto newsDto = new NewsDto();

        newsDto.setId( news.getId() );
        newsDto.setName( news.getName() );
        newsDto.setShortDescription( news.getShortDescription() );
        newsDto.setContent( news.getContent() );
        newsDto.setAttachments( attachmentListToAttachmentDtoList( news.getAttachments() ) );

        return newsDto;
    }
}

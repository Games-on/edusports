package com.example.checkscam.service.mapper;

import com.example.checkscam.dto.request.ReportRequestDto;
import com.example.checkscam.dto.response.ReportResponseDto;
import com.example.checkscam.entity.Report;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T11:13:49+0700",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class ReportMapperImpl implements ReportMapper {

    @Override
    public Report requestToEntity(ReportRequestDto reportRequestDto) {
        if ( reportRequestDto == null ) {
            return null;
        }

        Report report = new Report();

        if ( reportRequestDto.getId() != null ) {
            report.setId( reportRequestDto.getId() );
        }
        report.setInfo( reportRequestDto.getInfo() );
        report.setDescription( reportRequestDto.getDescription() );
        report.setStatus( reportRequestDto.getStatus() );
        report.setType( reportRequestDto.getType() );
        report.setIdScamTypeAfterHandle( reportRequestDto.getIdScamTypeAfterHandle() );
        report.setEmailAuthorReport( reportRequestDto.getEmailAuthorReport() );
        report.setReason( reportRequestDto.getReason() );
        report.setInfoDescription( reportRequestDto.getInfoDescription() );
        report.setDateReport( reportRequestDto.getDateReport() );

        return report;
    }

    @Override
    public void setValue(ReportRequestDto dto, Report entity) {
        if ( dto == null ) {
            return;
        }

        entity.setInfo( dto.getInfo() );
        entity.setDescription( dto.getDescription() );
        entity.setStatus( dto.getStatus() );
        entity.setType( dto.getType() );
        entity.setIdScamTypeAfterHandle( dto.getIdScamTypeAfterHandle() );
        entity.setEmailAuthorReport( dto.getEmailAuthorReport() );
        entity.setReason( dto.getReason() );
        entity.setInfoDescription( dto.getInfoDescription() );
        entity.setDateReport( dto.getDateReport() );
    }

    @Override
    public ReportResponseDto entityToResponse(Report report) {
        if ( report == null ) {
            return null;
        }

        ReportResponseDto reportResponseDto = new ReportResponseDto();

        reportResponseDto.setId( report.getId() );
        reportResponseDto.setInfo( report.getInfo() );
        reportResponseDto.setDescription( report.getDescription() );
        reportResponseDto.setStatus( report.getStatus() );
        reportResponseDto.setType( report.getType() );
        reportResponseDto.setIdScamTypeAfterHandle( report.getIdScamTypeAfterHandle() );
        reportResponseDto.setEmailAuthorReport( report.getEmailAuthorReport() );
        reportResponseDto.setReason( report.getReason() );
        reportResponseDto.setInfoDescription( report.getInfoDescription() );
        reportResponseDto.setDateReport( report.getDateReport() );

        return reportResponseDto;
    }
}

package com.example.checkscam.service.mapper;

import com.example.checkscam.dto.ScamTypesDto;
import com.example.checkscam.entity.ScamTypes;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-08T09:17:29+0700",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class ScamTypesMapperImpl implements ScamTypesMapper {

    @Override
    public ScamTypes requestToEntity(ScamTypesDto request) {
        if ( request == null ) {
            return null;
        }

        ScamTypes scamTypes = new ScamTypes();

        if ( request.getId() != null ) {
            scamTypes.setId( request.getId() );
        }
        scamTypes.setName( request.getName() );
        scamTypes.setCode( request.getCode() );

        return scamTypes;
    }

    @Override
    public void setValue(ScamTypesDto dto, ScamTypes entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setCode( dto.getCode() );
    }

    @Override
    public ScamTypesDto entityToResponse(ScamTypes entity) {
        if ( entity == null ) {
            return null;
        }

        ScamTypesDto scamTypesDto = new ScamTypesDto();

        scamTypesDto.setId( entity.getId() );
        scamTypesDto.setName( entity.getName() );
        scamTypesDto.setCode( entity.getCode() );

        return scamTypesDto;
    }
}

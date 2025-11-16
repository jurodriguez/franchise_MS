package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.FranchiseRequestDto;
import co.com.bancolombia.model.franchise.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FranchiseMapper {

    FranchiseMapper MAPPER = Mappers.getMapper(FranchiseMapper.class);

    Franchise toFranchise(FranchiseRequestDto franchiseRequestDto);
}

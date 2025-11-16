package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.BranchRequestDto;
import co.com.bancolombia.model.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BranchMapper {

    BranchMapper MAPPER = Mappers.getMapper(BranchMapper.class);

    Branch toBranch(BranchRequestDto branchRequestDto);
}

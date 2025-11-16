package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.ProductRequestDto;
import co.com.bancolombia.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    Product toProduct(ProductRequestDto productRequestDto);
}

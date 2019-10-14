package cz.jares.pavel.config.orica;

import java.util.Map;

import cz.jares.pavel.dto.PageableDto;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

/**
 * 
 *  Convertor of HTTP parameter stored in map into PageableDto
 * 
 * @author jaresp
 *
 */
public class MapPageableDtoConverter extends CustomConverter<Map<String, String>, PageableDto> {

	@Override
	public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
		return sourceType.isMap() && destinationType.isAssignableFrom(PageableDto.class);
	}
	
	@Override
	public PageableDto convert(Map<String, String> source, Type<? extends PageableDto> destinationType, MappingContext mappingContext) {
		PageableDto out=new PageableDto();
		
		final String page=source.get("page");
		out.setPageNumber(page==null?0:Integer.parseInt(page));
		
		final String size=source.get("size");
		out.setPageSize(size==null?10:Integer.parseInt(size));
		
		return out;
	}

}

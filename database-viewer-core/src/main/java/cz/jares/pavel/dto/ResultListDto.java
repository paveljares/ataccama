package cz.jares.pavel.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Value;

/**
 * 
 *  DTO for wrapping result from database. This DTO contains two values: pageable, items.
 *  
 *  pageable - contains information about current page + size (count of all records)
 *  items - list of map, with all values of fetched page (key=column name, value=value from database)
 * 
 * @author jaresp
 *
 */
@Value
public class ResultListDto implements Serializable {

	private static final long serialVersionUID = -8242552590624974891L;

	private PageableDto pageable;
	private List<Map<String, Object>> items;
	
}

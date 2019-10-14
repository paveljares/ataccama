package cz.jares.pavel.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author jaresp
 *
 */
@Data
@NoArgsConstructor
public class TableStatistics {

	@JsonProperty("column")
	private Map<String, ColumnStatistic<Object>> columnStatistics;
	
}

package cz.jares.pavel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author jaresp
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnStatistic<T> {

	private T min;
	private T avg;
	private T max;
	private T median;
	
}

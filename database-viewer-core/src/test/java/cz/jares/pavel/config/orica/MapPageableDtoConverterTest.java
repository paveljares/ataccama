package cz.jares.pavel.config.orica;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import cz.jares.pavel.dto.PageableDto;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * 
 * @author jaresp
 *
 */
public class MapPageableDtoConverterTest {

	private MapPageableDtoConverter converter=new MapPageableDtoConverter();
	
	@Test
	public void testConstructors() {
		PageableDto p;
		
		p=new PageableDto();
		assertEquals(0, p.getPageNumber());
		assertEquals(0, p.getPageSize());
		assertNull(p.getSize());
		assertNull(p.getSort());
		
		p=new PageableDto(3, 7);
		assertEquals(7, p.getPageNumber());
		assertEquals(3, p.getPageSize());
		assertNull(p.getSize());
		assertNull(p.getSort());
	}
	
	@Test
	public void testCondition() {
		assertTrue(converter.canConvert(TypeFactory.valueOf(Map.class), TypeFactory.valueOf(PageableDto.class)));
		assertTrue(converter.canConvert(TypeFactory.valueOf(HashMap.class), TypeFactory.valueOf(PageableDto.class)));
		assertTrue(converter.canConvert(TypeFactory.valueOf(Map.class), TypeFactory.valueOf(Object.class)));
		
		assertFalse(converter.canConvert(TypeFactory.valueOf(Collection.class), TypeFactory.valueOf(PageableDto.class)));
		assertFalse(converter.canConvert(TypeFactory.valueOf(Object.class), TypeFactory.valueOf(Object.class)));		
		assertFalse(converter.canConvert(TypeFactory.valueOf(Set.class), TypeFactory.valueOf(PageableDto.class)));
		assertFalse(converter.canConvert(TypeFactory.valueOf(Map.class), TypeFactory.valueOf(Exception.class)));
		assertFalse(converter.canConvert(TypeFactory.valueOf(Set.class), TypeFactory.valueOf(Exception.class)));
	}
	
	@Test
	public void baseMapping() {
		PageableDto p;
		
		Map<String, String> map=new HashMap<>();
		
		p=converter.convert(map, TypeFactory.valueOf(PageableDto.class), mock(MappingContext.class));
		assertEquals(0, p.getPageNumber());
		assertEquals(10, p.getPageSize());
		assertNull(p.getSize());
		assertNull(p.getSort());
		
		map.put("page", "5");
		p=converter.convert(map, TypeFactory.valueOf(PageableDto.class), mock(MappingContext.class));
		assertEquals(5, p.getPageNumber());
		assertEquals(10, p.getPageSize());
		assertNull(p.getSize());
		assertNull(p.getSort());
		
		map.put("size", "11");
		p=converter.convert(map, TypeFactory.valueOf(PageableDto.class), mock(MappingContext.class));
		assertEquals(5, p.getPageNumber());
		assertEquals(11, p.getPageSize());
		assertNull(p.getSize());
		assertNull(p.getSort());
	}
		
	@Test
	public void testNegativeOffset() {
		assertEquals(0, new PageableDto(0, 0).getOffset());
		assertEquals(0, new PageableDto(-1, 0).getOffset());
		assertEquals(0, new PageableDto(0, -2).getOffset());
		assertEquals(0, new PageableDto(-3, -4).getOffset());
		assertEquals(0, new PageableDto(1, 0).getOffset());
		assertEquals(0, new PageableDto(0, 1).getOffset());
		assertEquals(6, new PageableDto(2, 3).getOffset());
		assertEquals(56, new PageableDto(8, 7).getOffset());
	}
	
	private void assertEqualsAndHash(PageableDto pi1, PageableDto pi2, boolean eq) {
		assertEquals(eq, pi1.equals(pi2));
		if (eq) {
			assertEquals(eq, pi1.hashCode()==pi2.hashCode());
		}
	}
	
	@Test
	public void testEquals() {
		assertEqualsAndHash(new PageableDto(2, 3), new PageableDto(2, 3), true);
		assertEqualsAndHash(new PageableDto(2, 3), new PageableDto(2, 1), false);
		assertEqualsAndHash(new PageableDto(1, 1), new PageableDto(2, 1), false);
		assertEqualsAndHash(new PageableDto(1, 1), new PageableDto(2, 3), false);
	}
	
	@Test
	public void testFirst() {
		assertEquals(new PageableDto(10, 0), new PageableDto(10, 5).first());
		assertEquals(new PageableDto(15, 0), new PageableDto(15, 3).first());
	}
	
	@Test
	public void testNext() {
		assertEquals(new PageableDto(10, 6), new PageableDto(10, 5).next());
		assertEquals(new PageableDto(15, 4), new PageableDto(15, 3).next());
	}
	
	@Test
	public void testPreviousOrFirst() {
		assertEquals(new PageableDto(10, 1), new PageableDto(10, 2).previousOrFirst());
		assertEquals(new PageableDto(10, 0), new PageableDto(10, 1).previousOrFirst());
		assertEquals(new PageableDto(15, 0), new PageableDto(15, 0).previousOrFirst());
		assertEquals(new PageableDto(15, 0), new PageableDto(15, -1).previousOrFirst());
	}

	@Test
	public void testHasPrevious() {
		assertTrue(new PageableDto(10, 2).hasPrevious());
		assertTrue(new PageableDto(5, 10).hasPrevious());
		assertTrue(new PageableDto(5, 1).hasPrevious());
		assertFalse(new PageableDto(5, 0).hasPrevious());
		assertFalse(new PageableDto(5, -5).hasPrevious());
	}
	
}

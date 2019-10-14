package cz.jares.pavel.dto;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author jaresp
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class PageableDto implements Pageable, Serializable {
	
	private static final long serialVersionUID = 5221505128471528719L;
	
	private int pageNumber;
	private int pageSize;
	private Long size;
	private Sort sort;
	
	public PageableDto() {
		super();
	}
	
	public PageableDto(int pageSize) {
		this();
		this.pageSize=pageSize;
	}
	
	public PageableDto(int pageSize, int pageNumber) {
		this(pageSize);
		this.pageNumber=pageNumber;
	}
	
	@Override
	public int getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	@JsonIgnore
	public long getOffset() {
		if (pageNumber<0) return 0;
		if (pageSize<0) return 0;
		return pageNumber * pageSize;
	}

	@Override
	@JsonIgnore
	public Sort getSort() {
		return sort;
	}
	
	public void setSort(Sort sort) {
		this.sort = sort;
	}

	@Override
	@JsonIgnore
	public Pageable next() {
		final PageableDto out=new PageableDto(pageSize, pageNumber+1);
		out.setSort(sort);
		return out;
	}

	@Override
	@JsonIgnore
	public Pageable previousOrFirst() {
		final PageableDto out=new PageableDto(pageSize, Math.max(pageNumber-1, 0));
		out.setSort(sort);
		return out;
	}

	@Override
	@JsonIgnore
	public Pageable first() {
		final PageableDto out=new PageableDto(pageSize, 0);
		out.setSort(sort);
		return out;
	}

	@Override
	@JsonIgnore
	public boolean hasPrevious() {
		return pageNumber>0;
	}
	
}

package com.rejs.reservation.global.dto.response.metadata;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

@SuperBuilder
@Getter
public class PageMetadata extends SliceMetadata{
    private Integer totalPage;
    private Long totalElements;

    public static <T> PageMetadata of(Page<T> page){
        return PageMetadata.builder()
                .count(page.getNumberOfElements())
                .requestNumber(page.getNumber()+1)
                .requestSize(page.getSize())
                .hasNextPage(page.hasNext())
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

}

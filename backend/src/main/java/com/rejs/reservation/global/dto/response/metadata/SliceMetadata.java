package com.rejs.reservation.global.dto.response.metadata;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Slice;

@SuperBuilder
@Getter
public class SliceMetadata extends CollectionMetadata{
    private Integer requestNumber;
    private Integer requestSize;
    private Boolean hasNextPage;

    public static <T> SliceMetadata of(Slice<T> slice){
        return SliceMetadata.builder()
                .count(slice.getNumberOfElements())
                .requestNumber(slice.getNumber()+1)
                .requestSize(slice.getSize())
                .hasNextPage(slice.hasNext())
                .build();
    }

}

package com.rejs.reservation.global.dto.response.metadata;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class CollectionMetadata {
    private Integer count;

    public static <T> CollectionMetadata of(List<T> list){
        return CollectionMetadata.builder()
                .count(list.size())
                .build();
    }
}

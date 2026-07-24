package com.main.MerchantMart.utility.function;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utility {
    public static <E,D> List<D> mapListToDto(List<E> entityList, Function<E,D> mapper) {
        if(entityList == null || entityList.isEmpty()){
            return  null;
        }

        return  entityList.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

}

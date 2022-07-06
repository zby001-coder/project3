package com.example.mypractice.filter.basic;

import com.example.mypractice.commons.exception.FilterException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CollectionNotEmptyFilter implements MyFilter {
    @Override
    public void doFilter(Object o, String name, boolean nullable) throws FilterException {
        if (nullable && o == null) {
            return;
        }
        if (o instanceof Collection) {
            Collection collection = (Collection) o;
            if (collection.size() == 0) {
                throw new FilterException(name);
            }
        } else {
            throw new FilterException(name);
        }
    }
}

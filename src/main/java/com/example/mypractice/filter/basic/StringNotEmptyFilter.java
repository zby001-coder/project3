package com.example.mypractice.filter.basic;

import com.example.mypractice.commons.exception.FilterException;
import org.springframework.stereotype.Component;

@Component
public class StringNotEmptyFilter implements MyFilter {
    @Override
    public void doFilter(Object s, String name, boolean nullable) throws FilterException {
        if (nullable && s == null) {
            return;
        }
        if (s instanceof String) {
            String param = (String) s;
            if (param.length() == 0) {
                throw new FilterException(name);
            }
        } else {
            throw new FilterException(name);
        }
    }
}

package com.example.mypractice.filter.basic;

import com.example.mypractice.commons.exception.FilterException;

public interface MyFilter {
    void doFilter(Object o,String name,boolean nullable) throws FilterException;
}

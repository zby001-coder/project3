package com.example.mypractice.filter.model;

import com.example.mypractice.commons.exception.FilterException;
import org.springframework.stereotype.Component;

@Component
public class PagingFilter {
    public PagingFilter filterPage(Integer page) throws FilterException {
        if (page == null) {
            throw new FilterException("PAGE");
        }
        if (page >= 0) {
            return this;
        }
        throw new FilterException("PAGE");
    }

    public PagingFilter filterStart(Integer start) throws FilterException {
        if (start == null) {
            throw new FilterException("START");
        }
        if (start >= 0) {
            return this;
        }
        throw new FilterException("START");
    }

    public PagingFilter filterLastIndex(Long lastIndex,boolean nullable) throws FilterException {
        if (lastIndex == null) {
            if (nullable){
                return this;
            }
            throw new FilterException("LAST_INDEX");
        }
        if (lastIndex >= -1) {
            return this;
        }
        throw new FilterException("LAST_INDEX");
    }

    public PagingFilter filterLastScore(Double lastScore,boolean nullable) throws FilterException {
        if (lastScore == null){
            if (nullable){
                return this;
            }
            throw new FilterException("LAST_SCORE");
        }
        if (lastScore>=0){
            return this;
        }
        throw new FilterException("LAST_SCORE");
    }
}

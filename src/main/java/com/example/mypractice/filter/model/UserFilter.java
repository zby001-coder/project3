package com.example.mypractice.filter.model;

import com.example.mypractice.commons.exception.FilterException;
import org.springframework.stereotype.Component;

@Component
public class UserFilter {
    private static final Long ID_MIN = 0L;
    private static final Integer NAME_MAX_SIZE = 32;
    private static final Integer[] IDENTITY_RANGE = new Integer[]{0, 1};
    private static final Integer PASS_MAX_SIZE = 32;

    public UserFilter filterId(Long id, boolean nullable) throws FilterException {
        if (id == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("ID");
        }
        if (id >= ID_MIN) {
            return this;
        }
        throw new FilterException("ID");
    }

    public UserFilter filterName(String name, boolean nullable) throws FilterException {
        if (name == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("NAME");
        }
        if (name.length() <= NAME_MAX_SIZE) {
            return this;
        }
        throw new FilterException("NAME");
    }

    public UserFilter filterIdentity(Integer identity, boolean nullable) throws FilterException {
        if (identity == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("IDENTITY");
        }
        for (int possible : IDENTITY_RANGE) {
            if (identity.equals(possible)) {
                return this;
            }
        }
        throw new FilterException("IDENTITY");
    }

    public UserFilter filterPass(String pass, boolean nullable) throws FilterException {
        if (pass == null) {
            if (nullable) {
                return this;
            }
            throw new FilterException("PASS");
        }
        if (pass.length() <= PASS_MAX_SIZE) {
            return this;
        }
        throw new FilterException("PASS");
    }
}

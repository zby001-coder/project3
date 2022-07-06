package com.example.mypractice.model.web;

import java.util.List;

/**
 * 接收id数组的对象
 * @author 张贝易
 */
public class IdList {
    private List<Long> id;

    public IdList() {
    }

    public IdList(List<Long> id) {
        this.id = id;
    }

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }
}

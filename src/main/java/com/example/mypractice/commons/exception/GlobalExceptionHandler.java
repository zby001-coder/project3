package com.example.mypractice.commons.exception;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.example.mypractice.commons.constant.Bodies;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * 全局异常处理
 *
 * @author 张贝易
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FilterException.class)
    public HashMap<String, Object> handleFilterException(FilterException filterException) {
        HashMap<String, Object> result = new HashMap<>(1);
        result.put(Bodies.MESSAGE, "参数检测失败,请检测传递的参数格式: " + filterException.getMessage());
        return result;
    }

    @ExceptionHandler({ServiceFailException.class, ElasticsearchException.class})
    public HashMap<String, Object> handleServiceException(ServiceFailException serviceFailException) {
        HashMap<String, Object> result = new HashMap<>(1);
        result.put(Bodies.MESSAGE, "服务失败,请检测调用逻辑、唯一性、内容是否存在、数据是否匹配: " + serviceFailException.getMessage());
        return result;
    }

    @ExceptionHandler(AccessException.class)
    public HashMap<String, Object> handleAccessException(AccessException accessException) {
        HashMap<String, Object> result = new HashMap<>(1);
        result.put(Bodies.MESSAGE, "越权操作!");
        return result;
    }

    @ExceptionHandler(Exception.class)
    public HashMap<String, Object> handleOtherException(Exception e) {
        HashMap<String, Object> result = new HashMap<>(1);
        e.printStackTrace();
        result.put(Bodies.MESSAGE, "未知错误，请提示查看日志!");
        return result;
    }
}

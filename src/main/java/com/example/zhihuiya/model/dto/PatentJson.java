package com.example.zhihuiya.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author li
 * P002对应api请求体数据
 */
@Data
public class PatentJson implements Serializable {

    private List<Sort> sort;
    private Integer limit;
    private Integer offset;
    private String query_text;
    private String collapse_by;
    private String collapse_type;
    private String collapse_order;


    @Data
    public static class Sort {
        private String field;
        private String order;
    }

    private static final long serialVersionUID = 1L;
}

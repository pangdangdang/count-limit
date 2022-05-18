package com.util;


import lombok.Data;

/**
 * @description: 计算限流
 * @author: tingmailang
 */
@Data
public class CountLimitDTO {

    private String Key;
    private Integer count;
    private Integer limit;
    private Boolean isAdd;
}

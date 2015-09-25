package com.wwh.test;

import java.util.Date;

import com.alibaba.fastjson.JSON;

public class TT {

    public static void main(String[] args) {
        String ss = "{\"state\":false,\"error\":\"\u9a8c\u8bc1\u7801\u4e0d\u6b63\u786e\"}";

        Object o = JSON.parse(ss);
        System.out.println(o);
    }

    @SuppressWarnings("deprecation")
    public static void main1(String[] args) {
        Date d = new Date(1443025656052l);

        Date d1 = new Date(1443025844974l);
        System.out.println(d.toLocaleString());
        System.out.println(d1.toLocaleString());
    }
}

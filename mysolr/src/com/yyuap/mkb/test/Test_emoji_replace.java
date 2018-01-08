package com.yyuap.mkb.test;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.yyuap.mkb.log.MKBLogger;

public class Test_emoji_replace {

    public static void test() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("你说的一定是真的/::*");
        list.add("你内心住着一个小孩/::>");
        list.add("你讨厌约束，喜欢自由自在[嘿哈]");
        list.add("你聊天真走心~[机智]");
        list.add("小友在想弦外之音/:?");
        list.add("没理解清楚，再讲讲嘛/:8*");
        list.add("听聪明人说话是一种享受~/:,@-D");
        list.add("你说得绘声绘色的呢~/:handclap");
        list.add("你辣么会说话，肯定不是一般银~/:,@P");
        list.add("和你聊天会被你迷住哒~/::B");

        list.add("小友在想你是不是话里有话/:dig");
        list.add("你很喜欢帮助别人，又很主动大方/::$");
        list.add("居然会这样/::O");

        for (String str : list) {
            String newStr = filterEmoji(str, "");
            MKBLogger.info(newStr);
        }

    }

    /**
     * emoji表情替换
     *
     * @param source
     *            原字符串
     * @param slipStr
     *            emoji表情替换成的字符串
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source, String slipStr) {
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        } else {
            return source;
        }
    }
}

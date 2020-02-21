package com.spike.redis;

public class GoodsKey extends  BasePrefix{


    public GoodsKey(int expireSeconds, String prefix) {
        super(60,prefix);
    }
    public static GoodsKey getGoodsList= new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail= new GoodsKey(60,"gd");
}

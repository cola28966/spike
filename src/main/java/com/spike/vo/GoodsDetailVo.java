package com.spike.vo;

import com.spike.model.MiaoshaUser;

public class GoodsDetailVo {

    private GoodsVo goods;
    private MiaoshaUser user;
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goodsVo) {
        this.goods = goodsVo;
    }

    public MiaoshaUser getUser() {
        return user;
    }

    public void setUser(MiaoshaUser user) {
        this.user = user;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    @Override
    public String toString() {
        return "GoodsDetailVo{" +
                "goodsVo=" + goods +
                ", user=" + user +
                ", miaoshaStatus=" + miaoshaStatus +
                ", remainSeconds=" + remainSeconds +
                '}';
    }
}

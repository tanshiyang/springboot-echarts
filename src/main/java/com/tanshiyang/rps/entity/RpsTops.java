package com.tanshiyang.rps.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author tanshiyang
 * @since 2021-10-12
 */
@TableName("rps_tops")
public class RpsTops implements Serializable {

    private static final long serialVersionUID = 1L;

      private String topType;

      private String tradeDate;

      private String tsCode;

    private Float extrs;


    public String getTopType() {
        return topType;
    }

    public void setTopType(String topType) {
        this.topType = topType;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTsCode() {
        return tsCode;
    }

    public void setTsCode(String tsCode) {
        this.tsCode = tsCode;
    }

    public Float getExtrs() {
        return extrs;
    }

    public void setExtrs(Float extrs) {
        this.extrs = extrs;
    }

    @Override
    public String toString() {
        return "RpsTops{" +
        "topType=" + topType +
        ", tradeDate=" + tradeDate +
        ", tsCode=" + tsCode +
        ", extrs=" + extrs +
        "}";
    }
}

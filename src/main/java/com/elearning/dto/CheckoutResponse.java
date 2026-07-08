package com.elearning.dto;

import java.math.BigDecimal;

public class CheckoutResponse {

    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String couponCode;
    private int courseCount;

    public CheckoutResponse() {
    }

    public CheckoutResponse(BigDecimal subTotal, BigDecimal discountAmount,
                            BigDecimal finalAmount, String couponCode, int courseCount) {
        this.subTotal = subTotal;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.couponCode = couponCode;
        this.courseCount = courseCount;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }
}

package com.elearning.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "discount_rate", nullable = false, precision = 4, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "max_discount", nullable = false, precision = 12, scale = 0)
    private BigDecimal maxDiscount;

    @Column(name = "min_courses", nullable = false)
    private Integer minCourses;

    @Column(nullable = false)
    private Boolean active;

    public Coupon() {
    }

    public Coupon(Long id, String code, BigDecimal discountRate,
                  BigDecimal maxDiscount, Integer minCourses, Boolean active) {
        this.id = id;
        this.code = code;
        this.discountRate = discountRate;
        this.maxDiscount = maxDiscount;
        this.minCourses = minCourses;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(BigDecimal maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Integer getMinCourses() {
        return minCourses;
    }

    public void setMinCourses(Integer minCourses) {
        this.minCourses = minCourses;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

package com.elearning.services;

import com.elearning.exceptions.CouponNotApplicableException;
import com.elearning.models.Coupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CouponServiceTest {

    private CouponService couponService;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        couponService = new CouponService();
        // Mã: giảm 20%, trần 500.000đ, tối thiểu 2 khóa học
        coupon = new Coupon(1L, "ELEARN20",
                new BigDecimal("0.20"), new BigDecimal("500000"), 2, true);
    }

    @Test
    @DisplayName("Happy path: 2 khóa, giảm 20% chưa chạm trần")
    void calculateDiscount_happyPath_returns20Percent() {
        // Arrange
        BigDecimal subTotal = new BigDecimal("2000000");
        // Act
        BigDecimal discount = couponService.calculateDiscount(coupon, subTotal, 2);
        // Assert
        assertEquals(0, new BigDecimal("400000").compareTo(discount));
    }

    @Test
    @DisplayName("Edge case: giảm 20% vượt trần → chặn ở 500.000đ")
    void calculateDiscount_exceedsCap_returnsMaxDiscount() {
        BigDecimal subTotal = new BigDecimal("3700000"); // 20% = 740.000
        BigDecimal discount = couponService.calculateDiscount(coupon, subTotal, 3);
        assertEquals(0, new BigDecimal("500000").compareTo(discount));
    }

    @Test
    @DisplayName("Edge case: đúng đúng ngưỡng trần 500.000đ")
    void calculateDiscount_exactlyAtCap_returnsCap() {
        BigDecimal subTotal = new BigDecimal("2500000"); // 20% = 500.000
        BigDecimal discount = couponService.calculateDiscount(coupon, subTotal, 2);
        assertEquals(0, new BigDecimal("500000").compareTo(discount));
    }

    @Test
    @DisplayName("Error case: mua 1 khóa → ném CouponNotApplicableException")
    void calculateDiscount_belowMinCourses_throwsException() {
        BigDecimal subTotal = new BigDecimal("800000");
        CouponNotApplicableException ex = assertThrows(
                CouponNotApplicableException.class,
                () -> couponService.calculateDiscount(coupon, subTotal, 1));
        assertTrue(ex.getMessage().contains("2 khóa học"));
        assertEquals("COUPON_NOT_APPLICABLE", ex.getErrorCode());
    }

    @ParameterizedTest
    @DisplayName("Nhiều kịch bản: subTotal, courseCount → discount kỳ vọng")
    @CsvSource({
            "1000000, 2, 200000",
            "2500000, 2, 500000",
            "5000000, 4, 500000",
            "1500000, 3, 300000"
    })
    void calculateDiscount_variousScenarios(String subTotal, int count, String expected) {
        BigDecimal discount = couponService.calculateDiscount(
                coupon, new BigDecimal(subTotal), count);
        assertEquals(0, new BigDecimal(expected).compareTo(discount));
    }
}

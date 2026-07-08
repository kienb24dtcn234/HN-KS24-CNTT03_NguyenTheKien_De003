package com.elearning.services;

import com.elearning.exceptions.CouponNotApplicableException;
import com.elearning.models.Coupon;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CouponService {

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal subTotal, int courseCount) {
        if (courseCount < coupon.getMinCourses()) {
            throw new CouponNotApplicableException(
                    "Mã giảm giá chỉ áp dụng khi mua từ " + coupon.getMinCourses()
                            + " khóa học trở lên.");
        }

        BigDecimal rawDiscount = subTotal
                .multiply(coupon.getDiscountRate())
                .setScale(0, RoundingMode.HALF_UP);

        return rawDiscount.min(coupon.getMaxDiscount());
    }
}

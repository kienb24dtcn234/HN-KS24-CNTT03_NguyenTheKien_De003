package com.elearning.exceptions;

import org.springframework.http.HttpStatus;

public class CouponNotApplicableException extends BusinessException {

    public CouponNotApplicableException(String message) {
        super("COUPON_NOT_APPLICABLE", message, HttpStatus.BAD_REQUEST);
    }
}

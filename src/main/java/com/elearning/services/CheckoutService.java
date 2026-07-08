package com.elearning.services;

import com.elearning.dto.CheckoutRequest;
import com.elearning.dto.CheckoutResponse;
import com.elearning.exceptions.InvalidCouponException;
import com.elearning.exceptions.ResourceNotFoundException;
import com.elearning.models.*;
import com.elearning.repositories.CouponRepository;
import com.elearning.repositories.CourseRepository;
import com.elearning.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutService {

    private final CourseRepository courseRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final CouponService couponService;

    public CheckoutService(CourseRepository courseRepository,
                           CouponRepository couponRepository,
                           OrderRepository orderRepository,
                           CouponService couponService) {
        this.courseRepository = courseRepository;
        this.couponRepository = couponRepository;
        this.orderRepository = orderRepository;
        this.couponService = couponService;
    }

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        List<Course> courses = courseRepository.findAllById(request.getCourseIds());
        if (courses.size() != request.getCourseIds().size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều khóa học không tồn tại.");
        }

        int courseCount = courses.size();
        BigDecimal subTotal = courses.stream()
                .map(Course::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCode = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository
                    .findByCodeAndActiveTrue(request.getCouponCode())
                    .orElseThrow(() -> new InvalidCouponException(
                            "Mã giảm giá không tồn tại hoặc đã hết hiệu lực."));
            discountAmount = couponService.calculateDiscount(coupon, subTotal, courseCount);
            appliedCode = coupon.getCode();
        }

        BigDecimal finalAmount = subTotal.subtract(discountAmount);

        Order order = new Order();
        order.setStudentId(request.getStudentId());
        order.setSubTotal(subTotal);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        order.setCouponCode(appliedCode);
        order.setStatus("PAID");
        for (Course course : courses) {
            order.getItems().add(new OrderItem(order, course, course.getPrice()));
        }
        orderRepository.save(order);

        return new CheckoutResponse(subTotal, discountAmount, finalAmount, appliedCode, courseCount);
    }
}

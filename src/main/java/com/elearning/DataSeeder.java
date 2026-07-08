package com.elearning;

import com.elearning.models.Coupon;
import com.elearning.models.Course;
import com.elearning.models.Student;
import com.elearning.repositories.CouponRepository;
import com.elearning.repositories.CourseRepository;
import com.elearning.repositories.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(StudentRepository studentRepository,
                           CourseRepository courseRepository,
                           CouponRepository couponRepository) {
        return args -> {
            studentRepository.save(new Student(null, "Nguyen The Kien", "kien@ptit.edu.vn", "0900000000"));

            courseRepository.save(new Course(null, "Java Core", new BigDecimal("800000")));
            courseRepository.save(new Course(null, "Spring Boot", new BigDecimal("1200000")));
            courseRepository.save(new Course(null, "ReactJS", new BigDecimal("1000000")));
            courseRepository.save(new Course(null, "Docker & K8s", new BigDecimal("1500000")));

            couponRepository.save(new Coupon(
                    null,
                    "ELEARN20",
                    new BigDecimal("0.20"),
                    new BigDecimal("500000"),
                    2,
                    true));
        };
    }
}

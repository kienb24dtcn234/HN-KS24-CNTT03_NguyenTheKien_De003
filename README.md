# Zip_De003 — E-learning: Giỏ hàng & Mã giảm giá

**Sinh viên:** Nguyễn Thế Kiên — PTIT232
**Stack:** Spring Boot 3.2.5 · Gradle · Java 17 · H2 (in-memory)
**Package gốc:** `com.elearning`

Dự án hoàn chỉnh — copy nguyên thư mục vào IDE (IntelliJ / Antigravity) và chạy `main` là dùng được ngay, không cần cài database.

---

## 1. CẤU TRÚC DỰ ÁN

```
de003/
├── build.gradle
├── settings.gradle
├── srs.md                    ← Đặc tả SRS (Nhiệm vụ 01)
├── prompt_history.md         ← Lịch sử prompt AI (nộp kèm)
├── HUONG_DAN_NHIEMVU02.md    ← Luồng nghiệp vụ + kịch bản test
└── src
    ├── main/java/com/elearning
    │   ├── ElearningBaseApplication.java   ← @SpringBootApplication
    │   ├── DataSeeder.java                 ← seed dữ liệu mẫu
    │   ├── models/        Student, Course, Coupon, Cart, CartItem, Order, OrderItem
    │   ├── repositories/  Student/Course/Coupon/Order Repository
    │   ├── dto/           CheckoutRequest, CheckoutResponse
    │   ├── services/      CouponService (thuật toán), CheckoutService
    │   ├── controllers/   CheckoutController  (POST /api/checkout)
    │   ├── exceptions/    BusinessException, CouponNotApplicable, InvalidCoupon, ResourceNotFound
    │   └── advice/        GlobalExceptionHandler (@RestControllerAdvice)
    ├── main/resources
    │   └── application.yml
    └── test/java/com/elearning/services
        └── CouponServiceTest.java          ← JUnit 5 (Happy/Edge/Error)
```

---

## 2. NGHIỆP VỤ MÃ GIẢM GIÁ (Đề 003)

| Điều kiện | Giá trị |
| --- | --- |
| Tỷ lệ giảm | **20%** tổng đơn |
| Mức chặn trần | Tối đa **500.000đ** |
| Số khóa tối thiểu | **≥ 2 khóa học** |
| Vi phạm | Ném `BusinessException` → HTTP 400 kèm `errorCode` |

Công thức: `discount = min(subTotal × 0.20, 500000)` (làm tròn HALF_UP)

---

## 3. CHẠY DỰ ÁN

```bash
./gradlew bootRun
```
Hoặc: mở IntelliJ → Run `ElearningBaseApplication`.

Ứng dụng chạy tại `http://localhost:8080`.
H2 console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:elearning`).

Dữ liệu mẫu tự nạp: 4 khóa học + mã `ELEARN20`.

---

## 4. API TÍNH TIỀN

**`POST /api/checkout`**

Request:
```json
{ "studentId": 1, "courseIds": [1, 2], "couponCode": "ELEARN20" }
```

Response (200):
```json
{
  "subTotal": 2000000,
  "discountAmount": 400000,
  "finalAmount": 1600000,
  "couponCode": "ELEARN20",
  "courseCount": 2
}
```

Response lỗi (400 — mua 1 khóa):
```json
{
  "status": 400,
  "error": "Bad Request",
  "errorCode": "COUPON_NOT_APPLICABLE",
  "message": "Mã giảm giá chỉ áp dụng khi mua từ 2 khóa học trở lên."
}
```

Response lỗi (400 — mã sai / hết hạn):
```json
{
  "errorCode": "INVALID_COUPON",
  "message": "Mã giảm giá không tồn tại hoặc đã hết hiệu lực."
}
```

---

## 5. KIỂM THỬ

```bash
./gradlew test
```
`CouponServiceTest` gồm: Happy path (20%), Edge case (chạm trần 500K, đúng ngưỡng),
Error case (dưới 2 khóa ném exception), và `@ParameterizedTest` nhiều kịch bản.

---

## 6. LUỒNG NGHIỆP VỤ

```
Học viên chọn khóa → POST /api/checkout
   → CheckoutService: Σ giá = subTotal, đếm courseCount
   → Có couponCode? → tra Coupon (active)
        → CouponService.calculateDiscount()
            ├─ courseCount < 2 → CouponNotApplicableException (400)
            └─ discount = min(subTotal×20%, 500000)
   → finalAmount = subTotal − discount
   → Lưu Order + OrderItem (PAID)
   → Trả JSON: subTotal / discountAmount / finalAmount
```

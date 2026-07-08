# NHIỆM VỤ 02 — Lập trình tính năng Giỏ hàng & Mã giảm giá

**Dự án:** elearing_base (E-learning) | **Nguyễn Thế Kiên — PTIT232**
Package: `com.elearning` | Spring Boot 3.x + Gradle + Java 17

---

## 1. CÁC FILE ĐÃ LẬP TRÌNH

### Yêu cầu 1 — Entity + Repository + DTO (chuẩn JPA)
| Tầng | File | Vai trò |
| --- | --- | --- |
| Entity | `models/Course.java` | Khóa học (có sẵn) |
| Entity | `models/Coupon.java` | Mã giảm giá + điều kiện |
| Entity | `models/Cart.java` | Giỏ hàng |
| Entity | `models/CartItem.java` | Chi tiết giỏ (unique cart+course) |
| Entity | `models/Order.java` | Đơn hàng (lưu trữ) |
| Entity | `models/OrderItem.java` | Chi tiết đơn hàng |
| Repository | `repositories/CourseRepository.java` | JpaRepository |
| Repository | `repositories/CouponRepository.java` | `findByCodeAndActiveTrue` |
| Repository | `repositories/OrderRepository.java` | JpaRepository |
| DTO | `dto/CheckoutRequest.java` | Nhận studentId + courseIds + couponCode |
| DTO | `dto/CheckoutResponse.java` | Trả subTotal / discountAmount / finalAmount |

### Yêu cầu 2 — Service chuẩn thuật toán
- `services/CouponService.java` — thuật toán tính mã giảm:
  ```
  discount = min(subTotal × 20%, 500000)   (làm tròn HALF_UP)
  courseCount < 2  →  ném CouponNotApplicableException
  ```
- `services/CheckoutService.java` — điều phối: cộng tổng tiền → áp mã → lưu Order.

### Yêu cầu 3 — Controller cung cấp API tính tiền
- `controllers/CheckoutController.java` → `POST /api/checkout`
- JSON trả về: **tổng tiền ban đầu, số tiền được giảm, số tiền cuối phải trả**.

### Xử lý lỗi tập trung
- `exceptions/CouponNotApplicableException.java`, `exceptions/ResourceNotFoundException.java`
- `advice/GlobalExceptionHandler.java` (`@RestControllerAdvice`)

### Yêu cầu 4 — Chạy dự án
- `DataSeeder.java` — seed 4 khóa học + mã `ELEARN20`.
- `resources/application.yml` — H2 in-memory (chạy ngay, không cần cài DB).

---

## 2. ĐIỀU KIỆN NGHIỆP VỤ MÃ GIẢM GIÁ
| Điều kiện | Giá trị |
| --- | --- |
| Tỷ lệ giảm | 20% (`discountRate = 0.20`) |
| Chặn trần | Tối đa 500.000đ |
| Số khóa tối thiểu | ≥ 2 khóa học |
| Vi phạm | Ném `CouponNotApplicableException` → HTTP 400 |

---

## 3. LUỒNG NGHIỆP VỤ (để trình bày khi thi)

```
[Học viên] chọn khóa học
      │
      ▼
POST /api/checkout  { studentId, courseIds:[...], couponCode }
      │
      ▼
CheckoutService:
   1. Lấy Course theo courseIds → tính subTotal (Σ giá)
   2. Đếm courseCount
   3. Nếu có couponCode → tra Coupon (active)
        └─ CouponService.calculateDiscount()
             ├─ courseCount < 2 → LỖI 400
             └─ discount = min(subTotal×20%, 500000)
   4. finalAmount = subTotal − discount
   5. Lưu Order + OrderItem (status = PAID)
      │
      ▼
JSON: { subTotal, discountAmount, finalAmount, couponCode, courseCount }
```

---

## 4. CÁCH CHẠY & TEST

**Chạy:** `./gradlew bootRun` (hoặc Run trong IntelliJ / Antigravity)

### Test 1 — Đủ điều kiện (2 khóa, giảm 20%)
Request:
```json
POST /api/checkout
{ "studentId": 1, "courseIds": [1, 2], "couponCode": "ELEARN20" }
```
Tính: subTotal = 800.000 + 1.200.000 = 2.000.000
discount = min(2.000.000 × 20% = 400.000, 500.000) = **400.000**
Response:
```json
{ "subTotal": 2000000, "discountAmount": 400000, "finalAmount": 1600000, "couponCode": "ELEARN20", "courseCount": 2 }
```

### Test 2 — Chạm trần 500.000đ (3 khóa giá cao)
```json
{ "studentId": 1, "courseIds": [2, 3, 4], "couponCode": "ELEARN20" }
```
subTotal = 1.200.000 + 1.000.000 + 1.500.000 = 3.700.000
discount = min(740.000, 500.000) = **500.000** (chặn trần)
finalAmount = 3.200.000

### Test 3 — Không đủ điều kiện (1 khóa) → LỖI
```json
{ "studentId": 1, "courseIds": [1], "couponCode": "ELEARN20" }
```
Response HTTP 400:
```json
{ "status": 400, "message": "Mã giảm giá chỉ áp dụng khi mua từ 2 khóa học trở lên." }
```

### Test 4 — Không dùng mã (mua bình thường)
```json
{ "studentId": 1, "courseIds": [1, 2] }
```
discount = 0, finalAmount = subTotal.

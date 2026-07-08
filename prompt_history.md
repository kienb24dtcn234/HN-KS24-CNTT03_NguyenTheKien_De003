# LỊCH SỬ PROMPT — ĐỀ 003: E-LEARNING GIỎ HÀNG & MÃ GIẢM GIÁ

**Sinh viên:** Nguyễn Thế Kiên
**Mã SV:** PTIT232
**Dự án:** Zip_De003 — Spring Boot 3.x + Gradle + Java 17
**Package:** `com.elearning`

---

# PHẦN 1: TỔNG QUAN & CHUẨN BỊ DỰ ÁN

## Mục tiêu kỹ thuật

Đọc hiểu base code E-learning (Spring Boot + Gradle), nắm cấu trúc package `com.elearning` gồm các tầng `models`, `controllers`, `exceptions`, `advice`, `security`. Clone và chạy dự án nền tảng thành công trước khi bổ sung tính năng giỏ hàng & mã giảm giá.

## Lịch sử Prompt (Prompt Chain)

### Prompt 1 — Phân tích base code

```
[Vai trò] Đóng vai Java Backend Mentor chuyên Spring Boot & Gradle.

[Ngữ cảnh] Tôi có một base code hệ thống E-learning (repo Zip_De003), viết
bằng Spring Boot 3.x, build bằng Gradle, package gốc com.elearning. Cấu trúc
gồm các folder: models, controllers, exceptions, advice, security.

[Mục tiêu] Tóm tắt kiến trúc từng tầng, giải thích luồng một request đi qua
các tầng, và hướng dẫn tôi clone + chạy dự án trên IntelliJ.

[Ràng buộc]
- Chỉ ra các file cấu hình cần kiểm tra (build.gradle, application.yml)
- Không sinh code mới, chỉ phân tích cấu trúc có sẵn
```

**Phản hồi AI — Cấu trúc base code:**

1. `models/` — các Entity JPA (Student, Course...)
1. `controllers/` — REST endpoint nhận request
1. `exceptions/` + `advice/` — custom exception & `@RestControllerAdvice`
1. `security/` — cấu hình bảo mật
1. `build.gradle` — khai báo dependency (spring-boot-starter-web, data-jpa)

---

# PHẦN 2: NGHIỆP VỤ MÃ GIẢM GIÁ NÂNG CAO

## Mục tiêu kỹ thuật

Xây dựng mã giảm giá theo quy tắc nghiệp vụ nâng cao: **giảm 20% tổng đơn hàng**, **không giảm quá 500.000đ (chặn trần)**, **chỉ áp dụng khi mua từ 2 khóa học trở lên**. Nếu mua dưới 2 khóa → báo lỗi không dùng được. Tách logic vào Service riêng (SRP), dùng `BigDecimal` cho tiền tệ.

## Lịch sử Prompt (Prompt Chain)

### Prompt 1 — Thiết kế thuật toán mã giảm giá

```
[Vai trò] Đóng vai Java Senior Developer chuyên Clean Code & SOLID.

[Ngữ cảnh] Hệ thống E-learning cho phép học viên mua nhiều khóa học một lúc.
Phòng marketing yêu cầu một loại mã giảm giá nâng cao, không phải giảm %
thông thường.

[Mục tiêu] Thiết kế thuật toán tính mã giảm giá theo quy tắc nghiệp vụ.

[Ràng buộc]
- Giảm 20% tổng đơn hàng
- Không giảm quá tối đa 500.000đ (mức chặn trần)
- Chỉ áp dụng cho học viên mua từ 2 khóa học trở lên
- Nếu dưới 2 khóa → ném exception "không dùng được"
- Dùng BigDecimal, làm tròn HALF_UP, tách vào Service riêng (SRP)

Đừng viết code ngay, hãy mô tả công thức và các nhánh xử lý trước.
```

**Phản hồi AI — Công thức:**

```
courseCount < 2  →  ném CouponNotApplicableException
rawDiscount    = subTotal × 0.20   (làm tròn HALF_UP)
discountAmount = min(rawDiscount, 500000)
finalAmount    = subTotal − discountAmount
```

### Prompt 2 — Sinh code CouponService

```
[Ngữ cảnh] Công thức đã chốt ở prompt trên.

[Mục tiêu] Sinh code CouponService hoàn chỉnh (Java 17, Spring @Service).

[Ràng buộc]
- Đọc discountRate / maxDiscount / minCourses từ Entity Coupon (không hardcode)
- Dùng BigDecimal.min() để áp mức chặn trần
- Custom exception CouponNotApplicableException kèm thông báo tiếng Việt
- Method: BigDecimal calculateDiscount(Coupon coupon, BigDecimal subTotal, int courseCount)
```

**Phản hồi AI:** sinh `CouponService.calculateDiscount()` đọc điều kiện từ Entity `Coupon`, dùng `subTotal.multiply(rate).setScale(0, HALF_UP).min(maxDiscount)`.

## Phân tích lỗi AI

**Lỗi AI ở lần sinh code đầu tiên (Prompt 2):** AI dùng `double` cho tính tiền và hardcode trực tiếp `0.20`, `500000` bên trong Service — vi phạm nguyên tắc tránh sai số tiền tệ và khó bảo trì khi đổi điều kiện.

**Cách khắc phục:** Tôi phản biện — "Tiền tệ phải dùng `BigDecimal` để tránh sai số làm tròn; các tham số điều kiện nên lưu trong Entity `Coupon` thay vì hardcode." → AI sửa lại: `Coupon` giữ `discountRate`, `maxDiscount`, `minCourses`; `CouponService` chỉ đọc từ Entity.

---

# PHẦN 3: PHÂN TÍCH & THIẾT KẾ HỆ THỐNG VỚI AI

## Nhiệm vụ 1: Phân tích đặc tả SRS

### Prompt

```
[Vai trò] Đóng vai System Analyst chuyên phân tích SRS.

[Ngữ cảnh] Base code E-learning đã có sẵn Entity Student và Course. Tôi cần
bổ sung nghiệp vụ giỏ hàng và mã giảm giá.

[Mục tiêu] Dựa trên các Entity hiện có, tạo file srs.md. Thiết kế cấu trúc
dữ liệu và xác định các Entity cần thêm để mô phỏng: giỏ hàng, lưu trữ
thông tin, mã giảm giá.

[Ràng buộc]
- Tự quyết định điều kiện khách hàng (phần trăm, mức chặn trần, số lượng khóa)
- Nhất quán với nghiệp vụ Phần 2 (20% / 500K / ≥2 khóa)
- Vẽ ERD bằng Mermaid với PK/FK và quan hệ 1-N
```

### Phản hồi AI — Danh sách Entities cần thêm

1. **Cart** — id (PK), studentId (FK), createdAt
1. **CartItem** — id (PK), cartId (FK), courseId (FK), priceSnapshot
1. **Coupon** — id (PK), code, discountRate, maxDiscount, minCourses, active
1. **Order** — id (PK), studentId (FK), subTotal, discountAmount, finalAmount, couponCode, status
1. **OrderItem** — id (PK), orderId (FK), courseId (FK), price

## Phân tích lỗi AI

**Lỗi AI:** AI ban đầu gộp giỏ hàng và đơn hàng làm một Entity duy nhất.

**Cách khắc phục:** Tôi phản biện — "Giỏ hàng (tạm thời) và đơn hàng (đã thanh toán, lưu trữ lâu dài) có vòng đời khác nhau, phải tách riêng." → AI tách `Cart/CartItem` khỏi `Order/OrderItem`, và thêm `priceSnapshot` để chốt giá tại thời điểm giao dịch, tránh sai lệch nếu giá khóa học đổi sau đó.

---

## Nhiệm vụ 2: Lập trình tính năng bổ sung

### Prompt

```
[Vai trò] Đóng vai Java Backend Developer.

[Ngữ cảnh] SRS đã chốt ở Nhiệm vụ 1, gồm 5 Entity mới. CouponService (thuật
toán mã giảm) đã có ở Phần 2.

[Mục tiêu] Sinh code hoàn chỉnh 3 tầng cho tính năng giỏ hàng + mã giảm giá.

[Ràng buộc]
- Yêu cầu 1: Entity, Repository, DTO theo chuẩn JPA
- Yêu cầu 2: Service chuẩn thuật toán (dùng lại CouponService)
- Yêu cầu 3: Controller cung cấp API tính tiền, JSON trả về:
             tổng tiền ban đầu, số tiền được giảm, số tiền cuối phải trả
- Entity dùng annotation JPA đầy đủ (@Entity, @Id, @ManyToOne, @OneToMany)
- CartItem có unique constraint (cartId + courseId)
```

### Phản hồi AI — Kết quả

- **Entity:** Student, Course, Cart, CartItem, Coupon, Order, OrderItem
- **Repository:** Student/Course/Coupon/Order Repository (JpaRepository)
- **DTO:** CheckoutRequest (input), CheckoutResponse (output)
- **Service:** CouponService (thuật toán) + CheckoutService (điều phối)
- **Controller:** `POST /api/checkout` trả JSON đúng 3 trường tiền

### JSON trả về mẫu

```json
{ "subTotal": 2000000, "discountAmount": 400000, "finalAmount": 1600000 }
```

---

## Nhiệm vụ 3: Tối ưu & Xử lý ngoại lệ

### Prompt

```
[Vai trò] Đóng vai Java Backend Developer chuyên xử lý lỗi tập trung.

[Ngữ cảnh] Tính năng đã chạy được. Front-end cần thông báo lỗi rõ ràng khi
người dùng nhập mã giảm giá sai hoặc không hợp lệ.

[Mục tiêu]
- Yêu cầu 1: Chạy thử dự án, kiểm tra toàn bộ logic, không lỗi syntax.
- Yêu cầu 2: Cập nhật GlobalExceptionHandler. Nếu người dùng nhập mã sai hoặc
             không hợp lệ → ném BusinessException kèm thông báo lỗi phù hợp,
             trả JSON có errorCode để front-end dễ xử lý.

[Ràng buộc]
- Tạo BusinessException gốc chứa errorCode + HttpStatus
- CouponNotApplicable / InvalidCoupon / ResourceNotFound kế thừa BusinessException
- GlobalExceptionHandler trả JSON: timestamp, status, errorCode, message
```

### Phản hồi AI — JSON lỗi trả về

```json
{
  "status": 400,
  "errorCode": "INVALID_COUPON",
  "message": "Mã giảm giá không tồn tại hoặc đã hết hiệu lực."
}
```

## Phân tích lỗi AI

**Lỗi AI ở lần sinh code đầu tiên:** AI viết mỗi loại lỗi một `@ExceptionHandler` riêng lẻ, và ném lỗi mã sai bằng `ResourceNotFoundException` (HTTP 404) — không đúng ngữ nghĩa, front-end khó phân biệt lỗi nghiệp vụ với lỗi "không tìm thấy tài nguyên".

**Cách khắc phục:** Tôi phản biện — "Mã sai là lỗi nghiệp vụ (400), không phải 404. Nên có một `BusinessException` gốc mang `errorCode` để handler bắt tập trung; front-end chỉ cần đọc `errorCode` là biết cách xử lý." → AI sửa lại: tạo `BusinessException` (errorCode + status), các exception con kế thừa; `GlobalExceptionHandler` chỉ cần bắt `BusinessException` là bao trùm mọi lỗi nghiệp vụ.

# Hệ thống quản lý bán hàng và kho phim "ChauPhim"

# Project Overview

Dự án "ChauPhim" là một ứng dụng web được phát triển nhằm hiện đại hóa việc mua bán và quản lý phim. Hệ thống cung cấp nền tảng bán phim trực tuyến, giúp người dùng dễ dàng tìm kiếm, mua sắm và theo dõi giao dịch. Đồng thời, nó trang bị cho nhà quản lý các công cụ cần thiết để quản lý toàn diện thông tin phim, diễn viên, đạo diễn, studio, người dùng và đơn hàng. Được xây dựng với Java (Spring Boot) ở Back-end, HTML, CSS, JavaScript (Bootstrap) ở Front-end và PostgreSQL cho cơ sở dữ liệu, "ChauPhim" hướng đến việc tối ưu hóa hiệu quả hoạt động trong lĩnh vực phân phối phim kỹ thuật số.

# Installation and Setup
## 1. Clone repository
```bash
git clone https://github.com/Phla25/Project
```

## 2. Mở Project trong IDE
- Mở Intellij IDEA hoặc Eclipse.
- Chọn File -> Open Project và trỏ đến thư mục gốc của Project mà vừa clone về.
- IDE sẽ tự động nhận diện là một project Maven và import các dependency cần thiết.

## 3. Chạy chương trình
```bashbash
- Chọn thư mục ChauPhim -> Run As -> Maven Clean -> Maven Build
- Tìm file ChauPhimApplication.java trong src -> Run As -> Java Application
```
- Nhập đường link sau trên website để đăng ký tài khoản manager: [Manager Register](http://localhost:8080/manager-register)
- Nhập đường link sau trên website để đăng ký tài khoản customer: [Customer Register](http://localhost:8080/customer-register)

# Tech Stack
- Front-end: HTML, CSS, JavaScript (Bootstrap)
- Back-end: Java, Spring Boot
- Database: PostgreSQL

# SRT Todo Application

Ứng dụng quản lý công việc (Todo List) hoàn chỉnh tích hợp xác thực tài khoản tùy biến, phân quyền bảo mật JWT, và cấu hình chạy đa dạng (chạy trực tiếp Local hoặc qua Docker Compose).

---

## 📌 Mục lục
- [🛠️ Công nghệ Sử dụng (Tech Stack)](#️-công-nghệ-sử-dụng-tech-stack)
- [🌟 Các Tính năng Nổi bật](#-các-tính-năng-nổi-bật)
  - [📝 Tính năng Nghiệp vụ (Core Todo)](#-tính-năng-nghiệp-vụ-core-todo)
  - [🔐 Bảo mật JWT (JWT Security)](#-bảo-mật-jwt-jwt-security)
  - [📱 Responsive UI](#-responsive-ui)
  - [📑 Phân trang & Sắp xếp dữ liệu](#-phân-trang--sắp-xếp-dữ-liệu)
  - [🐳 Triển khai Docker Compose](#-triển-khai-docker-compose)
  - [🧪 Unit Test](#-unit-test)
  - [⚡ Tối ưu Hiệu năng (Performance)](#-tối-ưu-hiệu-năng-performance)
- [📂 Cấu trúc Thư mục Chính](#-cấu-trúc-thư-mục-chính)
- [🚀 Hướng dẫn Chạy Dự án](#-hướng-dẫn-chạy-dự-án)
  - [Cách 1: Chạy thông qua Docker Compose (Khuyên dùng)](#cách-1-chạy-thông-qua-docker-compose-khuyên-dùng)
  - [Cách 2: Chạy trực tiếp trên máy cục bộ (Local Run)](#cách-2-chạy-trực-tiếp-trên-máy-cục-bộ-local-run)
- [🧪 Kiểm thử (Testing)](#-kiểm-thử-testing)

---

## 🛠️ Công nghệ Sử dụng (Tech Stack)

Hệ thống được phát triển theo mô hình **Client-Server** sử dụng các công nghệ hiện đại được định nghĩa trong `client/package.json` và `server/pom.xml`:

### 1. Frontend (`/client`)
Ứng dụng trang đơn (SPA) được phát triển bằng **React 19**, **Vite** và **TypeScript**:
- **State Management**: Quản lý trạng thái đăng nhập toàn cục qua **Redux Toolkit** (`authSlice`).
- **API Client**: Gửi yêu cầu qua **Axios** với bộ lọc phản hồi (Response Interceptor) tự động chuẩn hóa cấu hình thành công (`success: boolean` -> `status: 'success'`).
- **UI/UX Components**: Xây dựng dựa trên **Radix UI Primitives**, kết hợp **Lucide React** (icons) và thông báo đẩy **Sonner Toast**.
- **CSS Framework**: **Tailwind CSS (v3)** cho giao diện nhanh, linh hoạt và tối ưu hóa CSS bundle.
- **Routing**: Quản lý điều hướng bảo mật (Private Routes) bằng **React Router DOM v7**.
- **Testing**: Cấu hình chạy test với **Jest** và **React Testing Library**.

### 2. Backend (`/server`)
Được xây dựng bằng **Java 21** và **Spring Boot**, tuân thủ mô hình kiến trúc phân lớp (Layered Architecture):
- **Controller**: Tiếp nhận và điều phối các yêu cầu HTTP RESTful APIs từ phía client.
- **Service**: Xử lý toàn bộ logic nghiệp vụ (quản lý công việc, cơ chế mã hóa mật khẩu, phân tích/xử lý ngày hạn chót ISO-8601).
- **Repository**: Sử dụng **Spring Data JPA / Hibernate** tương tác với cơ sở dữ liệu PostgreSQL.
- **Security & Auth**: Bảo mật bằng **Spring Security + JWT (JSON Web Tokens)**. Hỗ trợ xác thực kép:
  - Đăng nhập thủ công qua định danh tùy chọn (`accountName`).
  - Xác thực qua JWT sử dụng mã định danh hệ thống UUID (`userName`) làm chủ thể (Subject).
- **Validation**: Xác thực đầu vào với **Spring Boot Starter Validation**.
- **Testing**: Viết unit test nghiệp vụ bằng **JUnit 5** và **Mockito**.

### 3. Database & DevOps
- **Database**: Sử dụng **PostgreSQL 17** là hệ quản trị cơ sở dữ liệu quan hệ chính.
- **Containerization**: 
  - **Multi-stage Build**: Giảm kích thước Docker Image của backend bằng cách tách giai đoạn build (Maven JDK 21) và chạy (JRE 21).
  - **Docker Compose**: Đồng bộ khởi chạy PostgreSQL và Backend, thiết lập cơ chế kiểm tra sức khỏe (`pg_isready`) đảm bảo dữ liệu sẵn sàng trước khi nạp ứng dụng.

---

## 🌟 Các Tính năng Nổi bật

Dự án áp dụng các tiêu chuẩn và tính năng nâng cao sau:

### 📝 Tính năng Nghiệp vụ (Core Todo)
- **Hiển thị danh sách công việc**: Danh sách nhiệm vụ được trình bày trực quan và ngăn nắp, thể hiện đầy đủ các thông tin cần thiết: Tiêu đề, mô tả, ngày đến hạn (Due Date) và mức độ ưu tiên.
- **Quản lý nhiệm vụ toàn diện (CRUD Task)**: 
  - **Create**: Thêm mới nhiệm vụ nhanh chóng với các tùy chọn ngày đến hạn và gắn nhãn quan trọng.
  - **Read/Update**: Xem chi tiết và cập nhật nội dung công việc mọi lúc.
  - **Delete**: Cơ chế xóa mềm đưa nhiệm vụ vào thùng rác (giúp tránh việc vô tình xóa mất dữ liệu), cho phép khôi phục lại (Restore) hoặc xóa vĩnh viễn (Hard Delete).
- **Đánh dấu trạng thái Hoàn thành**: Dễ dàng đánh dấu nhiệm vụ đã hoàn thành hoặc khôi phục về trạng thái chưa hoàn thành chỉ với một click thông qua Checkbox tiện lợi.
- **Tìm kiếm & Bộ lọc linh hoạt**: 
  - Cho phép người dùng tìm kiếm tức thời các công việc dựa trên **Tiêu đề (Title)**.
  - Hệ thống bộ lọc (Filter tabs) đa dạng hỗ trợ phân loại nhanh công việc theo các trạng thái: Đang làm (Active), Quan trọng (Important), Đã hoàn thành (Completed), và Đã xóa/Thùng rác (Deleted).

### 🔐 Bảo mật JWT (JWT Security)
- **Cơ chế xác thực phi trạng thái (Stateless Authentication)**: 
  - Toàn bộ các API yêu cầu dữ liệu Todo đều được cấu hình kiểm soát truy cập thông qua bộ lọc `SecurityConfig.java`.
  - Sử dụng Token JWT được ký mã hóa và xác thực tự động ở mỗi request thông qua `JwtUtils.java` và `JwtAuthenticationFilter.java`.
- **Hệ thống Đăng ký / Đăng nhập an toàn**: Mật khẩu người dùng được mã hóa bằng thuật toán `BCryptPasswordEncoder` trước khi lưu vào database.
- **Phân quyền truy cập tài nguyên**: Đảm bảo người dùng đã đăng nhập chỉ có thể truy vấn, sửa đổi hoặc xóa các công việc do chính tài khoản của họ tạo ra, ngăn chặn việc rò rỉ hoặc can thiệp chéo dữ liệu giữa các tài khoản.

### 📱 Responsive UI
- Giao diện người dùng được thiết kế responsive hoàn chỉnh, tối ưu trải nghiệm trên mọi kích thước màn hình từ điện thoại di động, máy tính bảng đến máy tính để bàn lớn.
- Sử dụng các layout linh hoạt (Grid, Flexbox) và breakpoints của Tailwind CSS để tự động thích ứng.
- Sidebar điều hướng có thể thu gọn linh hoạt trên màn hình desktop và ẩn/hiện dạng Drawer trên điện thoại di động để tiết kiệm diện tích.

### 📑 Phân trang & Sắp xếp dữ liệu
- **Phân trang (Pagination)**: Hỗ trợ phân trang phía Backend sử dụng API `Pageable` của Spring Data JPA. Frontend nhận dữ liệu và hiển thị thanh chuyển trang trực quan (Trang trước/sau, tổng số trang) tại `Dashboard.tsx`, giúp tăng tốc độ tải và giảm băng thông truyền tải dữ liệu.
- **Sắp xếp (Sorting)**: Backend tự động sắp xếp danh sách công việc theo thời gian tạo mới nhất (`createdDate` giảm dần - DESC) trước khi trả về qua API nhờ cấu hình `Sort.by(Sort.Direction.DESC, "createdDate")` trong `TaskServiceImpl.java`.

### 🐳 Triển khai Docker Compose
- Hệ thống hỗ trợ khởi dựng toàn diện chỉ bằng một câu lệnh: `docker compose up -d --build` thông qua tệp cấu hình `docker-compose.yml`.
- Tự động hóa quá trình xây dựng image cho dịch vụ Spring Boot qua `Dockerfile`.
- Cấu hình mạng độc lập (`srt-network`) và volume bền vững (`postgres_data`) đảm bảo dữ liệu PostgreSQL không bị mất khi container dừng/khởi động lại.
- Dữ liệu mẫu (sample data) được tự động nạp vào DB khi khởi tạo lần đầu thông qua file khởi tạo SQL `init-sample-data.sql`.

### 🧪 Unit Test
- **Backend**: Đã triển khai bộ kiểm thử đơn vị (Unit Tests) hoàn chỉnh cho tầng dịch vụ nghiệp vụ chính `AuthServiceImplTest.java` và `TaskServiceImplTest.java` sử dụng framework **JUnit 5** kết hợp với thư viện giả lập **Mockito**.
- **Frontend**: Môi trường kiểm thử được thiết lập đầy đủ với **Jest**, **React Testing Library** và **ts-jest**, cấu hình sẵn mock cho các Web API (như IntersectionObserver) trong `setupTests.ts` giúp sẵn sàng phát triển thêm các kịch bản test giao diện.

### ⚡ Tối ưu Hiệu năng (Performance)
- **Search Debouncing**: Tích hợp cơ chế trì hoãn (Debounce 500ms) ở ô tìm kiếm tại `Dashboard.tsx`. Khi người dùng gõ từ khóa, hệ thống đợi ngắt phím đủ 500ms mới kích hoạt API gọi lên backend, tránh spam các request liên tục không cần thiết.
- **Database Lazy Loading**: Thực thể `Task.java` được cấu hình liên kết với `User` dạng `@ManyToOne(fetch = FetchType.LAZY)`. Điều này giúp tránh hiện tượng N+1 query và chỉ tải thông tin người dùng khi thực sự cần thiết, cải thiện đáng kể tốc độ truy cập cơ sở dữ liệu.
- **Vite Build**: Quy trình build tối ưu hóa mã nguồn tĩnh phía Frontend, giảm thiểu dung lượng file đóng gói để người dùng tải trang nhanh nhất.

---

## 📂 Cấu trúc Thư mục Chính

```bash
SRT_test/
├── client/                     # Mã nguồn Frontend (React Vite TS)
│   ├── src/
│   │   ├── api/                # Axios API calls (authApi.ts, taskApi.ts)
│   │   ├── components/         # Các component dùng chung
│   │   ├── config/             # Cấu hình tĩnh (routes, endpoints)
│   │   ├── pages/              # Trang giao diện (Auth, Todo Dashboard)
│   │   └── store/              # Redux slices quản lý state
│   ├── package.json
│   └── tailwind.config.js
├── server/                     # Mã nguồn Backend (Spring Boot)
│   ├── src/main/java/SRT/test/
│   │   ├── controller/         # REST Controllers
│   │   ├── dto/                # Request & Response DTOs
│   │   ├── entity/             # JPA Entities (User, Task)
│   │   ├── repo/               # Database Repositories
│   │   ├── security/           # JWT, Cors, UserDetailsService
│   │   └── service/            # Lớp dịch vụ nghiệp vụ (ITaskService, IAuthService)
│   ├── src/test/               # Bộ mã nguồn unit tests (Auth & Task services)
│   ├── Dockerfile              # Hướng dẫn build ảnh Docker Backend
│   └── pom.xml                 # Cấu hình Maven dependencies
└── docker-compose.yml          # File Docker Compose khởi chạy toàn bộ hệ thống
```

---

## 🚀 Hướng dẫn Chạy Dự án

Bạn có thể chạy dự án theo một trong hai cách dưới đây:

### Cách 1: Chạy thông qua Docker Compose (Khuyên dùng)

Cách này sẽ tự động khởi dựng cơ sở dữ liệu PostgreSQL và máy chủ Backend mà không cần cài đặt môi trường Java/Postgres trên máy cá nhân của bạn.

#### Yêu cầu:
* Máy tính đã cài đặt **Docker** và **Docker Desktop**.

#### Các bước khởi chạy:

1. Mở terminal tại thư mục gốc của dự án (nơi chứa file `docker-compose.yml`).
2. Khởi chạy Docker Compose bằng lệnh:
   ```bash
   docker compose up -d --build
   ```
3. Docker sẽ tiến hành tải ảnh PostgreSQL, xây dựng mã nguồn Java bên trong container và chạy dịch vụ ngầm. 
   * **Database**: Chạy trên cổng `5434`.
   * **Backend REST API**: Chạy trên cổng `8080`.
4. **Khởi chạy Frontend**:
   * Đảm bảo máy tính đã cài đặt **Node.js** (v18 trở lên).
   * Mở terminal mới, truy cập vào thư mục `client/`, cài đặt các thư viện và khởi chạy dự án:
     ```bash
     npm install
     npm run dev
     ```
     *(Hoặc dùng `pnpm install` và `pnpm dev` nếu bạn sử dụng pnpm).*
     Ứng dụng FE sẽ được khởi chạy tại địa chỉ `http://localhost:5174/`.

---

### Cách 2: Chạy trực tiếp trên máy cục bộ (Local Run)

#### Yêu cầu môi trường:
* **Java 21** hoặc cao hơn.
* **Node.js** (v18 trở lên) & **npm** hoặc **pnpm**.
* **PostgreSQL** đang chạy trên cổng `5434` (hoặc cấu hình lại trong file `application.properties`).

#### Các bước khởi chạy:

1. **Khởi động Database**:
   Đảm bảo PostgreSQL đang chạy và đã tạo cơ sở dữ liệu tên là `srt`. Tài khoản/mật khẩu mặc định: `admin` / `123`.

2. **Chạy Backend**:
   Mở terminal tại thư mục `server/` và thực thi:
   ```bash
   mvn spring-boot:run
   ```
   Máy chủ Backend sẽ lắng nghe tại cổng `http://localhost:8080`.

3. **Chạy Frontend**:
   Mở một terminal mới tại thư mục `client/` và thực thi:
   ```bash
   npm install
   npm run dev
   ```
   Ứng dụng FE sẽ được khởi chạy tại địa chỉ `http://localhost:5174/`.

---

## 🧪 Kiểm thử (Testing)

Dự án đã cấu hình đầy đủ Unit Test cho phần xử lý logic nghiệp vụ chính (Authentication và Task Management).

Để chạy toàn bộ bộ thử nghiệm của Backend, truy cập thư mục `server/` và gõ lệnh:
```bash
mvn test
```
*(Hệ thống sẽ tự động thiết lập múi giờ kiểm thử thành UTC để tương thích tốt nhất với hệ quản trị CSDL cục bộ).*
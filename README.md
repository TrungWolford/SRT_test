# SRT Todo Application

Ứng dụng quản lý công việc (Todo List) hoàn chỉnh tích hợp xác thực tài khoản tùy biến, phân quyền bảo mật JWT, và cấu hình chạy đa dạng (chạy trực tiếp Local hoặc qua Docker Compose).

---

## 🛠️ Kiến trúc Hệ thống

Dự án được xây dựng theo mô hình **Client-Server** chia làm hai phần chính:

### 1. Backend (`/server`)
Được xây dựng bằng **Java Spring Boot**, tuân thủ mô hình kiến trúc phân lớp (Layered Architecture):
* **Controller**: Tiếp nhận và điều phối các yêu cầu HTTP RESTful APIs từ phía client.
* **Service**: Xử lý toàn bộ logic nghiệp vụ (quản lý công việc, cơ chế mã hóa mật khẩu, phân tích/xử lý ngày hạn chót ISO-8601).
* **Repository**: Sử dụng Spring Data JPA / Hibernate tương tác với hệ quản trị cơ sở dữ liệu PostgreSQL.
* **Security & Auth**: Bảo mật bằng **Spring Security + JWT (JSON Web Tokens)**. Hỗ trợ xác thực kép:
  * Đăng nhập thủ công qua định danh tùy chọn (`accountName`).
  * Xác thực qua JWT sử dụng mã định danh hệ thống UUID (`userName`) làm chủ thể (Subject).

### 2. Frontend (`/client`)
Ứng dụng trang đơn (SPA) được phát triển bằng **React, Vite và TypeScript**:
* **State Management**: Quản lý trạng thái đăng nhập toàn cục qua **Redux Toolkit** (`authSlice`).
* **API Client**: Gửi yêu cầu qua **Axios** với bộ lọc phản hồi (Response Interceptor) tự động chuẩn hóa cấu hình thành công (`success: boolean` -> `status: 'success'`).
* **UI/UX**: Sử dụng **Tailwind CSS** kết hợp biểu tượng **Lucide React** và thông báo đẩy **Sonner Toast**.
* **Routing**: Quản lý điều hướng bảo mật (Private Routes) bằng **React Router DOM**.

### 3. Containerization (`Docker`)
* **Multi-stage Build**: Giảm kích thước Docker Image của backend bằng cách tách giai đoạn build (Maven JDK 21) và chạy (JRE 21).
* **Docker Compose**: Đồng bộ khởi chạy cơ sở dữ liệu PostgreSQL và máy chủ Backend, thiết lập cơ chế kiểm tra sức khỏe (`pg_isready`) đảm bảo dữ liệu sẵn sàng trước khi nạp ứng dụng.

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

### Cách 1: Chạy trực tiếp trên máy cục bộ (Local Run)

#### Yêu cầu môi trường:
* **Java 21** hoặc cao hơn.
* **Node.js** (v18 trở lên) & **npm** hoặc **pnpm**.
* **PostgreSQL** đang chạy trên cổng `5434` (hoặc cấu hình lại trong file [application.properties](file:///d:/Project/SRT_test/server/src/main/resources/application.properties)).

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

### Cách 2: Chạy thông qua Docker Compose (Khuyên dùng)

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
4. Sau đó, bạn chỉ cần khởi chạy FE ở cục bộ bằng cách vào thư mục `client/` và chạy `npm run dev`.

---

## 🧪 Kiểm thử (Testing)

Dự án đã cấu hình đầy đủ Unit Test cho phần xử lý logic nghiệp vụ chính (Authentication và Task Management).

Để chạy toàn bộ bộ thử nghiệm của Backend, truy cập thư mục `server/` và gõ lệnh:
```bash
mvn test
```
*(Hệ thống sẽ tự động thiết lập múi giờ kiểm thử thành UTC để tương thích tốt nhất với hệ quản trị CSDL cục bộ).*
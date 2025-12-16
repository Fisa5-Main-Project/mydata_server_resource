# 노후하우 리소스 서버 (KnowWhoHow Resource Server)

## 1. 프로젝트 개요
**KnowWhoHow Resource Server**는 마이데이터 서비스의 핵심인 사용자 금융 데이터(자산, 계좌 등)를 저장하고 제공하는 데이터 제공자(Data Provider) 서버입니다.

Spring OAuth2 Resource Server를 기반으로 구축되었으며, 인증 서버(Auth Server)가 발급한 **Access Token(JWT)**의 유효성을 검증하고, 허가된 클라이언트에게만 보호된 리소스(API)를 제공합니다.

인증 서버와 마찬가지로 온프레미스(On-Premise) 환경에서 DB 이중화 및 강력한 암호화 전략을 적용하여 금융 데이터의 기밀성과 무결성을 보장합니다.

---

## 2. 주요 기능
* **OAuth 2.0 Resource Server:** `Authorization: Bearer <Token>` 헤더를 파싱하여 표준 인증 흐름을 처리.
* **Stateless 토큰 검증:** 인증 서버의 공개키(Public Key)를 통해 JWT 서명(Signature)을 자체적으로 검증 (인증 서버 부하 감소).
* **권한 기반 접근 제어 (Scope Check):** JWT 내의 scope (예: `my.data.read`)를 확인하여 API 접근 권한 제어.
* **마이데이터 API 제공:** 사용자의 금융 자산 데이터를 조회하여 메인 서버(BFF)에 제공.
* **DB 이중화 및 데이터 보안:**
    * Master(Write) / Slave(Read) 구조의 고가용성 아키텍처.
    * 민감 정보(주민번호, 계좌번호 등)에 대한 AES-256-GCM 암호화 저장.

---

## 3. 기술 스택 (Tech Stack)

### 3.1. Infrastructure (On-Premise)
| Category | Technology | Description |
| :--- | :--- | :--- |
| **Server** | Linux (Ubuntu) | 온프레미스 물리 서버 환경 |
| **CI/CD** | Jenkins | 자동화된 빌드 및 배포 파이프라인 |
| **Container** | Docker, Docker Compose | 서비스 컨테이너화 및 실행 |
| **Load Balancer** | HAProxy | DB 트래픽 분산 및 Failover |

### 3.2. Backend
* **Language:** Java 17
* **Framework:** Spring Boot 3.2
* **Security:**
    * Spring Security 6
    * Spring OAuth2 Resource Server (JWT Validation)
* **Database:** MySQL 8.0 (Replication 적용)
* **ORM:** Spring Data JPA

---

## 4. 인프라 및 보안 아키텍처
리소스 서버는 실제 금융 데이터를 다루므로 데이터 보호와 조회의 안정성에 최우선을 두었습니다.

### 4.1. JWT 서명 검증 (Signature Verification)
리소스 서버는 인증 서버와 직접 통신하지 않고, 비대칭 키(RSA) 방식을 사용하여 토큰을 검증합니다.

1. 서버 시작 시 인증 서버(Issuer-URI)의 JWK Set 엔드포인트(`/.well-known/jwks.json`)에서 **공개키(Public Key)**를 조회하여 캐싱합니다.
2. API 요청이 오면 이 공개키를 사용해 JWT의 위변조 여부와 만료 시간(exp)을 검증합니다.

### 4.2. DB 이중화 (Replication)
* **Master DB:** 데이터 변경(금융 데이터 갱신 등) 트랜잭션 처리.
* **Slave DB:** 대량의 마이데이터 조회(Read) 트랜잭션 처리.
* **HAProxy:** 쿼리 타입에 따른 라우팅 및 DB 헬스 체크 수행.

### 4.3. 데이터 암호화
* 데이터베이스에 저장되는 모든 민감 정보(PII)는 **AES-256-GCM** 알고리즘으로 암호화되어 저장됩니다.
* 애플리케이션 레벨에서 `AttributeConverter`를 통해 투명하게 암호화/복호화가 이루어집니다.

---

## 5. 디렉토리 구조
```bash
knowwhohow-resource/
├── src/
│   ├── main/
│   │   ├── java/com/knowwhohow/
│   │   │   ├── global/
│   │   │   │   ├── config/          # SecurityConfig (ResourceServer 설정), JpaConfig
│   │   │   │   └── util/            # AESUtil (암호화 모듈)
│   │   │   ├── domain/
│   │   │   │   ├── controller/      # MyDataApiController (데이터 조회 API)
│   │   │   │   ├── service/         # MyDataService (비즈니스 로직)
│   │   │   │   ├── entity/          # Asset, Account, Transaction 등
│   │   │   │   └── repository/      # JPA Repositories
│   │   │   └── ResourceApplication.java
│   │   └── resources/
│   │       ├── application.yml      # 서버 설정 (Issuer URI 포함)
├── docker/                          # Dockerfile
└── build.gradle
```

---

## 6. API 명세
리소스 서버는 유효한 Access Token을 보유한 클라이언트에게만 응답합니다.

**Header:** `Authorization: Bearer {Access_Token}`

| Method | Endpoint | Description | Required Scope |
| :---: | :--- | :--- | :--- |
| GET | `/api/v1/my-data` | 사용자의 전체 자산 및 금융 정보 조회 | `my.data.read` |
| GET | `/api/v1/my-data/accounts` | 계좌 목록 조회 | `my.data.read` |
| GET | `/api/v1/my-data/cards` | 카드 소비 내역 조회 | `my.data.read` |

---

## 7. 실행 가이드

### ⚠️ 중요: 실행 순서
> 리소스 서버는 시작 시 인증 서버의 공개키를 가져와야 하므로, **반드시 인증 서버(Auth Server)가 먼저 실행되어 있어야 합니다.**

### 실행 방법

**1. 레포지토리 클론**
```bash
git clone [https://github.com/Fisa5-Main-Project/mydata_server_resource.git](https://github.com/Fisa5-Main-Project/mydata_server_resource.git)
cd mydata_server_resource
```
**2. DB 컨테이너 실행**
```bash
docker-compose up -d my-data-resource-db
```
**3. 애플리케이션 빌드 및 실행**
```bash
# 인증 서버가 localhost:9000에 떠 있는지 확인 필수
./gradlew clean build
java -jar build/libs/knowwhohow-resource-0.0.1-SNAPSHOT.jar
```

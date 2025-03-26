# Project Wave

## 📢 프로젝트 개요 (Overview)

**Project Wave**는 번개장터를 벤치마킹한 중고거래 플랫폼이며, 어떠한 예외처리도 용납하지 않는 안정성과 완성도가 높은 플랫폼입니다:)

- **주요 목표**:
  - 안전하고 신뢰성 있는 중고 거래 환경 제공
  - 상점 관리 페이지를 통한 통합 정보 조회 제공
  - 구매자 판매자 간의 채팅 기능 제공
  - 소셜 로그인 시스템 구현
  - Toss Payments 결제 시스템 도입

- **주요 기능**:
  - 상품 (등록, 수정, 검색, 필터)
  - 채팅
  - 결제 (Toss API 활용, 결제내역 조회)
  - 회원 (회원 가입, 로그인, 수정, soft 탈퇴)
  - 소셜 로그인 (Google, Kakao)
  - 후기, 별점
  - 찜, 팔로우
  - 상점 관리(회원, 상품)

## 📌 배포 링크
[http://ec2-52-78-157-47.ap-northeast-2.compute.amazonaws.com:8080]

## 📌 기술 스택 (Tech Stack)

- **프론트엔드**: JavaScript, Thymeleaf
- **백엔드**: Java, Spring Boot, JPA
- **데이터베이스**: Oracle
- **보안**: Spring Security, OAuth2, BCrypt
- **빌드 도구**: Gradle
- **배포**: AWS EC2, Elastic IP
- **테스트**: Postman

## 🎯 구현 결과 (프리뷰 링크입니다!)
[https://highfalutin-breeze-eae.notion.site/Project-Wave-1a46f9e05cdc81bd8f52edfabc68f29b]

### **📆**프로젝트 기간

2025.01.20 ~ 2025.03.20

## 📌 향후 개선 사항 (Future Improvements)
1. 회원 등급 시스템 도입
    - 회원 활동에 따라 파워 유저 등 등급 나누기.
2. 모바일 반응형 개발
    - 웹 플랫폼에 대한 접근성을 높임.
3. 탈퇴한 회원의 유예기간을 정한 뒤 기한 만료 시 회원 삭제
4. FAQ 구현

## 📌 프로젝트 구조 (Project Structure)

```plaintext
project-wave
├── 📂 src/main/java/org/example/store
│     ├── 📂 chat                  # 채팅 생성, 채팅 목록 조회
│     ├── 📂 chatRoom              # 채팅 내역, 상품 정보, 구매성공 메시지 및 이미지가 담기는 채팅방
│     ├── 📂 follow                # 팔로우/취소, 사용자/타인의 팔로우, 팔로잉 목록 조회
│     ├── 📂 like_product          # 상품 찜/취소, 사용자가 찜한 상품 목록 조회
│     ├── 📂 member                # 회원가입, (소셜)로그인, 회원수정, soft탈퇴
│     ├── 📂 memberReview          # 결제 후 후기, 별점 작성, 사용자/타인 상점의 후기 목록 조회
│     ├── 📂 payment               # 결제 기능, 구매내역 조회
│     ├── 📂 shop                  # 통합 정보 조회(회원, 상품, 찜, 팔로워/팔로잉)
│     ├── 📂 product               # 상품 등록, 검색, 수정, up, 숨기기/보이기, 
│     ├── 📂 util                  # 폴더 & 파일 생성 및 삭제, 직관적인 날짜 출력(ex: n일 전, n시간 전, 방금 전, n분 전)
│
├── 📂 src/main/resources
│     ├── 📂 static                # 정적 리소스 (CSS, JS, img, video)
│     ├── 📂 templates             # Thymeleaf 템플릿 (HTML 뷰)
│     ├── 📄 application.yml       # 환경 설정 파일
│     ├── 📄 application-oauth.yml # 소셜 로그인 환경 설정 파일
├── 📄 build.gradle                # 프로젝트 빌드 설정
├── 📄 README.md                   # 프로젝트 문서

```

## 📌 데이터 흐름 (Data Flow)
사용자가 요청을 보냄 → Controller가 요청을 받은 뒤 Service 계층 연결
Service 계층이 비즈니스 로직 처리, 필요 시 타 패키지 Service 참조 → Repository에서 DB 조회
DB에서 데이터 반환 후, 응답 생성 → Controller에서 JSON or 템플릿 이동 형태로 반환

[Client] - [Controller] - [DTO] - [Service] - [Entity] - [Repository] - [Database]
  
## 📌 개발 환경 설정 (Setup Instructions)

빌드 도구 설정
build.gradle 파일에 설정된 Gradle을 사용하여 의존성 패키지를 설치합니다.
```plaintext
./gradlew build
```

데이터베이스 설정
Oracle 데이터베이스 설정을 application.yml 파일에서 확인하고, 필요한 테이블을 생성합니다.
애플리케이션 실행 (Run the application)
Spring Boot 애플리케이션을 실행합니다.
```plaintext
./gradlew bootRun
```
웹 브라우저에서 접속
애플리케이션이 성공적으로 실행되면 웹 브라우저에서 http://localhost:8080을 통해 접속할 수 있습니다.

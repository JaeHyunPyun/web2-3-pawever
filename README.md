# Project : PAWEVER
> **프로그래머스 데브코스 2기 - 최종 프로젝트 Team10** <br/> **개발기간: 2025.2.10 ~ 2025.3.12**

<div align="center">
<img width="700" alt="image" src="https://github.com/user-attachments/assets/bd5696ff-ede9-4814-8cd3-26f0f87e838b">
</div>

<br/>

## 📢 배포 주소
> **PAWEVER 배포 버전** : <a href="https://pawever.netlify.app/"> PAWEVER </a> </br>
> - 소셜 로그인(Google/Kakao) 후 사용 가능

<br/>

## 🎀 프로젝트 소개
- 유기동물 입양 문화 확산과 인식 개선을 목표로, 입양 희망자와 보호소를 효과적으로 연결하는 플랫폼 서비스를 기획하였습니다.
- 전국 각지에 분산된 유기동물 보호소 정보를 통합 제공하고, 유기 동물 매칭 및 후원 등 편의 기능을 지원하여 입양 과정의 접근성과 편리성을 높였습니다.
- 입양 희망자뿐만 아니라 반려동물에 관심 있는 모든 사용자가 정보와 경험을 자유롭게 공유할 수 있는 커뮤니티를 구성하여, 유기동물 입양 활성화를 위한 참여 기반을 마련하였습니다.

<br/>

## 👐 개발팀 구성
### Front-End
| [안효태](https://github.com/Quokka3764)  | [임승현](https://github.com/SeungHyeon-web) | [우정완](https://github.com/WJoungWan)  | [윤정인](https://github.com/yoongjeonging) |
|---|---|---|---|
| <a href="https://github.com/Quokka3764"><img src="https://avatars.githubusercontent.com/u/179582796?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/SeungHyeon-web"><img src="https://avatars.githubusercontent.com/u/187251777?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/WJoungWan"><img src="https://avatars.githubusercontent.com/u/57696567?v=4" width="150px;" alt=""/> | <a href="https://github.com/yoongjeonging"><img src="https://avatars.githubusercontent.com/u/187367747?v=4" width="150px;" alt=""/></a> |
| PO(Project Owner)  | 팀장  | 팀원 | 팀원  |
| 아키텍처 및 기술 스택 선정, </br> 소셜로그인, </br> 입양동물찾기, </br> 필터링, </br> 후원 , </br> 유저페이지 , </br> 좋아요 , </br> 동물카드 제작  | 로그인 모달,</br> 애니멀보드 페이지,</br> 홈화면 퍼블리싱,</br> 매칭페이지 기능 구현  |사이트 디자인/퍼블리싱, </br> Header, Sidebar, Footer, </br> 공용 컴포넌트 제작 , </br> 모바일 환경 퍼블리싱 , </br> 동물 상세 페이지   | 카드 컴포넌트, </br> 매칭페이지, </br> 마이페이지 퍼블리싱, </br> 커뮤니티 페이지 기능 | 

<br/>

### Back-End
| [남주연](https://github.com/juyeon6069) | [강바다](https://github.com/202111255) | [김민영](https://github.com/myqewr) | [편재현](https://github.com/JaeHyunPyun) | [황규리](https://github.com/gyuri127) |
|---|---|---|---|---|
| <a href="https://github.com/juyeon6069"><img src="https://avatars.githubusercontent.com/u/97582404?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/202111255"><img src="https://avatars.githubusercontent.com/u/101125205?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/myqewr"><img src="https://avatars.githubusercontent.com/u/97663486?v=4" width="150px;" alt=""/> | <a href="https://github.com/JaeHyunPyun"><img src="https://avatars.githubusercontent.com/u/179315815?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/gyuri127"><img src="https://avatars.githubusercontent.com/u/117248176?v=4" width="150px;" alt=""/></a> |
| 팀장 | 팀원  | 팀원  | 팀원  | 팀원 |
| 클라우드 인프라 및 CI/CD 구축, </br> 후원&결제 API 개발, </br> AWS 관리 | Open API 및 KakaoMap API 데이터 처리, </br> 유기동물 API 개발, </br> 커뮤니티 게시글 API 개발 | 보호소 방문 예약 및 </br> 예약 조회 API 개발| 클라우드 인프라 및 CI//CD 구축, </br> 회원 인증/인가 API 개발, </br> CORS 설정, </br> 회원정보 조회 및 수정 API 개발 | 품종 매칭 및 지리 기반 추천 API 개발, </br> 유기동물 좋아요 API 개발, </br> 커뮤니티 댓글 API 개발 |

<br/>

## 🛠️ 기술스택
### ✨ Front-End ✨
#### Framework/Library
<div>
<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"/> 
<img src="https://img.shields.io/badge/React_Router-CA4245?style=for-the-badge&logo=react-router&logoColor=white"/> 
<img src="https://img.shields.io/badge/TailwindCSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white"/> 
<img src="https://img.shields.io/badge/Toast_UI-007ACC?style=for-the-badge"/>
</div>

#### State Management
<div>
<img src="https://img.shields.io/badge/TanStack_Query-FF4154?style=for-the-badge&logo=react-query&logoColor=white"/> <img src="https://img.shields.io/badge/Zustand-333333?style=for-the-badge"/>
</div>

#### Build & Package Management
<div>
<img src="https://img.shields.io/badge/PNPM-F69220?style=for-the-badge&logo=pnpm&logoColor=white"/>
</div>

#### API & Networking  
<div>
<img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge"/>
</div>

####  Animations & UI
<div>
<img src="https://img.shields.io/badge/Lottie-1A2C50?style=for-the-badge&logo=lottiefiles&logoColor=white"/>
</div>

<br/>

### ✨ Back-End ✨
#### Framework
<div>
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F.svg?&style=for-the-badge&logo=Hibernate&logoColor=white">
</div>

#### Infrastructure/Cloud
<div>
<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> </br>
<img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
</div>

#### Database/Cache
<div>
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"> <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
</div>

<br/>

### ✨ Communication ✨
<div>
<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white"/> 
<img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"/>  </br>
<img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"/> 
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/>
</div>

<br/>

## 🛠️ 화면구성
| 페이지명             | 주요 기능                                                        | 화면                                                                                                       |
|------------------|--------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| 메인 </br> 페이지     | 입양동물 매칭,</br> 후원하기,</br> 보호중인 동물 ,</br> 근처 동물                | <img width="700" src="https://github.com/user-attachments/assets/d6712833-2e72-4545-9df5-a5b9adedf362"/> |
| 입양동물찾기 </br> 페이지 | 보호중인동물,</br> 보호동물 상세페이지,</br> 조건 검색                          | <img width="700" src="https://github.com/user-attachments/assets/3c6eb552-731f-480e-85fa-b561c7e8b3cf"/>                                                                                |
| 커뮤니티 </br> 페이지   | 게시글 작성, </br> 댓글 작성, </br> 게시글 검색                            | <img width="700" src="https://github.com/user-attachments/assets/4bf1d368-8973-43f5-a8a3-103b9f934d4d"/>                                                                                |
| 후원 </br> 페이지     | 후원 정보 입력, </br> 후원 결제                                        | <img width="700" src="https://github.com/user-attachments/assets/6b5f56cc-55da-4610-b8f8-a8dc26c549fc"/>                                                                                |
| 마이 </br> 페이지     | 프로필 수정, </br> 좋아요 동물 조회, </br> 작성글 조회, </br> 후원내역 조회         | <img width="700" src="https://github.com/user-attachments/assets/83847541-49a6-4792-8bbc-d07ed9c3b94b"/>                                                                                |
| 매칭 </br> 페이지     | 매칭 동물 선택, </br> 질문 페이지, </br> 매칭 결과 페이지                      | <img width="700" src="https://github.com/user-attachments/assets/4ac84f1d-6b49-4a70-b804-0228d918f049"/>                                                                                |

<br/>

## 📦 주요 기능
### 입양 동물 매칭
- 사용자의 라이프스타일과 선호도를 반영한 간단한 매칭 테스트를 통해, 성향이 잘 맞는 강아지 또는 고양이를 추천하여 입양 성공률을 높임
- 사용자가 라이프스타일 관련 질문에 응답하면, 해당 응답을 기반으로 반려동물 선호 특성을 도출하고, 이를 보호 중인 동물의 특성과 비교하여 매칭 점수를 산출

### 위치 기반 근처 동물 추천
- 사용자 위치 정보를 기반으로, 가까운 보호소에서 보호 중인 동물을 노출
- Haversine 알고리즘을 활용해 지구 곡률을 고려한 정확한 거리 계산을 수행하고, 사용자 주변 보호소 내 추천 품종과 유사한 유기동물을 찾아 제안

### 후원, 결제
- TossPayments API를 활용하여 간편하고 안전한 결제 시스템을 구축, 후원을 희망하는 사용자가 쉽고 편리하게 후원할 수 있도록 지원
- 카드 결제 및 계좌 이체를 통한 후원 기능 제공

### 보호소 정보 조회
- 보호소에서 보호 중인 동물 목록 조회 및 보호소 연락처, 위치 정보 제공
- 카카오맵 API를 활용해 보호소의 위도·경도 데이터를 불러와 보호소 정보와 매핑
- 비동기 호출 및 스케줄러를 도입하여 보호소 데이터를 매일 자동 업데이트

### 조건 기반 필터 검색 
- 축종, 성별, 나이, 검색어 입력을 통해 사용자가 원하는 조건의 동물을 쉽고 빠르게 검색할 수 있도록 지원
- JPA Specification을 활용해 품종, 지역, 보호소 ID, 성별, 나이, 검색어 등 다양한 필터링 조건 구현

### 커뮤니티
- 사용자가 입양 동물 및 보호소 관련 정보나 경험을 게시글로 자유롭게 공유하고, 댓글을 통해 소통할 수 있도록 하여 유기동물 입양 커뮤니티 활성화
- 게시글 이미지 업로드 시 Multipart를 활용하고, S3Client를 이용해 AWS S3에 파일 업로드 및 삭제 처리

### 소셜 로그인
- OAuth 2.0 및 JWT를 이용한 소셜 로그인(구글, 카카오) 기능 구현
- 토큰 탈취 감지 시 모든 기기에서 자동 로그아웃 처리 기능 제공
- 사용자가 최근 접속 IP와 다른 IP로 로그인할 경우 보안 알림 메일 자동 전송 기능 구현

### 마이 페이지
- 사용자 프로필 이미지, 이름, 소개글 등 개인정보 수정 기능 제공
- 사용자가 좋아요 표시한 입양 동물, 작성한 게시글, 후원 내역 조회 기능 제공

<br/>

## 🏗️ 아키텍처
### Infrastructure
<div align="center">
<img width="700" alt="image" src="https://github.com/user-attachments/assets/992af752-0925-4d88-9d0c-d5e2f3ae07e8">
</div>

### Project Structure

외부 라이브러리, 인증, 인프라, 공통 엔티티 등은 common 패키지로 통합 관리하고, 핵심 도메인 비즈니스 로직은 domain 패키지 내부에서 독립적으로 구성

```
src/main/java/com/pawever/server/
├── common/
│   ├── config/            # 전역 설정
│   ├── entity/            # 공통 엔티티
│   ├── exception/         # 커스텀 에외 등록
│   ├── handler/           # 예외 처리
│   ├── infra/             # 외부 의존성 추가
│   └── response/          # API 공통 응답 형식
├── domain/
│   ├── carehub/           # 유기동물, 보호소 데이터 조회
│   ├── community/         # 댓글 기능
│   ├── donation/          # 후원, 결제
│   ├── likedpet/          # 좋아요
│   ├── post/              # 게시글 기능
│   ├── recommendation/    # 추천, 매칭
│   ├── reservation/       # 보호소 예약
│   └── user/              # 사용자 인증, 인가, 정보 조회
└── PawEverApplication.java
```

<br/>

### ERD
<div align="center">
<img width="700" alt="image" src="https://github.com/user-attachments/assets/c113c1c2-0b1e-43e0-b9a3-b53cbbd00393">
</div>
# Project : PAWEVER
> **프로그래머스 데브코스 2기 - 최종 프로젝트 Team10** <br/> **개발기간: 2025.2.10 ~ 2025.3.12**

<div align="center">
<img width="700" alt="image" src="https://github.com/user-attachments/assets/bd5696ff-ede9-4814-8cd3-26f0f87e838b">
</div>

<br/>

## 📢 배포 주소
> **PAWEVER 배포 버전** : <a href="https://pawever.netlify.app/"> PAWEVER </a> </br>
> - 소셜 로그인(Google/Kakao) 후 사용 가능

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


### Back-End
| [남주연](https://github.com/juyeon6069) | [강바다](https://github.com/202111255) | [김민영](https://github.com/myqewr) | [편재현](https://github.com/JaeHyunPyun) | [황규리](https://github.com/gyuri127) |
|---|---|---|---|---|
| <a href="https://github.com/juyeon6069"><img src="https://avatars.githubusercontent.com/u/97582404?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/202111255"><img src="https://avatars.githubusercontent.com/u/101125205?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/myqewr"><img src="https://avatars.githubusercontent.com/u/97663486?v=4" width="150px;" alt=""/> | <a href="https://github.com/JaeHyunPyun"><img src="https://avatars.githubusercontent.com/u/179315815?v=4" width="150px;" alt=""/></a> | <a href="https://github.com/gyuri127"><img src="https://avatars.githubusercontent.com/u/117248176?v=4" width="150px;" alt=""/></a> |
| 팀장 | 팀원  | 팀원  | 팀원  | 팀원 |
| 클라우드 인프라 및 CI/CD 구축, </br> 후원&결제 API 개발, </br> AWS 관리 | Open API 및 KakaoMap API 데이터 처리, </br> 유기동물 API 개발, </br> 커뮤니티 게시글 API 개발 | 보호소 방문 예약 및 </br> 예약 조회 API 개발| 클라우드 인프라 및 CI//CD 구축, </br> 회원 인증/인가 API 개발, </br> CORS 설정, </br> 회원정보 조회 및 수정 API 개발 | 품종 매칭 및 지리 기반 추천 API 개발, </br> 유기동물 좋아요 API 개발, </br> 커뮤니티 댓글 API 개발 |

<br/>

## 🛠️ 기술스택
### ✨ Front-End ✨
***
#### Framework/Library
<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"/> <img src="https://img.shields.io/badge/React_Router-CA4245?style=for-the-badge&logo=react-router&logoColor=white"/> 
<img src="https://img.shields.io/badge/TailwindCSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white"/> <img src="https://img.shields.io/badge/Toast_UI-007ACC?style=for-the-badge"/>

#### State Management
<img src="https://img.shields.io/badge/TanStack_Query-FF4154?style=for-the-badge&logo=react-query&logoColor=white"/> <img src="https://img.shields.io/badge/Zustand-333333?style=for-the-badge"/>

#### Build & Package Management
<img src="https://img.shields.io/badge/PNPM-F69220?style=for-the-badge&logo=pnpm&logoColor=white"/>

#### API & Networking  
<img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge"/>

####  Animations & UI
<img src="https://img.shields.io/badge/Lottie-1A2C50?style=for-the-badge&logo=lottiefiles&logoColor=white"/>

### ✨ Back-End ✨
***
#### Framework
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F.svg?&style=for-the-badge&logo=Hibernate&logoColor=white">

#### Infrastructure/Cloud
<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> 
<img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> </br>
<img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> 
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> 
<img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">

#### Database/Cache
<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"> 
<img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">

### ✨ Communication ✨
***
<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white"> 
<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white"/> 
<img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"/>  </br>
<img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"/> 
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/>
<br/>

## 🛠️ 화면구성
| 페이지명                      | 주요 기능                                                                     | 화면                                                                                                       |
|---------------------------|---------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| 메인 </br> 페이지              | 입양동물 매칭,</br> 후원하기,</br> 보호중인 동물 ,</br> 근처 동물   | <img width="700" src="https://github.com/user-attachments/assets/d6712833-2e72-4545-9df5-a5b9adedf362"/> |
| 관리자 </br> 페이지             | 웹사이트 통계 조회, </br> 사용자 계정 정보 검색, </br> 관리자 권한 댓글 검색 및 삭제                   | <img width="550" src=""/>                                                                                |
| 사용자 </br> 인증              | ID 및 PW 기반 로그인, </br> 로그아웃, </br> 회원탈퇴, </br> 비밀번호 수정, </br> Kakao 소셜 로그인 | <img width="550" src=""/>                                                                                |
| 마이 </br> 페이지              | 사용자 계정 및 </br> 즐겨찾기 정보 조회                                                 | <img width="550" src=""/>                                                                                |
| 인기 칵테일 </br> 추천           | 좋아요 기반 </br> 인기 칵테일 조회                                                    | <img width="550" src=""/>                                                                                |
| 칵테일 </br> 추천              | Open-AI API를 활용한 </br> 지식 기반 추천                                           | <img width="550" src=""/>                                                                                |
| 칵테일 </br> 검색              | 칵테일명 또는 </br> 재료 기반 검색, <br/> 조회수 또는 좋아요수 기반 </br> 인기 검색어 추천              | <img width="550" src=""/>                                                                                |
| 좋아요, </br> 즐겨찾기, </br> 댓글 | 좋아요, 즐겨찾기, 댓글 </br> 조회, 등록, 삭제                                            | <img width="550" src=""/>                                                                                |


## 📦 주요 기능
### 칵테일 추천 기능
- OpenAI API를 활용하여 간단한 취향 질문을 통해 사용자의 칵테일 및 술자리 선호를 파악한 뒤, 맞춤형 칵테일 추천
- 인증된 사용자의 '좋아요' 데이터를 기반으로 인기 칵테일 랭킹 제공

### 칵테일 검색 기능
- CocktailDB API를 활용하여 칵테일 이름 또는 재료 기반 검색 기능 제공
- 인증된 사용자의 '좋아요' 및 '즐겨찾기' 데이터를 기반으로 인기 검색어 추천

### 좋아요, 즐겨찾기, 댓글 기능
- 인증된 사용자의 '좋아요', '즐겨찾기' 및 '댓글' 등록 및 삭제 기능 제공
- '즐겨찾기'한 칵테일은 마이페이지에서 조회 가능

### 소셜 로그인
- 아이디 및 비밀번호 기반 인증 방식과 더불어 OAuth 2.0 인증을 통해 Kakao 소셜 로그인 기능 구현
- 사용자 정보를 세션에 저장하여 로그인 상태 유지

### 관리자 페이지
- 전체 유저 목록 조회 및 특정 유저 삭제 기능 제공
- 유저 이메일 또는 이름 기준으로 특정 유저 검색 가능
- 전체 댓글 목록 조회 및 특정 댓글 삭제 기능 제공
- 댓글 내용 키워드를 활용한 댓글 검색 기능 지원
- 대시보드를 통해 인기 칵테일 TOP 3, 유저 및 조회수 및 댓글 증가율 등 주요 지표 시각화 및 통계 데이터 제공

## 🏗️ 아키텍처
### 프로젝트 아키텍처
<div align="center">
<img width="700" alt="image" src="https://github.com/user-attachments/assets/4021572d-60d0-42ce-978e-75dc94d05af3">
</div>

### ERD
<div align="center">
<img width="700" alt="image" src="https://github.com/user-attachments/assets/72a4cfb6-0f5a-43b7-8900-60763f0c1438">
</div>
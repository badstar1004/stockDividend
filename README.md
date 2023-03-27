# stockDividend
stockDividend(주식배당금) 주식 배당금 서비스 (학습용 프로젝트)

# 기술 & 툴
- Java jdk 11
- H2 DataBase(memory DB 모드)
- Spring Boot 2.5.6, gradle
- JAP, web, Redis, security, jsoup 1.15.3, commons-collections4 4.4, jjwt 0.9.1, Lombok
- IntelliJ Ultimate

# 개발 기간
- 2023.03.20 ~ 2023.03.27

# 주요 기능
미국 주식 배당금 정보 제공 API
- 회원가입 기능
- 로그인 기능 (JWT 활용)
- 회사와 배당금 정보 등록, 수정, 삭제 기능 (스크래핑 기법 활용)
- 회사 검색 시 자동완성 기능 (10개 반환)
- 캐시 기능 (Redis 활용)

***

# 프로젝트 후기
> - 오타와 오류들의 싸움이였다.
> - 예외처리 클래스를 만들어서 사용해본 것과 jwt 발행, 캐시에 올리는 기능들이 신기했다.
> - Redis 설정과 jwt 발행부분을 좀 더 알아야할 것 같다.

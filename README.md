# KPP-Gateway

### 개요
Spring Cloud Gateway 를 통해 로드 밸런싱과 JWT 를 통한 인증을 담당하는 서비스

### Filter
* JwtTokenFilter: JWT Token 이 유효한지 검사
* RoleFilter: 특정 회원 등급 제한을 위한 필터. 상속받아서 사용
  * AdminRoleFilter: [ADMIN] 등급 요청 거부
  * UserRoleFilter: [USER] 등급 요청 거부
* MaintainPortFilter: 웹 서비스시 redirect 하면 original port 로 돌아가는 문제를 해결하기 위한 필터



package kr.co.tj.memberservice.sec;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
		.cors().and() //CORS 설정 Cross-Origin Resource Sharing (CORS)를 활성화하여 다른 도메인에서의 요청을 허용하기 위함
		.csrf().disable() //CSRF 설정 Cross-Site Request Forgery (CSRF) 공격을 방지하기 위한 설정을 해제
		.httpBasic().disable() //HTTP 기본 인증 설정 HTTP 기본 인증을 비활성화하여 사용자 이름과 암호를 사용하여 인증하는 기본 방식을 사용하지 않음
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
		//세션 관리 설정:STATELESS => 세션을 사용하지 않는 상태로 설정, RESTful API와 같이 세션을 필요로 하지 않는 상황에서 사용
		
		// 인증과 인가의 범위는 apigateway의 application.yml에 설정합니다.
		http.authorizeRequests().antMatchers("/actuator/**").permitAll();
		//접근 제어 설정(모두허용), .yml 파일에 include: refresh, health, beans, busrefresh 
		http.authorizeRequests().antMatchers("/member-service/members/**").permitAll();//접근 제어 설정(모두허용)
		
		http.headers().frameOptions().disable(); //프레임 옵션 설정
	}

	
}



//*** CSRF(Cross-Site Request Forgery) 공격?
// 인증된 사용자의 브라우저를 이용하여 악성 요청을 보내어 특정 동작을 수행하는 공격
//.csrf().disable() CSRF 공격방지 기능 비활성화(CSRF 토큰을 체크하지 않고 모든 요청을 처리함)
//-> 추가 보안 대책(토큰 기반 방어: CSRF 토큰을 사용, HTTPS 사용)
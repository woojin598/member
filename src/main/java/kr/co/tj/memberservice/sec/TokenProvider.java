package kr.co.tj.memberservice.sec;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.co.tj.memberservice.dto.MemberEntity;


// 로그인 작업 후 클라이언트에게 토큰을 반환하는 과정
@Component
public class TokenProvider {
	
	// bootstrap.yml에 SECRETE_KEY입력한 값을 가져오기 위한 작업
	private Environment env;
	
	@Autowired
	public TokenProvider(Environment env) {
		super();
		this.env = env;
	}
	

	// 클라이언트가 토큰을 넘겨주면, SECRET_KEY로 파싱을 합니다. 그러면 header + payload가 남습니다.
	// 클라이언트가 넘겨준 header와 payload를 비교해서 같은지 여부를 확인함...
	public String create(MemberEntity memberEntity) { //MemberEntity객체를 기반으로 JWT토큰 생성. 
		
		System.out.println("토큰111111111111111111111111");

		// 시간을 담기 위한 메서드
		long now = System.currentTimeMillis();		
		Date today = new Date(now); //현재시간
		
		//Date exireDate = new Date(now + 1000 * 1 * 60 * 60 * 24); // 24시간
		Date expire = Date.from(Instant.now().plus(1, ChronoUnit.HOURS)); //현재시간으로 부터 1시간 후

		
		
		//토큰을 생성하면서 아래의 정보를 빌드함
		return Jwts.builder() 
				.signWith(SignatureAlgorithm.HS512, env.getProperty("data.SECRETE_KEY"))//서명을 추가
				.setSubject(memberEntity.getUsername()) //이름
				.setIssuer("member-service") //발급자
				.setIssuedAt(today) //발생일
				//.setExpiration(exireDate) //토큰의 만료일
				.setExpiration(expire)
				//.claim("authority", "ROLE_ADMIN")
				.claim("authority",memberEntity.getRole()) //로그인시 권한
				.compact();

	}

}



// ***현재 시간을 담기 위한 방법
// 1. 발급된 순간을 미리 계산하여 저장(일관된 시간을 지정하기 위한용도로 사용 가능)
//long now = System.currentTimeMillis();		
//Date today = new Date(now);
//Date exireDate = new Date(now + 1000 * 1 * 60 * 60 * 24);  //1000 * 1 = 1초 * 60 = 1분 * 60 = 1시간 * 24 = 하루

//2. 발급된 순간을 저장
//Date expire = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
//.setIssuedAt(new Date())


// *** 토큰과 서명
//JWT는 헤더(Header), 페이로드(Payload), 서명(Signature)으로 구성
//.signWith(SignatureAlgorithm.HS512, SECRETE_KEY) // JWT 토큰에 SignatureAlgorithm.HS512알고리즘을 사용하여 서명을 추가
//서명은 헤더와 페이로드를 조합하여 암호화하여 생성되며, 이를 통해 토큰의 무결성을 보장
//SignatureAlgorithm.HS512는 HMAC-SHA512 알고리즘을 사용하여 서명을 생성하고 검증하는 대칭키 알고리즘
//SECRET_KEY는 토큰 서명에 사용되는 비밀키(서버와 클라이언트가 동일한 값을 가져야함_ 서명의 무결성을 위해 안전하게 보관되어야함)
//*****config 서버에 저장해 놓은 시크릿 키를 member-service의 bootstrap.yml에 가져오는 설정을 추가함.
//member-service 실행(bootstrap.yml이 이전에 실행됨) -> 로그인 -> 토큰발행(bootstrap.yml의 config서버의 시크릿키 가져오기 동작을 수행함)


//1. 클라이언트 [로그인 , 인증 요청] -> 서버 [클라이언트의 신원확인 후 토큰생성] 서버는 비밀 키(SECRETE_KEY)를 사용해 토큰에 서명
//2. 서버 [토큰전달(응답)] -> 클라이언트 [투큰수신]
//3. 클라이언트 [요청](헤더나 쿠키를 통해) -> 서버[수신] 비밀키를 통해검증 및 유효성 확인 후 필요한 권한 및 인증 정보를 추출하여 요청을 처리


















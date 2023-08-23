package kr.co.tj.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdatePasswdRequest{ //비밀번호 수정

	private String username;
	private String password;
	private String password2;
	private String orgPassword;
	
}

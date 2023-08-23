package kr.co.tj.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequest { //toMemberDTO

	private String username;
	private String name;
	
	private String gender;

	private String email;
	private String phonenum;
	private String address;

	private String password;
	//private String password2;
	//private String orgPassword; // 회원정보 수정 전 비밀번호 재확인 구현 예정

}

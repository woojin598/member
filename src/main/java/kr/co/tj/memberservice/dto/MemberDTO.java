package kr.co.tj.memberservice.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String username;// 사용자 실제 아이디
	
	private String gender;

	private String name;

	private String email;
	private String phonenum;
	private String address;

	private String password; //신규 비밀번호
	private String password2; //신규 비밀번호 확인
	private String orgPassword; //기존 비밀번호

	private Date createAt;

	private Date updateAt;
	
	private List<OrderResponse> orderList;
	
	private String role;

	private String token;
	


	// 회원정보수정(비밀번호)
	public static MemberDTO toMemberDTOPasswd(MemberUpdatePasswdRequest updatePasswdRequest) {
		return MemberDTO.builder()
				.password(updatePasswdRequest.getPassword())
				.build();
	}
	
	// 회원가입(+시간+pw암호화)
	// 회원정보수정
	public MemberDTO toMemberDTO(MemberEntity memberEntity) {
		this.id = memberEntity.getId();
		this.username = memberEntity.getUsername();
		this.name = memberEntity.getName();
		this.gender = memberEntity.getGender();
		this.email = memberEntity.getEmail();
		this.phonenum = memberEntity.getPhonenum();
		this.address = memberEntity.getAddress();
		this.createAt = memberEntity.getCreateAt();
		this.updateAt = memberEntity.getUpdateAt();
		this.role = memberEntity.getRole();
		this.token = memberEntity.getToken();

		return this;
	}

	   

	// 회원가입(+시간)
	public MemberEntity toMemberEntity() {
		return MemberEntity.builder()
				.id(id)
				.username(username)
				.name(name)
				.gender(gender)
				.email(email)
				.phonenum(phonenum)
				.address(address)
				.password(password)
				.createAt(createAt)
				.updateAt(updateAt)
				.role(role)
				.token(token)
				.build();
	}

	// 회원가입
	public static MemberDTO toMemberDTO(MemberRequest memberRequest) {
		return MemberDTO.builder()
				.username(memberRequest.getUsername())
				.name(memberRequest.getName())
				.gender(memberRequest.getGender())
				.email(memberRequest.getEmail())
				.phonenum(memberRequest.getPhonenum())
				.address(memberRequest.getAddress())
				.password(memberRequest.getPassword())
				.build();
	}

	// 로그인
	public MemberResponse toMemberResponse() {
		return MemberResponse.builder()
				.username(username)
				.name(name)
				.createAt(createAt)
				.updateAt(updateAt)
				.orderList(orderList)
				.token(token)
				.role(role)
				.build();
	}

	// 로그인
	public static MemberDTO toMemberDTO(MemberLoginRequest memberLoginRequest) {
		return MemberDTO.builder()
				.username(memberLoginRequest.getUsername())
				.password(memberLoginRequest.getPassword())
				.build();
	}



	// 비밀번호 수정
	public static MemberDTO toMemberDTO(MemberUpdatePasswdRequest updatePasswdRequest) {
		return MemberDTO.builder()
				.password(updatePasswdRequest.getPassword())
				.build();
	}




}

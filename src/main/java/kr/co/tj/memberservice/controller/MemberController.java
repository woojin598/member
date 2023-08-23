package kr.co.tj.memberservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.tj.memberservice.dto.MemberDTO;
import kr.co.tj.memberservice.dto.MemberLoginRequest;
import kr.co.tj.memberservice.dto.MemberRequest;
import kr.co.tj.memberservice.dto.MemberResponse;
import kr.co.tj.memberservice.dto.MemberUpdatePasswdRequest;
import kr.co.tj.memberservice.service.MemberService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/member-service")
@Slf4j // 로그를 찍게함
public class MemberController {

	@Autowired
	private Environment env;

	@Autowired
	private MemberService memberService;

	// 주문목록
	@GetMapping("{username}/orders")
	public ResponseEntity<?> getOrders(@PathVariable("username") String username) {
		MemberDTO memberDTO = memberService.getOrders(username);
		MemberResponse memberResponse = memberDTO.toMemberResponse();

		return ResponseEntity.status(HttpStatus.OK).body(memberResponse);

	}

	// 목록(페이징)
	@GetMapping("/list")
	public ResponseEntity<?> list(int pageNum) {
		Map<String, Object> map = new HashMap<>();
		Page<MemberDTO> page = memberService.findAll(pageNum);
		map.put("result", page);

		return ResponseEntity.ok().body(map);
	}

	// 목록
	@GetMapping("/all")
	public ResponseEntity<?> findAll() {
		Map<String, Object> map = new HashMap<>();

		try {
			List<MemberDTO> list = memberService.findAll();
			map.put("list", list);

			return ResponseEntity.ok().body(map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("err", "회원정보 가져오기를 실패했습니다.");
			return ResponseEntity.badRequest().body(map);
		}
	}

	// 삭제
	@DeleteMapping("member/delete")
	public ResponseEntity<?> delete(@RequestBody MemberDTO dto) {
		Map<String, Object> map = new HashMap<>();

		if (dto == null) {
			map.put("result", "잘못된 정보입니다");
			return ResponseEntity.badRequest().body(map);
		}

		try {
			memberService.delete(dto);
			map.put("result", "삭제 성공");

			return ResponseEntity.ok().body(map);

		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", "삭제 실패"); // 에러 500
			return ResponseEntity.badRequest().body(map);
		}
	}

	// 비밀번호 수정
	@PutMapping("member/updatePasswd/{username}")
	public ResponseEntity<?> updatePasswd(@RequestBody MemberUpdatePasswdRequest updatePasswdRequest) {
		Map<String, Object> map = new HashMap<>();

		// user가 입력한 [기존] password 확인
		if (updatePasswdRequest.getOrgPassword() == null || updatePasswdRequest.getOrgPassword().isEmpty()) {
			map.put("result", "비밀번호를 바르게 입력하세요.1");
			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 [신규] password 확인
		if (updatePasswdRequest.getPassword() == null || updatePasswdRequest.getPassword().isEmpty()) {
			map.put("result", "비밀번호를 바르게 입력하세요.2");
			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 [신규] password 확인
		if (updatePasswdRequest.getPassword2() == null || updatePasswdRequest.getPassword2().isEmpty()) {
			map.put("result", "비밀번호를 바르게 입력하세요.3");
			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 [신규] password 확인
		if (!updatePasswdRequest.getPassword().equals(updatePasswdRequest.getPassword2())) {
			map.put("result", "비밀번호를 바르게 입력하세요.4");

			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 [신규] password 확인
		if (updatePasswdRequest.getPassword() == updatePasswdRequest.getOrgPassword()) {
			map.put("result", "기존 비밀번호와 동일합니다.5");
			return ResponseEntity.badRequest().body(map);
		}

		System.out.println(updatePasswdRequest);

		// MemberDTO memberDTO = MemberDTO.toMemberDTOPasswd(updatePasswdRequest);
		// System.out.println(memberDTO);

		try {
			MemberDTO memberDTO = new MemberDTO();
			memberDTO = memberService.updatePasswd(updatePasswdRequest); // memberRequest 수정한 값을 다시 변수에 담음.
			map.put("result", memberDTO);

			return ResponseEntity.ok().body(map);

		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", "수정 실패");

			return ResponseEntity.badRequest().body(map);
		}
	}

	// 회원정보 수정logs/productid
	@PutMapping("member/update")
	public ResponseEntity<?> update(@RequestBody MemberRequest memberRequest) {
		Map<String, Object> map = new HashMap<>();

		if (memberRequest == null) {
			map.put("result", "바르게 입력하세요.");
			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 name(id)확인
		if (memberRequest.getName() == null) {
			map.put("result", "id를 바르게 입력하세요.");
			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 password 확인
		// if (memberRequest.getPassword() == null ||
		// memberRequest.getPassword().isEmpty()) {
		// map.put("result", "비밀번호를 바르게 입력하세요.");
		// return ResponseEntity.badRequest().body(map);
		// }

		// user가 입력한 phonenum 확인
		if (memberRequest.getPhonenum() == null || memberRequest.getPhonenum().equals("")) {
			map.put("result", "연락처를 바르게 입력하세요.");
			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 email 확인
		if (memberRequest.getEmail() == null || memberRequest.getEmail().equals("")) {
			map.put("result", "e-mail을 바르게 입력하세요.");
			return ResponseEntity.badRequest().body(map);
		}

		// user가 입력한 address 확인
		if (memberRequest.getAddress() == null || memberRequest.getAddress().equals("")) {
			map.put("result", "주소를 바르게 입력하세요.");
			return ResponseEntity.badRequest().body(map);
		}

		MemberDTO memberDTO = MemberDTO.toMemberDTO(memberRequest);

		try {
			memberDTO = memberService.update(memberDTO);
			map.put("result", memberDTO);

			return ResponseEntity.ok().body(map);

		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", "수정 실패");

			return ResponseEntity.badRequest().body(map);
		}
	}

	// 회원정보 자세히보기
	@GetMapping("/member/name/{username}")
	public ResponseEntity<?> findByUsername(@PathVariable("username") String username) {
		Map<String, Object> map = new HashMap<>();

		if (username == null) {
			map.put("result", "잘못된 정보입니다");
			return ResponseEntity.badRequest().body(map);
		}

		try {
			MemberDTO dto = memberService.findByUsername(username);
			map.put("result", dto);
			return ResponseEntity.ok().body(map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", "조회 실패");
			return ResponseEntity.badRequest().body(map);

		}
	}

	// 로그인
	@PostMapping("all/login")
	public ResponseEntity<?> login(@RequestBody MemberLoginRequest memberLoginRequest) {
		Map<String, Object> map = new HashMap<>();
		
		System.out.println("11111로그인 입력값이 잘 들어왔나?:"+memberLoginRequest);

		if (memberLoginRequest.getUsername() == null || memberLoginRequest.getUsername().isEmpty()) {
			map.put("result", "id를 바르게 입력하세요.");
			return ResponseEntity.ok().body(map);
		}

		if (memberLoginRequest.getPassword() == null || memberLoginRequest.getPassword().isEmpty()) {
			map.put("result", "비밀번호를 바르게 입력하세요.");
			return ResponseEntity.ok().body(map);
		}

		MemberDTO memberDTO = MemberDTO.toMemberDTO(memberLoginRequest); // dto로 변환
		memberDTO = memberService.login(memberDTO);
		map.put("result", memberDTO);

		if (memberDTO == null) {
			map.put("result", "사용자명이나 비밀번호가 잘못 되었습니다.");
			return ResponseEntity.ok().body(map);
		}

		MemberResponse memberResponse = memberDTO.toMemberResponse();
		map.put("result", memberResponse);
		
		System.out.println("2222로그인 입력값이 잘 들어왔나?:"+memberResponse);

		return ResponseEntity.ok().body(map);
	}

	@GetMapping("/test")
	public ResponseEntity<?> test() {
		System.out.println("토큰 첨부용 test 메서드 입니다..");

		return ResponseEntity.status(HttpStatus.OK).body(new MemberResponse());
	}

	// 회원가입
	@PostMapping("all/members")
	public ResponseEntity<?> createMember(@RequestBody MemberRequest memberRequest) {
		MemberDTO memberDTO = MemberDTO.toMemberDTO(memberRequest); // req -> dto

		memberDTO = memberService.createMember(memberDTO); // dto -> entity
		MemberResponse memberResponse = memberDTO.toMemberResponse();

		return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
	}

	@GetMapping("/health_check")
	public String status() {
		log.info("data odrders: {}", env.getProperty("data.url")); // 오더의 url 확인중

		return "member-service입니다" + env.getProperty("local.server.port");

	}

}

package kr.co.tj.memberservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import kr.co.tj.memberservice.dto.MemberDTO;
import kr.co.tj.memberservice.dto.MemberEntity;
import kr.co.tj.memberservice.dto.MemberUpdatePasswdRequest;
import kr.co.tj.memberservice.jpa.MemberRepository;
import kr.co.tj.memberservice.sec.TokenProvider;
import kr.co.tj.memberservice.dto.OrderResponse;


@Service
public class MemberService {
	
	@Autowired
	private Environment env; //order를 가져오기위해.

	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder; 
	
	
	
	

	//주문목록
	public MemberDTO getOrders(String username) {

		MemberEntity memberEntity = memberRepository.findByUsername(username);//username을 가져옴
		
		if(memberEntity == null) {
			throw new RuntimeException("존재하지 않는 사용입니다.");
		}

		MemberDTO memberDTO = new MemberDTO();
		memberDTO = memberDTO.toMemberDTO(memberEntity); //db의 username을 dto로 변환
		
		// 1. 서비스간의 통신: feign 이용해서 통신한 코드
		//List<OrderResponse> orderList = orderFeign.getOrdersByUsername(username);
			
		System.out.println("오더의url을 확인합니다!!!!!!!!!!"+env.getProperty("data.url"));
		
		// 2. 서비스간의 통신: RestTemplate 이용해서 통신		
		String orderUrl = String.format(env.getProperty("data.url"),username);//order의 url를 통해 username을 가져옴.
		ResponseEntity<List<OrderResponse>> result = restTemplate.exchange(orderUrl, 
																	HttpMethod.GET, 
																	null, 
							new ParameterizedTypeReference<List<OrderResponse>>() {
		});
		
		
		List<OrderResponse> orderList = result.getBody();
			
		memberDTO.setOrderList(orderList);
		memberDTO.setToken("");
		memberDTO.setRole("");
		memberDTO.setId("");
		memberDTO.setCreateAt(null);
		memberDTO.setUpdateAt(null);
		memberDTO.setPassword("");	
					
		return memberDTO;
	}

	
	
	//회원가입
	@Transactional
	public MemberDTO createMember(MemberDTO memberDTO) {
		memberDTO = getDate(memberDTO); //시간추가
		memberDTO = resolveRole(memberDTO);//권한추가
		MemberEntity memberEntity = memberDTO.toMemberEntity();// dto -> entity로 변환
		memberEntity.setPassword( passwordEncoder.encode(memberEntity.getPassword()) ); //패스워드 암호화
		memberEntity = memberRepository.save(memberEntity); // 암호화한 정보를 DB에 저장
		

//		item과 연결될 예정(값을 연결)respond에 보낼 값 수정하기 ★feign 작업예정입니다
//		MemberResponse memberResponse = memberDTO.toMemberResponse();
//		String result = catalogFeign.updateStockByProductId(memberResponse);	
//		if(result.startsWith("0")){
//			memberRepository.delete(memberEntity);
//			
//			return null;
//		}
		
		
		return memberDTO.toMemberDTO(memberEntity); // DB의 값을 다시 DTO로 변환하여 반환
	}
	
	 
	//회원가입, 회원수정(현재시간등록)
	private MemberDTO getDate(MemberDTO memberDTO) {
		Date date = new Date();
		if (memberDTO.getCreateAt() == null) {
			memberDTO.setCreateAt(date);
		}
		memberDTO.setUpdateAt(date);
		return memberDTO;
	}
	
	
	
	//회원가입, 회원수정(기본 user 권한부여)
	private MemberDTO resolveRole(MemberDTO memberDTO) {
	    if (memberDTO.getRole() == null) {
	        memberDTO.setRole("ROLE_USER");
	    }
	    return memberDTO;
	}

	
	//로그인
	@Transactional
	public MemberDTO login(MemberDTO memberDTO) {
		
		//사용자 인증(username, password의 일치여부)
		MemberEntity memberEntity = memberRepository.findByUsername(memberDTO.getUsername());
		if(memberEntity == null) {
			return null;
		}
				
		if(!passwordEncoder.matches(memberDTO.getPassword(), memberEntity.getPassword())) {
			return null;
		}
		
		
		String token = tokenProvider.create(memberEntity);//토큰생성(서버에 저장)
		
		
		//db에 로그인 시 발행되는 token과 role값 추가하기
		memberEntity.setToken(token);
		memberEntity = memberRepository.save(memberEntity);

		memberDTO = memberDTO.toMemberDTO(memberEntity);
		memberDTO.setToken(token); // dto에 token삽입
		memberDTO.getRole();; // dto에 role삽입
		memberDTO.setId("");
		memberDTO.setPassword("");

		return memberDTO; //id, pw를 null, 토큰값 반환
	}

	//회원정보 자세히 보기
	@Transactional
	public MemberDTO findByUsername(String username) {

		MemberEntity memberEntity = memberRepository.findByUsername(username);
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.toMemberDTO(memberEntity);
		memberDTO.setId("");
		memberDTO.setPassword("");
		
		return memberDTO;
	}

	

	// 회원정보 수정 화면 전 비밀번호 확인
	
	
	
	//회원정보 수정
	@Transactional
	public MemberDTO update(MemberDTO memberDTO) {
		MemberEntity memEntity = memberRepository.findByUsername(memberDTO.getUsername());

		if (memEntity == null) {
			throw new RuntimeException("회원 정보가 잘못됐습니다..");
		}
		
//		if(!passwordEncoder.matches(memberDTO.getPassword(), memEntity.getPassword())) {
//			throw new RuntimeException("비밀번호가 틀렸습니다.");
//		}
		
		memEntity.setName(memberDTO.getName());
		memEntity.setEmail(memberDTO.getEmail());
		//memEntity.setPassword(passwordEncoder.encode(memberDTO.getPassword())); // 비밀번호 따로 구현
		memEntity.setPhonenum(memberDTO.getPhonenum());
		memEntity.setAddress(memberDTO.getAddress());
		memEntity.setUpdateAt(new Date());
		
		memEntity.getPassword();

		memEntity = memberRepository.save(memEntity);
		
		memberDTO = memberDTO.toMemberDTO(memEntity);
		memberDTO.setId("");
		memberDTO.setPassword("");
		
		return memberDTO;
	}


	
	//비밀번호 수정
	@Transactional
	public MemberDTO updatePasswd(MemberUpdatePasswdRequest updatePasswdRequest) {
		MemberEntity memberEntity = memberRepository.findByUsername(updatePasswdRequest.getUsername());
		System.out.println(memberEntity);
		
		if (memberEntity == null) {
			throw new RuntimeException("회원 정보가 잘못됐습니다.");
		}
		
		if(!passwordEncoder.matches(updatePasswdRequest.getOrgPassword(), memberEntity.getPassword())) {
			throw new RuntimeException("기존 비밀번호가 틀렸습니다.");
		}
		
		memberEntity.setPassword(passwordEncoder.encode(updatePasswdRequest.getPassword()));
		memberEntity = memberRepository.save(memberEntity);
		MemberDTO memberDTO = new MemberDTO();
		memberDTO = memberDTO.toMemberDTO(memberEntity);

		return memberDTO;
	}



	//삭제
	public MemberEntity getByCredentials(String username) {
		return memberRepository.findByUsername(username);
	}

    
	//삭제
	@Transactional
	public void delete(MemberDTO memberDTO) {
		MemberEntity memberEntity = getByCredentials(memberDTO.getUsername());

		if (memberEntity == null) {
			throw new RuntimeException("회원 정보가 잘못됐습니다..");
		}

		if (!passwordEncoder.matches(memberDTO.getPassword(), memberEntity.getPassword())) {
			throw new RuntimeException("비밀번호가 잘못됐습니다..");
		}

		memberRepository.delete(memberEntity);

	}


	
	//회원목록
	@Transactional
	public List<MemberDTO> findAll() {
		List<MemberEntity> list_entity = memberRepository.findAll();
		List<MemberDTO> list_dto = new ArrayList<>();

	    for (MemberEntity e : list_entity) {
	        MemberDTO memberDTO = new MemberDTO();
	        memberDTO.toMemberDTO(e);
	        list_dto.add(memberDTO);
	    }
	    
		return list_dto;
	}
	
	
	@Transactional//페이징
	public Page<MemberDTO> findAll(int page) {
		List<Sort.Order> sortList = new ArrayList<>();
		sortList.add(Sort.Order.desc("id"));

		Pageable pageable = (Pageable) PageRequest.of(page, 10, Sort.by(sortList));
		Page<MemberEntity> page_member = memberRepository.findAll(pageable);
		Page<MemberDTO> page_dto = page_member.map(
				MemberEntity -> new MemberDTO(
				MemberEntity.getId(),
				MemberEntity.getUsername(),
				MemberEntity.getName(),
				MemberEntity.getGender(),
				MemberEntity.getEmail(),
				MemberEntity.getPhonenum(),
				MemberEntity.getAddress(),
				MemberEntity.getPassword(),
				null,
				null,
				MemberEntity.getCreateAt(),
				MemberEntity.getUpdateAt(),
				null,
				MemberEntity.getRole(),
				null));
		
		return page_dto;
	}




}

package kr.co.tj.memberservice.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.tj.memberservice.dto.MemberDTO;
import kr.co.tj.memberservice.dto.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String>{



	MemberEntity findByUsername(String username);

	List<MemberEntity> getMembersByUsername(String username);



}

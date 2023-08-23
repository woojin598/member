package kr.co.tj.memberservice.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "members")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "id-uuid")
	@GenericGenerator(strategy = "uuid", name = "id-uuid")
	private String id;

	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String gender;
	
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String phonenum;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	private Date createAt;

	private Date updateAt;
	
	//@ColumnDefault("'ROLE_USER'")
	private String role;

	private String token;

}

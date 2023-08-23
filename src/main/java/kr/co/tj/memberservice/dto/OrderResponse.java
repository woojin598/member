package kr.co.tj.memberservice.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String username;
	private String orderId;
	private String productId;
	private String artist;
	private String title;
	private String itemDescribe;
	private long qty;
	private long unitPrice;
	private long totalPrice;
	private Date createAt;
	private Date updateAt;
	
}
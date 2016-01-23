package com.wxtb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 状态表
 * @author shaka
 *
 */
@Entity
@Table(name="state")
public class State extends BaseModel implements Serializable{
	
	private Integer id;
	/**
	 * 状态名称
	 */
	private String name;
	/**
	 * 参数是否启用
	 */
	private boolean enable;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
}

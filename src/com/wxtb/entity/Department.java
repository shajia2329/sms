package com.wxtb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 部门表
 * @author shaka
 *
 */
//@Entity
//@Table(name="department")
public class Department extends BaseModel implements Serializable{
	
	private Integer id;
	/**
	 * 部门名称
	 */
	private String name;
	/**
	 * 参数是否启用
	 */
	private boolean enable;
}

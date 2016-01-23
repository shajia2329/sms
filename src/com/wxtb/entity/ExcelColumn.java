package com.wxtb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 模板和excel列的对应表
 * @author shaka
 *
 */
@Entity
@Table(name="excel_column")
public class ExcelColumn extends BaseModel implements Serializable{
	
	private Integer id;
	/**
	 * excel字段对应的列号
	 */
	private String excelColumnNumber;
	/**
	 * excel字段对应列号的中文名称
	 */
	private String excelColumnName;
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
	
	@Column(name="excel_column_number")
	public String getExcelColumnNumber() {
		return excelColumnNumber;
	}
	public void setExcelColumnNumber(String excelColumnNumber) {
		this.excelColumnNumber = excelColumnNumber;
	}
	
	@Column(name="excel_column_name")
	public String getExcelColumnName() {
		return excelColumnName;
	}
	public void setExcelColumnName(String excelColumnName) {
		this.excelColumnName = excelColumnName;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}

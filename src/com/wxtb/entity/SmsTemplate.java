package com.wxtb.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
 * 短信模板表
 * @author shaka
 *
 */
@Entity
@Table(name="sms_template")
public class SmsTemplate extends BaseModel implements Serializable{
	
	private Integer id;
	/**
	 * 模板Code
	 */
	private String templateCode;
	/**
	 * 模板名称
	 */
	private String name;
	/**
	 * 模板内容
	 */
	private String templateContent;
	/**
	 * 模板对应列的集合
	 */
	private List<ExcelColumn> excelColumns;
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
	
	@Column(name="template_code")
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(cascade={CascadeType.ALL})
	@JoinColumn(name="sms_template_id")
	public List<ExcelColumn> getExcelColumns() {
		return excelColumns;
	}
	public void setExcelColumns(List<ExcelColumn> excelColumns) {
		this.excelColumns = excelColumns;
	}
	
	@Column(name="template_content")
	public String getTemplateContent() {
		return templateContent;
	}
	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}

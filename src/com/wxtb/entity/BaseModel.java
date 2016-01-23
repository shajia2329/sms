package com.wxtb.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @MappedSuperclass 用在父类上面
 * 当这个类肯定是父类的时候，加此标注。如果改成@Entity，则继承后，多个类继承只会生成一个表，而不是多个类继承生成过个表
 * @author ang_jw
 *
 */
@MappedSuperclass
public abstract class BaseModel {
	
	protected static final Log logger = LogFactory.getLog(BaseModel.class);
	
	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();
	
	/**
	 * 最后一次修改时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt = new Date();

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}

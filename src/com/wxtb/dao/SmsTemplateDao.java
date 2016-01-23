package com.wxtb.dao;

import java.io.Serializable;
import java.util.List;

import com.wxtb.entity.ExcelColumn;
import com.wxtb.entity.SmsTemplate;

public interface SmsTemplateDao extends GeneralHibernateDao<SmsTemplate, Serializable>{
	
	public List<ExcelColumn> getColumnListByTemplateId(int id);
}

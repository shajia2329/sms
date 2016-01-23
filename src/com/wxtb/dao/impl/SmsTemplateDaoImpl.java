package com.wxtb.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.wxtb.dao.SmsTemplateDao;
import com.wxtb.entity.ExcelColumn;
import com.wxtb.entity.SmsTemplate;

@Repository
public class SmsTemplateDaoImpl extends GeneralHibernateDaoImpl<SmsTemplate, Serializable> implements SmsTemplateDao{

	@Override
	public List<ExcelColumn> getColumnListByTemplateId(int id) {
		Query query = getSession().createQuery("from ExcelColumn where sms_template_id=? group by excelColumnNumber");
		query.setParameter(0, id);
		return query.list();
	}
	
}

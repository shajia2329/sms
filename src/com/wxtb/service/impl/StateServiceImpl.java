package com.wxtb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wxtb.dao.StateDao;
import com.wxtb.entity.State;
import com.wxtb.service.StateService;

@Service
@Transactional
public class StateServiceImpl implements StateService{
	
	@Autowired
	private StateDao stateDao;
	
	@Override
	public State getStateByName(String stateName) {
		return stateDao.findUniqueBy("name", stateName);
	}
}

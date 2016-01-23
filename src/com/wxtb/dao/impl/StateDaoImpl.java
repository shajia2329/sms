package com.wxtb.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.wxtb.dao.StateDao;
import com.wxtb.entity.State;

@Repository
public class StateDaoImpl extends GeneralHibernateDaoImpl<State, Serializable> implements StateDao{
	
}

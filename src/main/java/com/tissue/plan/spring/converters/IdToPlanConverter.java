package com.tissue.plan.spring.converters;

import com.tissue.plan.Plan;
import com.tissue.plan.dao.PlanDao;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

//@Component
public class IdToPlanConverter implements Converter<String, Plan> {

    @Autowired
    private PlanDao planDao;

    public Plan convert(String src) {
        return planDao.getPlan("#"+src);
    }
}

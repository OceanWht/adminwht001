package com.wht.admin.demo.service.impl;


import com.wht.admin.demo.mapper.SysConfigMapper;
import com.wht.admin.demo.pojo.SysConfig;
import com.wht.admin.demo.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysServiceImpl implements SysService {

    @Autowired
    SysConfigMapper sysConfigMapper;

    @Override
    public SysConfig getConfigById(long id)
    {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigId(id);
        return sysConfigMapper.selectConfig(sysConfig);
    }
}

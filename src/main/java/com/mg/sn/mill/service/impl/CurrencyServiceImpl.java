package com.mg.sn.mill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.sn.mill.mapper.CurrencyMapper;
import com.mg.sn.mill.mapper.UserMapper;
import com.mg.sn.mill.model.entity.Currency;
import com.mg.sn.mill.model.entity.User;
import com.mg.sn.mill.service.ICurrencyService;
import com.mg.sn.mill.service.UserService;
import com.mg.sn.utils.result.StarNodeResultObject;
import com.mg.sn.utils.result.StarNodeSwitch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */
@Service
public class CurrencyServiceImpl extends ServiceImpl<CurrencyMapper, Currency> implements ICurrencyService {
	
	private final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);
	
	@Resource
	private CurrencyMapper currencyMapper;

    @Override
    public Currency getById(String id) {
        Currency result = currencyMapper.selectById(id);
        return result;
    }
}

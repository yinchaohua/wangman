package com.mg.sn.mill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.sn.mill.mapper.UserMapper;
import com.mg.sn.mill.model.entity.User;
import com.mg.sn.mill.service.UserService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
	
	private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
}
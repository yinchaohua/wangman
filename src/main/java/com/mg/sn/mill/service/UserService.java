package com.mg.sn.mill.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */
public interface UserService extends IService<User> {

    /**
     * 登陆验证
     * @param account   账号
     * @param password  密码
     * @return
     */
    List<User> queryForLogin (String account, String password);


}

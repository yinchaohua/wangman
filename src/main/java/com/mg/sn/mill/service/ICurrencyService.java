package com.mg.sn.mill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.entity.Currency;
import com.mg.sn.utils.result.StarNodeResultObject;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hcy
 * @since 2019-08-09
 */
public interface ICurrencyService extends IService<Currency> {

    /**
     * ID查询币种
     * @param id  主键
     * @return
     */
    Currency getById (String id);

}

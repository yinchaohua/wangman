package com.mg.sn.mill.service.impl;

import com.mg.sn.mill.mapper.SoftPackageMapper;
import com.mg.sn.mill.model.entity.SoftPackage;
import com.mg.sn.mill.service.ISoftPackageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.sn.utils.Enum.DelFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hcy
 * @since 2019-08-27
 */
@Service
public class SoftPackageServiceImpl extends ServiceImpl<SoftPackageMapper, SoftPackage> implements ISoftPackageService {

    @Autowired
    private SoftPackageMapper softPackageMapper;

    @Override
    public List<SoftPackage> query(String name, String version, String format, String delFlag) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("version", version);
        map.put("format", format);
        map.put("delFlag", delFlag);
        List<SoftPackage> result = softPackageMapper.query(map);
        return result;
    }

    @Override
    public SoftPackage save(String name, String version, String format, String userId) throws Exception {
        SoftPackage softPackage = new SoftPackage();
        softPackage.setName(name);
        softPackage.setVersion(version);
        softPackage.setFormat(format);
        //创建人ID
        softPackage.setCreateUserId(Integer.parseInt(userId));
        //创建日期
        softPackage.setCreateDate(LocalDateTime.now());
        //删除状态(未删除)
        softPackage.setDelFlag(DelFlag.NOT_DEL.getCode());
        boolean result = softPackage.insert();
        if (!result) {
            throw new ServerException("保存软件包失败");
        }
        return softPackage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoftPackage updateAndSave(List<SoftPackage> nameValilist, String name, String version, String format, String userId) throws Exception{
        for (SoftPackage softPackage : nameValilist) {
            softPackage.setDelFlag(DelFlag.DEL.getCode());
        }
        boolean updateResult = this.updateBatchById(nameValilist);
        if (!updateResult) {
            throw new ServerException("更新旧软件包状态失败");
        }

        SoftPackage updateAndSaveResult = save(name, version, format, userId);
        return updateAndSaveResult;
    }
}

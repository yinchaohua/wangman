package com.mg.sn.mill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.sn.mill.model.entity.SoftPackage;

import java.util.List;

/**
 * <p>
 *  软件包
 * </p>
 *
 * @author hcy
 * @since 2019-08-27
 */
public interface ISoftPackageService extends IService<SoftPackage> {

    /**
     *  查询软件包
     * @param name  名称
     * @param version  版本
     * @param format  格式
     * @param delFlag 删除状态
     * @return
     * @throws Exception
     */
    List<SoftPackage> query (String name, String version, String format, String delFlag) throws Exception;

    /**
     * 保存软件包信息
     * @param name  名称
     * @param version  版本
     * @param format  格式
     * @param userId  用户ID
     * @return
     * @throws Exception
     */
    SoftPackage save (String name, String version, String format, String userId) throws Exception;

    /**
     * 更新已经存在软件包并插入新软件包
     * @param nameValilist  旧软件包
     * @param name      新增软件包名称
     * @param version   新增软件包版本
     * @param format    新增软件包格式
     * @param userId    新增软件包用户Id
     * @return
     * @throws Exception
     */
    SoftPackage updateAndSave (List<SoftPackage> nameValilist, String name, String version, String format, String userId) throws Exception;

}

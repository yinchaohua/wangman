package com.mg.sn.mill.controller;

import com.mg.sn.mill.service.IEquipmentService;
import com.mg.sn.utils.annotation.Auth;
import com.mg.sn.utils.annotation.NotNull;
import com.mg.sn.utils.annotation.Param;
import com.mg.sn.utils.baseController.StarNodeBaseController;
import com.mg.sn.utils.result.InitEquipmentResult;
import com.mg.sn.utils.result.StarNodeWrappedResult;
import com.mg.sn.utils.schedule.cron.CronUtil;
import com.mg.sn.utils.schedule.model.TaskScheduleModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * <p>
 *  定时调度
 * </p>
 *
 * @author hcy
 * @since 2019-09-04
 */
@Api(description = "定时调度")
@Auth
@RestController
@RequestMapping("/equipmentController")
public class TaskSchedulerController extends StarNodeBaseController {

    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerController.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private IEquipmentService equipmentService;
    private ScheduledFuture future;

    // 线程存储器
    public static ConcurrentHashMap<String, ScheduledFuture> map = new ConcurrentHashMap<String, ScheduledFuture>();

    @ApiOperation(value="定时执行初始化设备", notes="定时执行初始化设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", value = "定时时间(min)", required = false, dataType = "String", paramType = "query")
    })
    @NotNull({
            @Param(paramKey = "time", message = "定时时间")
    })
    @PostMapping("/startInitEquipmentPoolTask")
    public StarNodeWrappedResult startInitEquipmentPoolTask (String time) {


        //生成cron表达式
        TaskScheduleModel taskScheduleModel = new TaskScheduleModel();
        taskScheduleModel.setJobType(1);//按每天
        taskScheduleModel.setHour("*");
        taskScheduleModel.setMinute("*/" + time);
        taskScheduleModel.setSecond("0");
        String cropExp = CronUtil.createCronExpression(taskScheduleModel);

        //获取当前线程名称
        String name = Thread.currentThread().getName();
        future = threadPoolTaskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
//                log.info("开始执行: 初始化设备");
////                InitEquipmentResult equipment = equipmentService.initEquipment();
////                if (!equipment.isSucc()) {
////                    log.error("初始化设备信息失败, msg: {} ", equipment);
////                }
////                log.info("结束执行: 初始化设备成功, msg: {} ", equipment);
                System.out.println("111111111");
            }
        }, new CronTrigger("*/2 * * * * ?"));

        return StarNodeWrappedResult.successWrapedResult("设备定时执行初始化设备任务成功");
    }

    @ApiOperation(value="停止定时执行初始化设备", notes="停止定时执行初始化设备")
    @PostMapping("/stopInitEquipmentPoolTask")
    public StarNodeWrappedResult stopInitEquipmentPoolTask () {
        if (future == null) {
            return StarNodeWrappedResult.failWrapedResult("停止定时执行初始化设备失败, 线程未启动");
        }

        future.cancel(true);
        return StarNodeWrappedResult.successWrapedResult("停止定时执行初始化设备任务成功");
    }


}

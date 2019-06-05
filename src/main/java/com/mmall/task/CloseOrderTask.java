package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 超过30min 未处理订单将订单取消，之后将库存释放
 * @author maoxin
 * */
@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;

    //无分布式锁的版本
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1(){
        log.info("定时任务启动");
        int hours = Integer.parseInt(PropertiesUtil.getProperty("task.close.order.hours","1"));
        iOrderService.closeOrder(hours);
        log.info("定时任务结束");
    }
}

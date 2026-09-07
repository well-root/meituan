package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j

public class OrderTask {

    //处理超市任务类

    @Autowired
    OrderMapper orderMapper;

    //订单超时处理 定义一个格式 每分钟执行一次

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("订单超时处理:{}", new Date());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // select * from orders where status = 1 and order_time < 当前时间-15分钟
        List<Orders> ordersList = orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT, time);
        if(ordersList != null && ordersList.size() > 0){
            ordersList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("支付超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }
    }

//派送中的订单处理 每天处理一次

    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("派送订单处理:{}",new Date());
        LocalDateTime time =LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList =orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS,time);

        //找到的数组全部遍历删除
        if(ordersList !=null && ordersList.size()>0){
            ordersList.forEach(
                    order ->
                    {
                        order.setStatus(Orders.COMPLETED);
                        orderMapper.update(order);
                    }
            );
        }


    }
}

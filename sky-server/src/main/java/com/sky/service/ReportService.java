package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;


//根据时间来统计营业额

public interface ReportService {
    TurnoverReportVO getTurnover(LocalDate begin, LocalDate end);
    //根据时间区间统计用户数量

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
//根据时间统计订单数量

    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);
    //获取top10的菜品

    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
//导出数据的方法excel

    void exportBusinessData(HttpServletResponse response);
}

package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    WorkspaceService workspaceService;
    //根据时间来统计数据
    public TurnoverReportVO getTurnover(LocalDate begin,LocalDate end){
        //定义一个数组来存数据
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(LocalDate.from(begin));

        while (!begin.equals(end)){
            begin=begin.plusDays(1);//往后数一天 如果相等就推出循环 不等就继续加
            dateList.add(LocalDate.from(begin));
        }

        List<Double> turnoverList=new ArrayList<>();

        //这个循环只是 把每个日期给取出来 然后进行时间上的遍历
        for (LocalDate date:dateList){
            LocalDateTime beginTime=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date,LocalTime.MAX);
            Map map=new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end",endTime);
            Double turnover = orderMapper.sumByMap(map);
            turnover=turnover==null?0.0:turnover;
            turnoverList.add(turnover);
        }

        //进行数据的封装
        return TurnoverReportVO.builder()
                .dateList(StringUtil.join(",", dateList))
                .turnoverList(StringUtil.join(",", turnoverList))
                .build();
    }


    public UserReportVO getUserStatistics(LocalDate begin,LocalDate end){
        List<LocalDate> dateList =new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList=new ArrayList<>();
        List<Integer> totalUserList=new ArrayList<>();

        //找出每天的新增用户
        for (LocalDate date : dateList){
            LocalDateTime beginTime= LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date,LocalTime.MAX);

            Integer newUser=getUserCount(beginTime,endTime);

            //总人数吧
            Integer totalUser=getUserCount(null,endTime);

            newUserList.add(newUser);
            totalUserList.add(totalUser);




        }


        return UserReportVO.builder()
                .dateList(StringUtil.join(",", dateList))
                .newUserList(StringUtil.join(",", newUserList))
                .totalUserList(StringUtil.join(",", totalUserList))
                .build();

    }

    //根据时间来查询订单数量

    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }

//        查询每天的订单
        List<Integer> orderCountList=new ArrayList<>();

        //查询每天的有效订单
        List<Integer> validOrderCountList=new ArrayList<>();

        for(LocalDate date : dateList){
            LocalDateTime beginTime=LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(date,LocalTime.MAX);
            //调用方法来查询响应的数据
            Integer orderCount=getOrderCount(beginTime,endTime,null);

            Integer validOrderCount=getOrderCount(beginTime,endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);

            validOrderCountList.add(validOrderCount);
        }
        //时间区域内的订单数 使用流 去得到订单数
        Integer totalOrderCount=orderCountList.stream().reduce(Integer::sum).get();
//        时间内有效的订单
        Integer validOrderCount=validOrderCountList.stream().reduce(Integer::sum).get();
//        完成率 要避免逻辑错误
        Double orderCompletionRate=0.0;
        if(totalOrderCount!=0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;

        }

        return OrderReportVO.builder()
                .dateList(StringUtil.join(",",dateList))
                .orderCountList(StringUtil.join(",",orderCountList))
                .validOrderCountList(StringUtil.join(",",validOrderCountList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    //指定时间的top10菜品

    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime=LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime=LocalDateTime.of(end,LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOList=orderMapper.getSaleTop10(beginTime,endTime);

        String nameList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()),",");
        String numberList = StringUtils.join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()),",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();


    }

   //导出近30天的数据报表

    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin=LocalDate.now().minusDays(30);
        LocalDate end=LocalDate.now().minusDays(1);

        //查询数据报表
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end,LocalTime.MAX));

        InputStream inputStream=this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try{
            //创建一个excel表格
            XSSFWorkbook excel=new XSSFWorkbook(inputStream);
            //获得一个页
            XSSFSheet sheet=excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第四行
            XSSFRow row=sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row=sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for(int i=0;i<30;i++){
                LocalDate date=begin.plusDays(i);
                        //导入数据
               businessData=workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
                row=sheet.getRow(7+i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());


            }

            //通过输出流下载文件到客户端
            ServletOutputStream out=response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    //查询用户数量
    private Integer getUserCount(LocalDateTime beginTime,LocalDateTime endTime){
        Map map=new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        return userMapper.countByMap(map);

    }

    //一个统计的方法
    private  Integer getOrderCount(LocalDateTime beginTime,LocalDateTime endTime,Integer status){
        Map map=new HashMap<>();
        map.put("status",status);
        map.put("begin",beginTime);
        map.put("end",endTime);
        return orderMapper.countByMap(map);

    }





}

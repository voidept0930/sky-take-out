package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final OrderDetailMapper orderDetailMapper;
    @Autowired
    public ReportServiceImpl(OrderMapper orderMapper, UserMapper userMapper, OrderDetailMapper orderDetailMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.orderDetailMapper = orderDetailMapper;
    }

    /**
     * 营业额统计接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        // 处理dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 处理turnoverList
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime zero = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime twentyFour = LocalDateTime.of(date, LocalTime.MAX);
            // 查单日营业额
            Double singleDayTurnover = orderMapper.getTurnOverByDate(zero, twentyFour);
            turnoverList.add(singleDayTurnover);
        }

        // 返回时将列表转换为所需的字符串
        return new TurnoverReportVO(
                StringUtils.join(dateList, ","),
                StringUtils.join(turnoverList, ",")
        );
    }

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {

        // 处理dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 处理newUserList和totalUserList
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime zero = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime twentyFour = LocalDateTime.of(date, LocalTime.MAX);
            newUserList.add(userMapper.getNewUserByDate(zero, twentyFour));
            totalUserList.add(userMapper.getTotalUserByDate(twentyFour));
        }

        return new UserReportVO(
                StringUtils.join(dateList, ","),
                StringUtils.join(newUserList, ","),
                StringUtils.join(totalUserList, ",")
        );
    }

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        // 处理dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 查询单日的
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime zero = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime twentyFour = LocalDateTime.of(date, LocalTime.MAX);
            orderCountList.add(orderMapper.getOrderCountByDate(zero, twentyFour, null));
            validOrderCountList.add(orderMapper.getOrderCountByDate(zero, twentyFour, Orders.COMPLETED));
        }

        // 查订单总数
        LocalDateTime zero = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime twentyFour = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);
        Integer totalOrderCount = orderMapper.getOrderCountByDate(zero, twentyFour, null);
        Integer validOrderCount = orderMapper.getOrderCountByDate(zero, twentyFour, Orders.COMPLETED);

        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();

        return new OrderReportVO(
                StringUtils.join(dateList, ","),
                StringUtils.join(orderCountList, ","),
                StringUtils.join(validOrderCountList, ","),
                totalOrderCount,
                validOrderCount,
                orderCompletionRate
        );
    }


    /**
     * 查询销量top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime zero = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime twentyFour = LocalDateTime.of(end, LocalTime.MAX);

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        List<GoodsSalesDTO> list = orderDetailMapper.getTop10(zero, twentyFour);
        for (GoodsSalesDTO goodsSalesDTO : list) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }


        return new SalesTop10ReportVO(
                StringUtils.join(nameList, ","),
                StringUtils.join(numberList, ",")
        );
    }

    /**
     * 导出最近30天的运营数据
     */
    @Override
    public void export() throws IOException {
        // 准备信息
        XSSFWorkbook excel = new XSSFWorkbook();
        LocalDate end = LocalDate.now();
        LocalDate begin = end.minusDays(30);
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 创建日期格式
        XSSFCellStyle dateStyle = excel.createCellStyle();
        XSSFDataFormat dataFormat = excel.createDataFormat();
        dateStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd"));

        // 营业额统计页
        // 查询营业额（from turnoverStatistics）
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime zero = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime twentyFour = LocalDateTime.of(date, LocalTime.MAX);
            // 查单日营业额
            Double singleDayTurnover = orderMapper.getTurnOverByDate(zero, twentyFour);
            turnoverList.add(singleDayTurnover);
        }
        // 导出Excel
        XSSFSheet turnoverSheet = excel.createSheet("营业额");
        XSSFRow turnoverTitle = turnoverSheet.createRow(0);
        turnoverTitle.createCell(0).setCellValue("日期");
        turnoverTitle.createCell(1).setCellValue("营业额");
        for (int i = 1; i <= 30; i++) {
            XSSFRow row = turnoverSheet.createRow(i);
            XSSFCell dateCell = row.createCell(0);
            dateCell.setCellValue(dateList.get(i - 1));
            dateCell.setCellStyle(dateStyle);
            Double turnover = turnoverList.get(i - 1);
            if (turnover != null)
            {
                row.createCell(1).setCellValue(turnover);
            } else {
                row.createCell(1).setCellValue(0);
            }
        }

        // 用户统计页
        // 查询用户（from userStatistics）
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime zero = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime twentyFour = LocalDateTime.of(date, LocalTime.MAX);
            newUserList.add(userMapper.getNewUserByDate(zero, twentyFour));
            totalUserList.add(userMapper.getTotalUserByDate(twentyFour));
        }
        // 导出Excel
        XSSFSheet userSheet = excel.createSheet("用户");
        XSSFRow userTitle = userSheet.createRow(0);
        userTitle.createCell(0).setCellValue("日期");
        userTitle.createCell(1).setCellValue("新增用户数");
        userTitle.createCell(2).setCellValue("总用户数");
        for (int i = 1; i <= 30; i++) {
            XSSFRow row = userSheet.createRow(i);
            XSSFCell dateCell = row.createCell(0);
            dateCell.setCellValue(dateList.get(i - 1));
            dateCell.setCellStyle(dateStyle);
            row.createCell(1).setCellValue(newUserList.get(i - 1));
            row.createCell(2).setCellValue(totalUserList.get(i - 1));
        }

        // 订单统计页
        // 查询订单信息（from orderStatistics）
        // 查询单日的
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime zero = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime twentyFour = LocalDateTime.of(date, LocalTime.MAX);
            orderCountList.add(orderMapper.getOrderCountByDate(zero, twentyFour, null));
            validOrderCountList.add(orderMapper.getOrderCountByDate(zero, twentyFour, Orders.COMPLETED));
        }
        // 查订单总数
        LocalDateTime zero = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime twentyFour = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);
        Integer totalOrderCount = orderMapper.getOrderCountByDate(zero, twentyFour, null);
        Integer validOrderCount = orderMapper.getOrderCountByDate(zero, twentyFour, Orders.COMPLETED);
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        // 导出Excel
        XSSFSheet orderSheet = excel.createSheet("订单");
        XSSFRow orderTitle = orderSheet.createRow(0);
        orderTitle.createCell(0).setCellValue("日期");
        orderTitle.createCell(1).setCellValue("订单总数");
        orderTitle.createCell(2).setCellValue("有效订单数");
        for (int i = 1; i <= 30; i++) {
            XSSFRow row = orderSheet.createRow(i);
            XSSFCell dateCell = row.createCell(0);
            dateCell.setCellValue(dateList.get(i - 1));
            dateCell.setCellStyle(dateStyle);
            row.createCell(1).setCellValue(orderCountList.get(i - 1));
            row.createCell(2).setCellValue(validOrderCountList.get(i - 1));
        }
        XSSFRow totalOrderCountRow = orderSheet.createRow(31);
        totalOrderCountRow.createCell(0).setCellValue("30天总订单数");
        totalOrderCountRow.createCell(1).setCellValue(totalOrderCount);
        XSSFRow validOrderCountRow = orderSheet.createRow(32);
        validOrderCountRow.createCell(0).setCellValue("30天有效订单数");
        validOrderCountRow.createCell(1).setCellValue(validOrderCount);
        XSSFRow orderCompletionRateRow = orderSheet.createRow(33);
        orderCompletionRateRow.createCell(0).setCellValue("30天订单完成率");
        orderCompletionRateRow.createCell(1).setCellValue(orderCompletionRate);

        // 销量统计页
        // 查询top10（from top10）
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        List<GoodsSalesDTO> list = orderDetailMapper.getTop10(zero, twentyFour);
        for (GoodsSalesDTO goodsSalesDTO : list) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        // 导出Excel
        XSSFSheet top10Sheet = excel.createSheet("销量top10");
        XSSFRow top10Title = top10Sheet.createRow(0);
        top10Title.createCell(0).setCellValue("名称");
        top10Title.createCell(1).setCellValue("销量");
        for (int i = 1; i <= 10; i++) {
            XSSFRow row = top10Sheet.createRow(i);
            row.createCell(0).setCellValue(nameList.get(i - 1));
            row.createCell(1).setCellValue(numberList.get(i - 1));
        }

        // 全部导出
        FileOutputStream out = new FileOutputStream(LocalDate.now() + "数据统计表格.xlsx");
        excel.write(out);
        out.flush();
        out.close();
        excel.close();

    }
}

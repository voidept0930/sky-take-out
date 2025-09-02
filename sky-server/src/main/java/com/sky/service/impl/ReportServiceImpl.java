package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    @Autowired
    public ReportServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
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
            Double singleDayTurnover = reportMapper.getTurnOverByDate(zero, twentyFour);
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
            newUserList.add(reportMapper.getNewUserByDate(zero, twentyFour));
            totalUserList.add(reportMapper.getTotalUserByDate(twentyFour));
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
            orderCountList.add(reportMapper.getOrderCountByDate(zero, twentyFour, null));
            validOrderCountList.add(reportMapper.getOrderCountByDate(zero, twentyFour, Orders.COMPLETED));
        }

        // 查订单总数
        LocalDateTime zero = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime twentyFour = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);
        Integer totalOrderCount = reportMapper.getOrderCountByDate(zero, twentyFour, null);
        Integer validOrderCount = reportMapper.getOrderCountByDate(zero, twentyFour, Orders.COMPLETED);

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

        List<GoodsSalesDTO> list = reportMapper.getTop10(zero, twentyFour);
        for (GoodsSalesDTO goodsSalesDTO : list) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }


        return new SalesTop10ReportVO(
                StringUtils.join(nameList, ","),
                StringUtils.join(numberList, ",")
        );
    }
}

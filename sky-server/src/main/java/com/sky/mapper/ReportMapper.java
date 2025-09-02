package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    /**
     * 查询某一日的订单总额
     * @param zero
     * @param twentyFour
     * @return
     */
    Double getTurnOverByDate(LocalDateTime zero, LocalDateTime twentyFour);

    /**
     * 查询某一日新增用户数
     * @param zero
     * @param twentyFour
     * @return
     */
    Integer getNewUserByDate(LocalDateTime zero, LocalDateTime twentyFour);

    /**
     * 查询某一日总用户数
     * @param twentyFour
     * @return
     */
    Integer getTotalUserByDate(LocalDateTime twentyFour);

    /**
     * 查询某一日订单数
     * @param zero
     * @param twentyFour
     * @param status
     * @return
     */
    Integer getOrderCountByDate(LocalDateTime zero, LocalDateTime twentyFour, Integer status);

    /**
     * 查询top10
     * @param zero
     * @param twentyFour
     * @return
     */
    List<GoodsSalesDTO> getTop10(LocalDateTime zero, LocalDateTime twentyFour);

}

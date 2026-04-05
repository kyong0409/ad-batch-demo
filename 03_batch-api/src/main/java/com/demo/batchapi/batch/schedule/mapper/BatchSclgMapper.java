package com.demo.batchapi.batch.schedule.mapper;

import com.demo.batchapi.batch.schedule.dto.BatchSclgDetails;
import com.demo.batchapi.batch.schedule.dto.BatchSclgListRequest;
import com.demo.batchapi.batch.schedule.dto.BatchSclgAddModifyRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BatchSclgMapper {
    int count(BatchSclgListRequest request);
    List<BatchSclgDetails> select(BatchSclgListRequest request);
    Optional<BatchSclgDetails> selectOne(@Param("jobGroup") String jobGroup, @Param("jobNm") String jobNm);
    int insert(BatchSclgAddModifyRequest request);
    int update(@Param("jobGroup") String jobGroup, @Param("jobNm") String jobNm, @Param("data") BatchSclgAddModifyRequest data);
}

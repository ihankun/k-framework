package io.hankun.framework.ai.store.history.repository;

import io.hankun.framework.ai.store.history.po.SessionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @className: ModelHistoryRepository
 * @createAt: 2025/10/16 19:03
 * @author: hankun
 */
@Slf4j
@Component
public class SessionInfoRepository {

    private final MongoTemplate mongoTemplate;

    public SessionInfoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public List<SessionInfo> loadSessionInfoData(String startTime, String endTime) {
        log.info("查询SessionInfo数据，时间范围：{} - {}", startTime, endTime);
        Query query = new Query();
        Date start = parseIsoDateTime(startTime);
        Date end = parseIsoDateTime(endTime);
        query.addCriteria(Criteria.where("lastUpdateTime")
                .gte(start)
                .lte(end)).with(Sort.by(Sort.Direction.DESC, "lastUpdateTime"));
        try {

            List<SessionInfo> result = mongoTemplate.find(query, SessionInfo.class);
            log.info("查询成功，共找到 {} 条记录", result.size());
            return result;
        } catch (Exception e) {
            log.error("MongoDB查询失败", e);
            throw new RuntimeException("数据库查询失败", e);
        }
    }

    /**
     * 解析ISO 8601格式的时间字符串
     * 支持格式：2025-11-24T11:33:46.108+00:00
     */
    private Date parseIsoDateTime(String dateTimeStr) throws DateTimeParseException {
        // 定义多种可能的日期时间格式
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ISO_DATE_TIME,                    // 2025-11-24T11:33:46.108+00:00
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),    // 2025-11-24T11:33:46
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),      // 2025-11-24 11:33:46
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,               // 2025-11-24T11:33:46.108
            DateTimeFormatter.ofPattern("yyyy-MM-dd")                 // 2025-11-24
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                // 尝试解析为LocalDateTime，然后转换为Date
                LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
                return Date.from(localDateTime.atOffset(ZoneOffset.UTC).toInstant());
            } catch (DateTimeParseException e) {
                // 继续尝试下一种格式
            }
        }

        // 如果所有格式都不匹配，尝试直接解析Instant
        try {
            Instant instant = Instant.parse(dateTimeStr);
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("无法解析时间格式: " + dateTimeStr, dateTimeStr, e.getErrorIndex());
        }
    }

}

package io.ihankun.framework.mq.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hankun
 */
@Slf4j
public class GrayMark {

    public static final String ALL_TAGS = "*";
    public static final String SPLIT = "||";
    public static final String GRAY_MARK = "-gray";
    public static final String REGEX_SPLIT = "\\|\\|";

    @Getter
    @Setter
    private static String grayMark;


    public static String buildConsumerTags(String tags) {
        if (checkAll(tags)) {
            return tags;
        }
        if (tags.contains(GRAY_MARK)) {
            return tags;
        }
        String[] datas = tags.split(REGEX_SPLIT);
        List<String> infos = Arrays.stream(datas).map(a -> a + SPLIT + a + GRAY_MARK).collect(Collectors.toList());
        return StringUtils.join(infos, SPLIT);

    }

    private static boolean checkAll(String tags) {
        return StringUtils.isEmpty(tags) || ALL_TAGS.equals(tags);
    }
}

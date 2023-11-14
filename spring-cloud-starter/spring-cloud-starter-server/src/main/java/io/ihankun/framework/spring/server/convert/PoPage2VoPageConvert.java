package io.ihankun.framework.spring.server.convert;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.mapstruct.InheritConfiguration;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author hankun
 */
public interface PoPage2VoPageConvert<PO,VO> {

    /**
     * 将PoPageInfo转为VoPageInfo，之所以转换是因为pagehelper默认返回的是Po的PageInfo，不转换的话，total总条数是不对的
     * @param page po分页信息
     * @return vo分页信息
     */
    default PageInfo<VO> convert(Page<PO> page) {
        //将业务数据封装为PageInfo<PO>防止total不准确
        PageInfo<PO> poPage = new PageInfo<>(page);

        //将PageInfo<PO>转为PageInfo<VO>返回给前端
        PageInfo<VO> voPage = new PageInfo(po2Vo(poPage.getList()));

        BeanUtils.copyProperties(poPage,voPage,"list");
        return voPage;
    }

    /**
     * 将po集合转为vo集合
     * @param voList
     * @return
     */
    @InheritConfiguration
    List<VO> po2Vo(List<PO> voList);
}

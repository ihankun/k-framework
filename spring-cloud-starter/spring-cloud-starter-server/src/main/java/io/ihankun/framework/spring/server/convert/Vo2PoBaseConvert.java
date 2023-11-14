package io.ihankun.framework.spring.server.convert;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

/**
 * @author hankun
 */
public interface Vo2PoBaseConvert<Vo, Po> {

    /**
     * 将单个VO转为PO
     *
     * @param vo
     * @return PO
     */
    @InheritConfiguration
    Po vo2Po(Vo vo);

    /**
     * 将VO集合转为PO集合
     *
     * @param sourceList
     * @return poList
     */
    @InheritConfiguration
    List<Po> vo2Po(List<Vo> sourceList);

    /**
     * 将单个PO转为VO
     *
     * @param po
     * @return VO
     */
    @InheritInverseConfiguration
    Vo po2Vo(Po po);

    /**
     * 将target集合转为集合source集合
     *
     * @param poList
     * @return VOList集合
     */
    @InheritInverseConfiguration
    List<Vo> po2Vo(List<Po> poList);
}

package io.ihankun.framework.spring.server.convert;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

/**
 * @author hankun
 */
public interface Dto2PoBaseConvert<Dto, Po> {

    /**
     * 将单个DTO转为PO
     *
     * @param dto
     * @return PO
     */
    @InheritConfiguration
    Po dto2Po(Dto dto);

    /**
     * 将DTO集合转为PO集合
     *
     * @param sourceList
     * @return poList
     */
    @InheritConfiguration
    List<Po> dto2Po(List<Dto> sourceList);

    /**
     * 将单个PO转为DTO
     *
     * @param po
     * @return DTO
     */
    @InheritInverseConfiguration
    Dto po2Dto(Po po);

    /**
     * 将target集合转为集合source集合
     *
     * @param poList
     * @return dtoList集合
     */
    @InheritInverseConfiguration
    List<Dto> po2Dto(List<Po> poList);
}

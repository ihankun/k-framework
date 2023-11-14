package io.ihankun.framework.spring.server.convert;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;

import java.util.List;

/**
 * @author hankun
 */
public interface Source2TargetConvert<Source, Target> {

    /**
     * 将单个source转为target
     *
     * @param source
     * @return target
     */
    @InheritConfiguration
    Target source2Target(Source source);

    /**
     * 将source集合转为target集合
     *
     * @param sourceList
     * @return poList
     */
    @InheritConfiguration
    List<Target> source2Target(List<Source> sourceList);

    /**
     * 将单个target转为source
     *
     * @param po
     * @return source
     */
    @InheritInverseConfiguration
    Source target2Source(Target po);

    /**
     * 将target集合转为集合source集合
     *
     * @param poList
     * @return sourceList集合
     */
    @InheritInverseConfiguration
    List<Source> target2Source(List<Target> poList);
}

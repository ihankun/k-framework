package io.hankun.framework.powerjob.task;

import io.hankun.framework.powerjob.entity.PowerJobHospitalTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 域名级别的子任务，用于 DOMAIN 模式。
 * @fileName: DomainSubTask.java
 * @author: hankun
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PowerJobDomainSubTask implements PowerJobTask {
    private String domain;
    private List<PowerJobHospitalTask> hospitals;
}

package io.hankun.framework.powerjob.task;

import io.hankun.framework.powerjob.entity.PowerJobHospitalTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description: 医院级别的子任务，用于 HOSPITAL 模式。包装了单个 PowerHospitalTask。
 * @fileName: PowerHospitalSubTask.java
 * @author: hankun
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PowerJobHospitalSubTask implements PowerJobTask {
    private PowerJobHospitalTask hospitalTask;
    private String domain;
}

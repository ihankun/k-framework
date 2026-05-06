package io.hankun.framework.ai.agent.def.entity;

/**
 * @description:
 * @className: IAgentDataParams
 * @createAt: 2025/12/25 10:55
 * @author: hankun
 */
public interface IAgentDataParams {

    String getInputText();

    default byte[] getInputAudio() {
        return null;
    }

    default byte[] getInputFileData() {
        return null;
    }


    default String getInputFilePath() {
        return null;
    }

}

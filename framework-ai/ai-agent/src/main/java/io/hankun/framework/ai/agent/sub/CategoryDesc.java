package io.hankun.framework.ai.agent.sub;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @className: CategoryDesc
 * @createAt: 2025/10/13 09:43
 * @author: hankun
 */
public record CategoryDesc(String desc, List<Param> params, List<Sample> samples) {


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private String desc;

        private final List<Param> params = new ArrayList<>();

        private final List<Sample> samples = new ArrayList<>();

        public Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder param(String name, String desc) {
            this.params.add(new Param(name, desc));
            return this;
        }

        public Builder sample(String input, String output) {
            this.samples.add(new Sample(input, output));
            return this;
        }

        public CategoryDesc build() {
            return new CategoryDesc(desc, params, samples);
        }
    }

    public record Param(String name, String desc) {

    }

    public record Sample(String input, String output) {

    }
}

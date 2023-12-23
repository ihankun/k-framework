/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ihankun.framework.poi.excel.imports.sax.parse;

import io.ihankun.framework.poi.excel.entity.sax.SaxReadCellEntity;

import java.util.List;

/**
 * @author jueyue
 * @since 3.1
 * @date 2017-11-9 13:04:06
 */
public interface ISaxRowRead {
    /**
     * 解析数据
     * @param index
     * @param cellList
     */
    void parse(int index, List<SaxReadCellEntity> cellList);

}

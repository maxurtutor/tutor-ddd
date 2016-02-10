/*
 * Copyright (c) 2016 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.maxur.ddd.service.components;

import static java.lang.String.format;

/**
 * The type Not found exception.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>13.11.2015</pre>
 */
public class NotFoundException extends BusinessException {

    private static final long serialVersionUID = -343985451164289078L;

    /**
     * Instantiates a new Not found exception.
     *
     * @param entityType the entity type
     * @param id         the entity id
     */
    public NotFoundException(String entityType, String id) {
        super(format("%s (id='%s') is not found", entityType, id));
    }
}

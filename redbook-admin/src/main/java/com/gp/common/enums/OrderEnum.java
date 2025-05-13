/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gp.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 订单渠道枚举
 */
@RequiredArgsConstructor
public enum OrderEnum {


    /**
     * 商城订单
     */
    MAIL_PHONE(1, "MAIL_PHONE", "商城订单"),


    APPLET(2, "APPLET", "小程序订单")
    ;


    @Getter
    private final Integer code;

    @Getter
    private final String name;

    @Getter
    private final String value;
}

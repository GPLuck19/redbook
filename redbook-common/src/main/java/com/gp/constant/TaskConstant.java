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

package com.gp.constant;

/**
 * 用户常量
 */
public final class TaskConstant {

    /**
     * 文章同步key
     */
    public static final String TASK_POST_KEY = "task:task_post_time";


    /**
     * 文章点赞同步事件
     */
    public static final String POST_LIKE_EVENT_KEY = "post_like_events";

    /**
     * 文章收藏同步事件
     */
    public static final String POST_COLLECT_EVENT_KEY = "post_collect_events";

    /**
     * 视频点赞同步事件
     */
    public static final String VIDEO_LIKE_EVENT_KEY = "video_like_events";

    /**
     * 视频收藏同步事件
     */
    public static final String VIDEO_COLLECT_EVENT_KEY = "video_collect_events";

    /**
     * 登录日志同步key
     */
    public static final String TASK_LOGIN_KEY = "task:task_login_time";

    /**
     * 操作日志同步key
     */
    public static final String TASK_OPERA_KEY = "task:task_opera_time";
}

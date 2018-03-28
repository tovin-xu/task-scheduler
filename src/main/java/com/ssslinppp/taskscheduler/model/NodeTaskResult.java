package com.ssslinppp.taskscheduler.model;

import lombok.Builder;
import lombok.Data;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/3/27 , Time: 12:32 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@Builder
public class NodeTaskResult {
    private String id;          //任务唯一标识
    private Object result;     // 子任务执行结果
}

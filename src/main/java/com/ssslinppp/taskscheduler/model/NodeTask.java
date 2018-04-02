package com.ssslinppp.taskscheduler.model;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/3/23 , Time: 17:10 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@AllArgsConstructor
public abstract class NodeTask implements Callable<NodeTaskResult> {
    private String parentId;
    private String id;                   //唯一标示
    private Set<String> dependences = Sets.newConcurrentHashSet();   //需要依赖的nodeTask
    private NodeTaskStatus nodeTaskStatus = NodeTaskStatus.init;
    private NodeTaskResult nodeTaskResult;  //TODO 不要依赖client去赋值

    private String type;                //任务类型 TODO
    private Object metadata;            //task元数据：可以是Json或其他  TODO
    private String metadataParserKey;  //元数据解析器 TODO
    private long maxRuntimeInSec = -1;   //最长运行时间, -1:表示无时间限制 TODO

    /**
     * @param id          nodeTaskId
     * @param dependences 依赖
     */
    public NodeTask(String id, Set<String> dependences) {
        this.id = id;
        this.dependences = dependences;
    }

    public NodeTask(String id) {
        this.id = id;
    }

    public void validate() {
        if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(parentId)) {
            throw new RuntimeException("NodeTask validate fail.");
        }
    }
}

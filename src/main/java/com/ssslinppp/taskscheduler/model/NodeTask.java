package com.ssslinppp.taskscheduler.model;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public abstract class NodeTask implements Callable<NodeTaskResult> {
    private String parentId;
    private String id;                   //唯一标示
    private Set<String> dependences = Sets.newConcurrentHashSet();   //需要依赖的nodeTask
    private NodeTaskStatus nodeTaskStatus = NodeTaskStatus.init;
    private NodeTaskResult nodeTaskResult;

    private String type;                //任务类型 TODO
    private Object metadata;            //task元数据：可以是Json或其他  TODO
    private String metadataParserKey;  //元数据解析器 TODO
    private long maxRuntimeInSec = -1;   //最长运行时间, -1:表示无时间限制 TODO

    public NodeTask(String parentId, String id, Set<String> dependences) {
        this.parentId = parentId;
        this.id = id;
        this.dependences = dependences;
    }

    public void validate() {
        if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(parentId)) {
            throw new RuntimeException("NodeTask validate fail.");
        }
    }
}

package com.ssslinppp.taskscheduler.model;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.ssslinppp.taskscheduler.manager.INodeTaskWork;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @param <T> the result type of method {@code doNodeTaskWork}
 */
@Data
@AllArgsConstructor
public abstract class NodeTask<T> implements Callable<NodeTaskResult>, INodeTaskWork<T> {
    private String parentId;
    private String id;                   //唯一标示
    private Set<String> dependences = Sets.newConcurrentHashSet();   //依赖的nodeTask id
    private NodeTaskStatus nodeTaskStatus = NodeTaskStatus.init;
    private NodeTaskResult nodeTaskResult;

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

    @Override
    public NodeTaskResult call() throws Exception {
        T result = doNodeTaskWork();

        NodeTaskResult nodeTaskResult = new NodeTaskResult();
        nodeTaskResult.setId(this.id);
        nodeTaskResult.setResult(result);
        return nodeTaskResult;
    }

    public void validate() {
        if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(parentId)) {
            throw new RuntimeException("NodeTask validate fail.");
        }
    }
}

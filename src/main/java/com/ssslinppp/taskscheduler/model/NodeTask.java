package com.ssslinppp.taskscheduler.model;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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
    private String type;                //任务类型

    private Object metadata;            //task元数据：可以是Json或其他
    private String metadataParserKey;  //元数据解析器

    private List<String> dependences = Lists.newCopyOnWriteArrayList();   //需要依赖的nodeTask

    private NodeTaskStatus nodeTaskStatus = NodeTaskStatus.init;
    private NodeTaskResult nodeTaskResult;

    public NodeTask(String parentId, String id, List<String> dependences) {
        this.parentId = parentId;
        this.id = id;
        this.dependences = dependences;
    }

    public void validate() {
        if (Strings.isNullOrEmpty(id)) {
            throw new RuntimeException("NodeTask validate fail.");
        }
    }
}

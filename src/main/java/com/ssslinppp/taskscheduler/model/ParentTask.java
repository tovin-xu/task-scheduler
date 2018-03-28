package com.ssslinppp.taskscheduler.model;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/3/27 , Time: 17:43 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
public class ParentTask {
    private String id;
    private List<NodeTask> nodeTasks = Lists.newArrayList();
    private AtomicInteger nodeTskSuccCount = new AtomicInteger(0);
    private volatile boolean isTaskFail = false;

    public int nodeTaskSuccess() {
        return nodeTskSuccCount.addAndGet(1);
    }

    public void nodeTaskFail() {
        this.setTaskFail(false);
    }


    public boolean isParentTaskFailOrFinish() {
        if (isTaskFail || nodeTskSuccCount.get() == nodeTasks.size()) {
            return true;
        }

        return false;
    }

    public void validate() {
        if (Strings.isNullOrEmpty(id) || CollectionUtils.isEmpty(nodeTasks)) {
            throw new RuntimeException("ParentTask validate fail.");
        }
    }

    public void addNodeTask(NodeTask nodeTask) {
        this.getNodeTasks().add(nodeTask);
    }

    public void addNodeTasks(List<NodeTask> nodeTasks) {
        this.getNodeTasks().addAll(nodeTasks);
    }

    public void initNodeTasks(List<NodeTask> nodeTasks) {
        this.setNodeTasks(nodeTasks);
    }
}

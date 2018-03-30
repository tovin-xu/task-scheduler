package com.ssslinppp.taskscheduler.model;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.ssslinppp.taskscheduler.manager.ITaskStatusListener;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/3/27 , Time: 17:43 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@Builder
public class ParentTask {
    private String id;
    private Map<String, NodeTask> nodeTasks = Maps.newConcurrentMap();
    private AtomicInteger nodeTskSuccCount;  //成功结束的NodeTask个数
    private volatile boolean isTaskFail = false;
    private ITaskStatusListener taskStatusListener;

    public void validate() {
        if (Strings.isNullOrEmpty(id) || CollectionUtils.isEmpty(nodeTasks)) {
            throw new RuntimeException("ParentTask validate fail.");
        }
    }

    public NodeTask getNodeTask(String nodeTaskId) {
        return nodeTasks.get(nodeTaskId);
    }

    public int nodeTaskSuccess() {
        if (nodeTskSuccCount == null) {
            synchronized (this) {
                if (nodeTskSuccCount == null) {
                    nodeTskSuccCount = new AtomicInteger(0);
                }
            }
        }

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

    public double progress() {
        validate();
        return (double) nodeTskSuccCount.get() / nodeTasks.size();
    }
}

package com.ssslinppp.taskscheduler.manager;

import com.google.common.util.concurrent.FutureCallback;
import com.ssslinppp.taskscheduler.model.NodeTaskResult;
import com.ssslinppp.taskscheduler.model.NodeTaskStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/3/28 , Time: 9:35 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
@Getter
@Setter
public class NodeTaskExecCallback implements FutureCallback<NodeTaskResult> {
    private String parentTaskId;
    private String nodeTaskId;

    public NodeTaskExecCallback(String parentTaskId, String nodeTaskId) {
        this.parentTaskId = parentTaskId;
        this.nodeTaskId = nodeTaskId;
    }

    @Override
    public void onSuccess(NodeTaskResult result) {
        System.out.println("Success: " + result);
        TaskManager.instance.updateNodeTaskStatus(parentTaskId, nodeTaskId, NodeTaskStatus.success);
        TaskExecutor.instance.addNodeTaskResultToTail(parentTaskId, result);  // 添加执行结果到 BlockingQueue

        if (TaskManager.instance.isParentTaskFailOrFinish(parentTaskId)) {
            TaskScheduleManager.instance.cancelParentTskSchedule(parentTaskId);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        System.out.println("NodeTask(parentTaskId:" + parentTaskId + ", nodeTaskId:" + nodeTaskId + ") exception: " + t.getMessage());
        TaskManager.instance.updateNodeTaskStatus(parentTaskId, nodeTaskId, NodeTaskStatus.fail);
        TaskScheduleManager.instance.cancelParentTskSchedule(parentTaskId);
    }
}

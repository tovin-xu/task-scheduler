package com.ssslinppp.taskscheduler.manager;

import com.google.common.util.concurrent.FutureCallback;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskResult;
import com.ssslinppp.taskscheduler.model.NodeTaskStatus;
import com.ssslinppp.taskscheduler.model.ParentTask;
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
    public void onSuccess(NodeTaskResult result) {//TODO 并发问题：当
        TaskManager.instance.updateNodeTaskStatus(parentTaskId, nodeTaskId, NodeTaskStatus.success); //TODO: 可能会抛出异常

        // 添加执行结果到 BlockingQueue
        TaskExecutor.instance.addNodeTaskResultToTail(parentTaskId, result);

        ParentTask parentTask = TaskManager.instance.getParentTask(parentTaskId);
        if (parentTask == null) { //可能是其他NodeTask异常，导致整个ParentTask结束
            return;
        }

        NodeTask nodeTask = TaskManager.instance.getNodeTask(parentTaskId, nodeTaskId);

        if (parentTask.getTaskStatusListener() != null) {//触发监听器
            parentTask.getTaskStatusListener().process(parentTask.progress(), nodeTask, result);
        }

        // 判断parentTask是否执行结束
        if (TaskManager.instance.isParentTaskFailOrFinish(parentTaskId)) {
            TaskScheduleManager.instance.cancelParentTskSchedule(parentTaskId);
        }

    }

    @Override
    public void onFailure(Throwable t) {//TODO: 可能会抛出异常
        System.out.println("NodeTask(parentTaskId:" + parentTaskId + ", nodeTaskId:" + nodeTaskId + ") exception: " + t.getMessage());
        TaskManager.instance.updateNodeTaskStatus(parentTaskId, nodeTaskId, NodeTaskStatus.fail);
        TaskScheduleManager.instance.cancelParentTskSchedule(parentTaskId);
    }
}

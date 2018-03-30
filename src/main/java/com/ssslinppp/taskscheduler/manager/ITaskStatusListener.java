package com.ssslinppp.taskscheduler.manager;

import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskResult;

/**
 * 用于主任务状态检查
 */
public interface ITaskStatusListener {
    /**
     * 当nodeTask执行success后，会触发该方法
     *
     * @param process  总任务进度
     * @param nodeTask 当前执行success的NodeTask
     */
    void process(double process, NodeTask nodeTask, NodeTaskResult taskResult);
}

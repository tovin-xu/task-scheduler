package com.ssslinppp.taskscheduler.manager;

import com.ssslinppp.taskscheduler.model.NodeTaskResult;

/**
 * 用于主任务状态检查
 */
public interface ITaskStatusListener {
    /**
     * 当nodeTask执行success后，会触发该方法
     *
     * @param process    总任务进度
     * @param nodeTaskId 当前执行success的NodeTaskId
     */
    void process(double process, String nodeTaskId, NodeTaskResult taskResult);

    void onFail(String nodeTaskId, Throwable t);
}

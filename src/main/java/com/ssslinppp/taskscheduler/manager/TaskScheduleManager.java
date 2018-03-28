package com.ssslinppp.taskscheduler.manager;

import com.google.common.collect.Maps;
import com.ssslinppp.taskscheduler.model.ParentTask;

import java.util.Map;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/3/27 , Time: 22:15 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
public enum TaskScheduleManager {
    instance;

    private Map<String, Thread> taskScheduleThreadMap = Maps.newConcurrentMap();


    /**
     * 开始ParentTask调度
     *
     * @param parentTask
     */
    public void startParentTask(ParentTask parentTask) {
        if (taskScheduleThreadMap.get(parentTask.getId()) == null) {
            synchronized (taskScheduleThreadMap) {
                TaskManager.instance.addTask(parentTask);

                Thread scheduleThread = new Thread(() -> {
                    TaskExecutor.instance.startTaskSchedule(parentTask.getId());
                });
                taskScheduleThreadMap.put(parentTask.getId(), scheduleThread);
                scheduleThread.start();
            }
        } else {
            throw new RuntimeException("Duplicate start parentTask:" + parentTask.getId());
        }
    }

    /**
     * 取消 ParentTask 调度
     *
     * @param parentTaskId
     */
    public void cancelParentTskSchedule(String parentTaskId) {
        if (taskScheduleThreadMap.get(parentTaskId) != null) {
            taskScheduleThreadMap.get(parentTaskId).interrupt();
        }
    }
}

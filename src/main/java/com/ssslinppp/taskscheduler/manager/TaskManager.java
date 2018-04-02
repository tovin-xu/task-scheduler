package com.ssslinppp.taskscheduler.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskStatus;
import com.ssslinppp.taskscheduler.model.ParentTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * !!!注意：该类声明时，没有添加{@code public}属性，是为了保证package可见==>该类只可供{@link TaskScheduleManager}使用
 * <p>
 * 负责管理所有Task(以及NodeTask)的各种状态;
 * <p>
 */
enum TaskManager {
    instance;

    private static Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private Map<String, ParentTask> parentTasks = Maps.newConcurrentMap();//维护整个任务的状态

    public void clearTask(String parentTaskId) {
        parentTasks.remove(parentTaskId);
    }

    /**
     * 当 ParentTask finish or fail，则不会更新NodeTask的状态
     *
     * @param parentTaskId
     * @param nodeTaskId
     * @param nodeTaskStatus
     * @return false: 说明 ParentTask已经失败
     */
    public boolean updateNodeTaskStatus(String parentTaskId, String nodeTaskId, NodeTaskStatus nodeTaskStatus) {
        if (Strings.isNullOrEmpty(parentTaskId) || Strings.isNullOrEmpty(nodeTaskId) || nodeTaskStatus == null) {
            throw new RuntimeException("updateNodeTaskStatus: params can not be null");
        }

        ParentTask parentTask = getParentTask(parentTaskId);
        if (parentTask == null) {
            logger.warn("parentTask has finish [or] any nodeTask exception,parentTaskId: {}, nodeTaskId: {}", parentTaskId, nodeTaskId);
            return false;
        }

        NodeTask nodeTask = parentTask.getNodeTask(nodeTaskId);
        if (nodeTask == null) {
            throw new RuntimeException("No nodeTask(parentTaskId: " + parentTaskId + ", nodeTaskId: " + nodeTaskId + ")");
        }
        nodeTask.setNodeTaskStatus(nodeTaskStatus);

        if (nodeTaskStatus == NodeTaskStatus.success) {
            parentTask.nodeTaskSuccess();
        } else if (nodeTaskStatus == NodeTaskStatus.fail) {
            parentTask.nodeTaskFail();
        }

        return true;
    }

    public boolean isParentTaskFailOrFinish(String parentTaskId) {
        try {
            ParentTask parentTask = getParentTask(parentTaskId);
            if (parentTask == null) {
                logger.warn("parentTask has finish [or] any nodeTask exception,parentTaskId: {}", parentTaskId);
                return true;
            }
            return parentTask.isParentTaskFailOrFinish();
        } catch (Exception e) {//查询不到时，表明success或NodeTask抛出异常
            return true;
        }
    }

    /**
     * 如果查询不到(返回NULL)，则说明ParentTask success finish 或 any nodeTask exception
     * <p>
     * 特别说明： 凡是调用此方法，都需要对null进行判断并处理
     *
     * @param parentTaskId
     * @return 可能返回NUlL
     */
    public ParentTask getParentTask(String parentTaskId) {
        if (Strings.isNullOrEmpty(parentTaskId)) {
            throw new RuntimeException("parentTaskId can not be null");
        }

        ParentTask parentTask = parentTasks.get(parentTaskId);
        if (parentTask == null) {
            logger.warn("parentTask has finish [or] any nodeTask exception,parentTaskId: {}", parentTaskId);
        }

        return parentTask;
    }

    /**
     * 当ParentTask完成或NodeTask异常，可能返回null
     * <p>
     * 特别说明： 凡是调用此方法，都需要对null进行判断并处理
     *
     * @param parentTaskId
     * @param nodeTaskId
     * @return 可能返回null
     */
    private NodeTask getNodeTask(String parentTaskId, String nodeTaskId) {
        if (Strings.isNullOrEmpty(parentTaskId) || Strings.isNullOrEmpty(nodeTaskId)) {
            throw new RuntimeException("parentTaskId or nodeTaskId can not be null");
        }

        ParentTask parentTask = getParentTask(parentTaskId);
        if (parentTask == null) {
            logger.warn("parentTask has finish [or] any nodeTask exception,parentTaskId: {}, nodeTaskId: ", parentTaskId, nodeTaskId);
            return null;
        }

        return parentTask.getNodeTask(nodeTaskId);
    }

    /**
     * 当前任务是否可以进行调度
     *
     * @param parentTaskId
     * @param nodeTaskId
     * @return
     */
    public boolean canNodeTaskSchedule(String parentTaskId, String nodeTaskId) {
        if (Strings.isNullOrEmpty(parentTaskId) || Strings.isNullOrEmpty(nodeTaskId)) {
            throw new RuntimeException("param can not be null");
        }

        NodeTask nodeTask = getNodeTask(parentTaskId, nodeTaskId);
        if (ObjectUtils.isEmpty(nodeTask)) {
            throw new RuntimeException("parentTask has finish [or] any nodeTask exception,parentTaskId: " + parentTaskId);
        }

        if (CollectionUtils.isEmpty(nodeTask.getDependences())) {
            return true;
        }

        // 判断依赖NodeTask是否执行完成
        for (Object dependTaskId : nodeTask.getDependences()) {
            NodeTask dependTask = getNodeTask(parentTaskId, (String) dependTaskId);
            if (ObjectUtils.isEmpty(dependTask)) {
                throw new RuntimeException("parentTask has finish [or] any nodeTask exception,parentTaskId: " + parentTaskId);
            }

            if (dependTask.getNodeTaskStatus() != NodeTaskStatus.success) {
                return false;
            }
        }

        return true;
    }

    public List<NodeTask> nodeTasksToBeScheduled(String parentTaskId) {
        List<NodeTask> nodeTasks = Lists.newArrayList();
        ParentTask parentTask = getParentTask(parentTaskId);
        if (parentTask == null) {
            logger.warn("parentTask has finish [or] any nodeTask exception,parentTaskId: {}", parentTaskId);
            return nodeTasks;
        }

        for (NodeTask nodeTask : parentTask.getNodeTasks().values()) {
            if (nodeTask.getNodeTaskStatus() == NodeTaskStatus.init) {
                nodeTasks.add(nodeTask);
            }
        }
        return nodeTasks;
    }

    /**
     * 获取没有依赖的NodeTasks
     *
     * @param parentTaskId
     * @return
     */
    public List<NodeTask> getNoDependentNodeTasks(String parentTaskId) {
        List<NodeTask> nodeTasks = Lists.newArrayList();
        ParentTask parentTask = getParentTask(parentTaskId);
        if (parentTask == null) {
            return nodeTasks;
        }

        for (NodeTask nodeTask : parentTask.getNodeTasks().values()) {
            if (CollectionUtils.isEmpty(nodeTask.getDependences())) {
                nodeTasks.add(nodeTask);
            }
        }

        return nodeTasks;
    }

    public void addTask(ParentTask parentTask) {
        parentTask.validate();

        // 判断是否重复
        if (parentTasks.get(parentTask.getId()) != null) {
            throw new RuntimeException("ParentTask( id: " + parentTask.getId() + ") has exist, please change the parentTask id");
        }
        parentTasks.put(parentTask.getId(), parentTask);
    }

}

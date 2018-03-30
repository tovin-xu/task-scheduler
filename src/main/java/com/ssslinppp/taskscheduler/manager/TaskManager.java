package com.ssslinppp.taskscheduler.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskStatus;
import com.ssslinppp.taskscheduler.model.ParentTask;
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

    /**
     * 维护 ParentTask 和 NodeTasks 的整个状态信息
     */
//    private Map<String, Map<String, NodeTask>> nodeTasks = Maps.newConcurrentMap();

    /**
     * 用于判断 ParentTask 是否执行结束or失败
     */
    private Map<String, ParentTask> parentTasks = Maps.newConcurrentMap();

    public void clearTask(String parentTaskId) {
        parentTasks.remove(parentTaskId);
//        nodeTasks.remove(parentTaskId);
    }

    public void updateNodeTaskStatus(String parentTaskId, String nodeTaskId, NodeTaskStatus nodeTaskStatus) {
        if (Strings.isNullOrEmpty(parentTaskId) || Strings.isNullOrEmpty(nodeTaskId) || nodeTaskStatus == null) {
            throw new RuntimeException("updateNodeTaskStatus: params can not be null");
        }

        ParentTask parentTask = getParentTask(parentTaskId); //查询不到时，会抛出异常.

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
    }

    public boolean isParentTaskFailOrFinish(String parentTaskId) {
        try {
            ParentTask parentTask = getParentTask(parentTaskId); //查询不到时，会抛出异常
            return parentTask.isParentTaskFailOrFinish();
        } catch (Exception e) {//查询不到时，表明success或NodeTask抛出异常
            return true;
        }
    }

    /**
     * 如果查询不到，则抛出异常【不会返回null】
     *
     * @param parentTaskId
     * @return
     * @throws RuntimeException
     */
    public ParentTask getParentTask(String parentTaskId) throws RuntimeException {
        if (Strings.isNullOrEmpty(parentTaskId)) {
            throw new RuntimeException("parentTaskId can not be null");
        }

        ParentTask parentTask = parentTasks.get(parentTaskId);
        if (parentTask == null) {
            throw new RuntimeException("parentTask has finish [or] any nodeTask exception: [parentTaskId:" + parentTaskId + "]");
        }

        return parentTask;
    }

    /**
     * * 当ParentTask完成或NodeTask异常，可能返回null
     *
     * @param parentTaskId
     * @param nodeTaskId
     * @return 可能返回null
     * @throws RuntimeException
     */
    public NodeTask getNodeTask(String parentTaskId, String nodeTaskId) throws RuntimeException {
        if (Strings.isNullOrEmpty(parentTaskId) || Strings.isNullOrEmpty(nodeTaskId)) {
            throw new RuntimeException("parentTaskId or nodeTaskId can not be null");
        }

        ParentTask parentTask = getParentTask(parentTaskId);
        if (parentTask == null) {//其他NodeTask失败，导致ParentTask被删除
            throw new RuntimeException("parentTask has finish [or] any nodeTask exception:" +
                    "[parentTaskId:" + parentTaskId + ", nodeTaskId: " + nodeTaskId + "]");
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
            throw new RuntimeException("No nodeTask(parentTaskId: " + parentTaskId + ", nodeTaskId: " + nodeTaskId + ")");
        }

        if (CollectionUtils.isEmpty(nodeTask.getDependences())) {
            return true;
        }

        // 判断依赖NodeTask是否执行完成
        for (String dependTaskId : nodeTask.getDependences()) {
            NodeTask dependTask = getNodeTask(parentTaskId, dependTaskId);
            if (ObjectUtils.isEmpty(dependTask)) {
                throw new RuntimeException("No nodeTask(parentTaskId: " + parentTaskId + ", nodeTaskId: " + dependTaskId + ")");
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

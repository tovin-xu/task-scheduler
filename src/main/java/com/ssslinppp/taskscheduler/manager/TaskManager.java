package com.ssslinppp.taskscheduler.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskStatus;
import com.ssslinppp.taskscheduler.model.ParentTask;
import org.springframework.util.CollectionUtils;

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
    private Map<String, Map<String, NodeTask>> nodeTasks = Maps.newConcurrentMap();

    /**
     * 用于判断 ParentTask 是否执行结束or失败
     */
    private Map<String, ParentTask> parentTasks = Maps.newConcurrentMap();

    public void clearTask(String parentTaskId) {
        parentTasks.remove(parentTaskId);
        nodeTasks.remove(parentTaskId);
    }

    public void updateNodeTaskStatus(String parentTaskId, String nodeTaskId, NodeTaskStatus nodeTaskStatus) {
        if (Strings.isNullOrEmpty(parentTaskId) || Strings.isNullOrEmpty(nodeTaskId) || nodeTaskStatus == null) {
            throw new RuntimeException("updateNodeTaskStatus: params can not be null");
        }

        if (nodeTasks.get(parentTaskId) != null) {
            NodeTask nodeTask = nodeTasks.get(parentTaskId).get(nodeTaskId);
            if (nodeTask == null) {
                throw new RuntimeException("can not find nodeTask{ parentTaskId: " + parentTaskId + ", nodeTaskId: " + nodeTaskId + "}");
            }

            nodeTask.setNodeTaskStatus(nodeTaskStatus);
        }

        if (nodeTaskStatus == NodeTaskStatus.success) {
            if (getParentTask(parentTaskId) != null) {
                getParentTask(parentTaskId).nodeTaskSuccess();
            }
        } else if (nodeTaskStatus == NodeTaskStatus.fail) {
            if (getParentTask(parentTaskId) != null) {
                getParentTask(parentTaskId).nodeTaskFail();
            }
        }
    }

    public boolean isParentTaskFailOrFinish(String parentTaskId) {
        if (getParentTask(parentTaskId) != null) {
            return getParentTask(parentTaskId).isParentTaskFailOrFinish();
        } else {
            return true;
        }
    }

    private ParentTask getParentTask(String parentTaskId) {
        if (Strings.isNullOrEmpty(parentTaskId)) {
            throw new RuntimeException("parentTaskId can not be null");
        }

        return parentTasks.get(parentTaskId);
    }

    public boolean canNodeTaskSchedule(String parentTaskId, String nodeTaskId) {
        if (Strings.isNullOrEmpty(parentTaskId) || Strings.isNullOrEmpty(nodeTaskId)) {
            throw new RuntimeException("param can not be null");
        }

        NodeTask nodeTask = nodeTasks.get(parentTaskId).get(nodeTaskId);
        if (CollectionUtils.isEmpty(nodeTask.getDependences())) {
            return true;
        }

        // 判断依赖NodeTask是否执行完成
        for (String dependTaskId : nodeTask.getDependences()) {
            if (nodeTasks.get(parentTaskId).get(dependTaskId) == null) {
                throw new RuntimeException("can not find nodeTask{ parentTaskId: " + parentTaskId + ", nodeTaskId: " + nodeTaskId + "}");
            }

            if (nodeTasks.get(parentTaskId).get(dependTaskId).getNodeTaskStatus() != NodeTaskStatus.success) {
                return false;
            }
        }

        return true;
    }

    public List<NodeTask> nodeTasksToBeScheduled(String parentTaskId) {
        List<NodeTask> nodeTasks = Lists.newArrayList();
        for (NodeTask nodeTask : this.nodeTasks.get(parentTaskId).values()) {
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
        for (NodeTask nodeTask : this.nodeTasks.get(parentTaskId).values()) {
            if (CollectionUtils.isEmpty(nodeTask.getDependences())) {
                nodeTasks.add(nodeTask);
            }
        }

        return nodeTasks;
    }

    public void addTask(ParentTask parentTask) {
        parentTask.validate();

        if (parentTasks.get(parentTask.getId()) != null) {
            throw new RuntimeException("ParentTask( id: " + parentTask.getId() + ") has exist, please change the parentTask id");
        }
        parentTasks.put(parentTask.getId(), parentTask);

        if (nodeTasks.get(parentTask.getId()) == null) {
            synchronized (nodeTasks) {
                if (nodeTasks.get(parentTask.getId()) == null) {
                    Map<String, NodeTask> nodeTaskMap = Maps.newConcurrentMap();
                    for (NodeTask nodeTask : parentTask.getNodeTasks()) {
                        nodeTask.validate();
                        if (nodeTaskMap.get(nodeTask.getId()) != null) {
                            throw new RuntimeException("nodeTask id(" + nodeTask.getId() + ") duplication, please change the nodeTask id");
                        }
                        nodeTaskMap.put(nodeTask.getId(), nodeTask);
                    }
                    nodeTasks.put(parentTask.getId(), nodeTaskMap);
                }
            }
        }
    }

}

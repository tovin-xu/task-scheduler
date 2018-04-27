package com.ssslinppp.taskscheduler.manager;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskResult;
import com.ssslinppp.taskscheduler.model.NodeTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;

/**
 * !!!注意：该类声明时，没有添加{@code public}属性，是为了保证package可见==>该类只可供{@link TaskScheduleManager}使用
 * <p>
 * 任务调度思路概述：
 * <pre>
 * 1. ParentTask: 主任务，下面包含若干NodeTask（注：all nodeTask是一个DAG，有向无环图）；
 * 2. NodeTask: 节点任务；
 * 3. 调度思路：基于“生产者-消费者模式”进行调度
 *   3.0: ParentTask调度：每个parentTask都是新启单独的Thread进行调度；
 *   3.1: NodeTask执行结束后，都会向BlockingQueue中put一个element
 *   3.2: ParentTask调度线程循环监控该BlockingQueue，一旦获取到element就执行NodeTask调度；
 *   3.3: NodeTask调度：当该NodeTask所依赖的其他NodeTask都执行完成后，就可以进行调度，并发调度；
 *   3.4: NodeTask执行失败：TODO 待完善
 *  </pre>
 */
enum TaskExecutor {
    instance;

    private static Logger logger = LoggerFactory.getLogger(NodeTaskExecCallback.class);

    private ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()));

    private Map<String, BlockingQueue<NodeTaskResult>> tasksScheduleQueueMap = Maps.newConcurrentMap();

    private void clear(String parentTaskId) {
        TaskManager.instance.clearTask(parentTaskId);

        tasksScheduleQueueMap.remove(parentTaskId);
    }

    // 添加 task 执行
    public void addNodeTaskResultToTail(String parentTaskId, NodeTaskResult nodeTaskResult) {
        if (ObjectUtils.isEmpty(nodeTaskResult) || Strings.isNullOrEmpty(parentTaskId)) {
            return;
        }

        try {
            BlockingQueue<NodeTaskResult> blockingQueue = tasksScheduleQueueMap.get(parentTaskId);
            if (blockingQueue != null) {
                blockingQueue.put(nodeTaskResult);   //插入到队尾
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化BlockingQueue
     *
     * @param parentTaskId
     */
    private void initTaskScheduleQueue(String parentTaskId) {
        if (tasksScheduleQueueMap.get(parentTaskId) == null) {
            synchronized (tasksScheduleQueueMap) {
                if (tasksScheduleQueueMap.get(parentTaskId) == null) {
                    BlockingQueue<NodeTaskResult> queue = Queues.newLinkedBlockingQueue();
                    tasksScheduleQueueMap.put(parentTaskId, queue);
                }
            }
        }
    }

    /**
     * 运行没有依赖的NodeTasks
     *
     * @param parentTaskId
     */
    private void runNoDependentNodeTasks(String parentTaskId) {
        List<NodeTask> noDependentNodeTasks = TaskManager.instance.getNoDependentNodeTasks(parentTaskId);
        if (CollectionUtils.isEmpty(noDependentNodeTasks)) {
            throw new RuntimeException("ParentTask init error, nodeTasks may be not dag(有向无环图)");
        }

        for (NodeTask nodeTask : noDependentNodeTasks) {
            TaskManager.instance.updateNodeTaskStatus(parentTaskId, nodeTask.getId(), NodeTaskStatus.running);
            ListenableFuture future = pool.submit(nodeTask);
            Futures.addCallback(future, new NodeTaskExecCallback(parentTaskId, nodeTask.getId()));
        }
    }

    /**
     * 开启任务调度 <br/>
     * 对于每个 parentTask，都需要新启一个独立的Thread去调度；
     * <p>
     * 该方法只能由{@link TaskScheduleManager}调用，对client端屏蔽（使用package可见）
     * <p>
     * 对于一个parentTaskId，无论何时都会保证【最多】只会有1个Thread执行
     *
     * @param parentTaskId
     */
    public void startTaskSchedule(String parentTaskId) {
        initTaskScheduleQueue(parentTaskId);

        BlockingQueue<NodeTaskResult> taskScheduleQueue = tasksScheduleQueueMap.get(parentTaskId);

        runNoDependentNodeTasks(parentTaskId);

        while (true) {
            try {
                // 若获取到，说明有NodeTask已经执行完成
                taskScheduleQueue.take(); //从队列头获取

                List<NodeTask> nodeTasksToScheduled = TaskManager.instance.nodeTasksToBeScheduled(parentTaskId);
                if (CollectionUtils.isEmpty(nodeTasksToScheduled)) {//说明parentTask finish or nodeTask Exception
                    break;
                }

                for (NodeTask nodeTask : nodeTasksToScheduled) {
                    boolean canSchedule = TaskManager.instance.canNodeTaskSchedule(parentTaskId, nodeTask.getId());

                    if (canSchedule) {
                        //说明parentTask失败
                        if (!TaskManager.instance.updateNodeTaskStatus(parentTaskId, nodeTask.getId(), NodeTaskStatus.running)) {
                            break;
                        }
                        ListenableFuture future = pool.submit(nodeTask);
                        Futures.addCallback(future, new NodeTaskExecCallback(parentTaskId, nodeTask.getId()));
                    }
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {//确保其他异常出现时，可以立即终止 线程调度
                break;
            }
        }/** end of  while (true)**/
        logger.info("### parentTask:{} ,【scheduler finish or fail】, thread exit", parentTaskId);
        clear(parentTaskId);
    }
}

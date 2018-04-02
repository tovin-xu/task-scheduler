package com.ssslinppp.taskscheduler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ssslinppp.taskscheduler.manager.TaskScheduleManager;
import com.ssslinppp.taskscheduler.model.NodeTask;
import org.junit.Test;

import java.util.Map;

public class TaskScheduleManagerTests {

    @Test
    public void testSingleTypeTask() {
        NodeTask nodeTaskA = new AAANodeTask(3_000, "nodeA", null);
        NodeTask nodeTaskB = new AAANodeTask(8_000, "nodeB", null);
        NodeTask nodeTaskC = new AAANodeTask(2_000, "nodeC", Sets.newHashSet(nodeTaskA.getId()));
        NodeTask nodeTaskD = new AAANodeTask(3_000, "nodeD", Sets.newHashSet(nodeTaskB.getId()));
        NodeTask nodeTaskE = new AAANodeTask(5_000, "nodeE", Sets.newHashSet(nodeTaskC.getId(), nodeTaskD.getId()));
        NodeTask nodeTaskF = new AAANodeTask(3_000, "nodeF", Sets.newHashSet(nodeTaskE.getId()));
        NodeTask nodeTaskG = new AAANodeTask(5_000, "nodeG", Sets.newHashSet(nodeTaskE.getId()));

        Map<String, NodeTask> nodeTaskMap = Maps.newConcurrentMap();
        nodeTaskMap.put(nodeTaskA.getId(), nodeTaskA);
        nodeTaskMap.put(nodeTaskB.getId(), nodeTaskB);
        nodeTaskMap.put(nodeTaskC.getId(), nodeTaskC);
        nodeTaskMap.put(nodeTaskD.getId(), nodeTaskD);
        nodeTaskMap.put(nodeTaskE.getId(), nodeTaskE);
        nodeTaskMap.put(nodeTaskF.getId(), nodeTaskF);
        nodeTaskMap.put(nodeTaskG.getId(), nodeTaskG);

        /////////////////////////
        TaskScheduleManager.instance.startNodeTasks(nodeTaskMap, new MyTaskStatusListener());
        while (true) ;
    }

}

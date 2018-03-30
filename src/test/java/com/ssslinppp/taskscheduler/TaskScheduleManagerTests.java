package com.ssslinppp.taskscheduler;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ssslinppp.taskscheduler.manager.TaskScheduleManager;
import com.ssslinppp.taskscheduler.model.NodeTask;
import org.junit.Test;

import java.util.Map;

public class TaskScheduleManagerTests {

    @Test
    public void contextLoads() {
        String parentTaskId = "parentTaskId-A0001";
        String nodeA = "nodeA";
        String nodeB = "nodeB";
        String nodeC = "nodeC";
        String nodeD = "nodeD";
        String nodeE = "nodeE";
        String nodeF = "nodeF";
        String nodeG = "nodeG";

        NodeTask nodeTaskA = new AAANodeTask(nodeA + "_name", 3_000, parentTaskId, nodeA, null);
        NodeTask nodeTaskB = new AAANodeTask(nodeB + "_name", 8_000, parentTaskId, nodeB, null);
        NodeTask nodeTaskC = new AAANodeTask(nodeC + "_name", 2_000, parentTaskId, nodeC, Sets.newHashSet(nodeA));
        NodeTask nodeTaskD = new AAANodeTask(nodeD + "_name", 3_000, parentTaskId, nodeD, Sets.newHashSet(nodeB));
        NodeTask nodeTaskE = new AAANodeTask(nodeE + "_name", 5_000, parentTaskId, nodeE, Sets.newHashSet(nodeC, nodeD));
        NodeTask nodeTaskF = new AAANodeTask(nodeF + "_name", 3_000, parentTaskId, nodeF, Sets.newHashSet(nodeE));
        NodeTask nodeTaskG = new AAANodeTask(nodeG + "_name", 5_000, parentTaskId, nodeG, Sets.newHashSet(nodeE));

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

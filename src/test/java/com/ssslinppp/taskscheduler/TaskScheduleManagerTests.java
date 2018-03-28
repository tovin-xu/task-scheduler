package com.ssslinppp.taskscheduler;

import com.ssslinppp.taskscheduler.manager.TaskScheduleManager;
import com.ssslinppp.taskscheduler.testmodel.AAANodeTask;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.ParentTask;
import org.junit.Test;

import java.util.Arrays;

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

        NodeTask nodeTaskA = new AAANodeTask(nodeA + "_name", 5_000, parentTaskId, nodeA, null);
        NodeTask nodeTaskB = new AAANodeTask(nodeB + "_name", 5_000, parentTaskId, nodeB, null);
        NodeTask nodeTaskC = new AAANodeTask(nodeC + "_name", 5_000, parentTaskId, nodeC, Arrays.asList(nodeA));
        NodeTask nodeTaskD = new AAANodeTask(nodeD + "_name", 5_000, parentTaskId, nodeD, Arrays.asList(nodeB));
        NodeTask nodeTaskE = new AAANodeTask(nodeE + "_name", 5_000, parentTaskId, nodeE, Arrays.asList(nodeC, nodeD));
        NodeTask nodeTaskF = new AAANodeTask(nodeF + "_name", 5_000, parentTaskId, nodeF, Arrays.asList(nodeE));
        NodeTask nodeTaskG = new AAANodeTask(nodeG + "_name", 5_000, parentTaskId, nodeG, Arrays.asList(nodeE));


        ParentTask parentTask = new ParentTask();
        parentTask.setId(parentTaskId);
        parentTask.addNodeTask(nodeTaskA);
        parentTask.addNodeTask(nodeTaskB);
        parentTask.addNodeTask(nodeTaskC);
        parentTask.addNodeTask(nodeTaskD);
        parentTask.addNodeTask(nodeTaskE);
        parentTask.addNodeTask(nodeTaskF);
        parentTask.addNodeTask(nodeTaskG);

        /////////////////////////
        TaskScheduleManager.instance.startParentTask(parentTask);
        while (true);
    }

}

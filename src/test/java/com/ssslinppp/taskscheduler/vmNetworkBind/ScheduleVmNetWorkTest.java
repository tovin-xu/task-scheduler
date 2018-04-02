package com.ssslinppp.taskscheduler.vmNetworkBind;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ssslinppp.taskscheduler.manager.TaskScheduleManager;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.vmNetworkBind.tasks.BindVMAndNetWorkTask;
import com.ssslinppp.taskscheduler.vmNetworkBind.tasks.CreataNetWorkTask;
import com.ssslinppp.taskscheduler.vmNetworkBind.tasks.CreateVMTask;
import org.junit.Test;

import java.util.Map;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/4/2 , Time: 11:20 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleVmNetWorkTest {
    @Test
    public void contextLoads() {
        NodeTask createVMTask = new CreateVMTask("createVMTask");
        NodeTask creataNetWorkTask = new CreataNetWorkTask("creataNetWorkTask");
        NodeTask bindVMAndNetWorkTask = new BindVMAndNetWorkTask("bindVMAndNetWorkTask",
                Sets.newHashSet(createVMTask.getId(), creataNetWorkTask.getId()));

        Map<String, NodeTask> nodeTaskMap = Maps.newConcurrentMap();
        nodeTaskMap.put(createVMTask.getId(), createVMTask);
        nodeTaskMap.put(creataNetWorkTask.getId(), creataNetWorkTask);
        nodeTaskMap.put(bindVMAndNetWorkTask.getId(), bindVMAndNetWorkTask);

        /////////////////////////
        TaskScheduleManager.instance.startNodeTasks(nodeTaskMap, new VmNetListener());
        while (true) ;
    }
}

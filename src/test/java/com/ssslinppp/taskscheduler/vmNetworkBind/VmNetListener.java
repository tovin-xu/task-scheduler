package com.ssslinppp.taskscheduler.vmNetworkBind;

import com.ssslinppp.taskscheduler.manager.ITaskStatusListener;
import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskResult;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/4/2 , Time: 11:18 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
public class VmNetListener implements ITaskStatusListener {
    @Override
    public void process(double process, NodeTask nodeTask, NodeTaskResult taskResult) {
        System.out.println("nodeTask: [" + nodeTask.getId() + "] success , 当前进度：" + process + ", result: " + taskResult.getResult());
    }
}

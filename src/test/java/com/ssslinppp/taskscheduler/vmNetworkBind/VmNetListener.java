package com.ssslinppp.taskscheduler.vmNetworkBind;

import com.ssslinppp.taskscheduler.manager.ITaskStatusListener;
import com.ssslinppp.taskscheduler.model.NodeTaskResult;

import java.util.concurrent.TimeUnit;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/4/2 , Time: 11:18 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
public class VmNetListener implements ITaskStatusListener {

    @Override
    public void process(double process, String nodeTaskId, NodeTaskResult taskResult) {
        System.out.println("----任务监听器 process (sleep 15s 模拟长时间处理)：");
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("nodeTask: [" + nodeTaskId + "] success , 当前进度：" + process + ", result: " + taskResult.getResult());
    }

    @Override
    public void onFail(String nodeTaskId, Throwable t) {
        System.out.println("----任务监听器 nodeTask fail (sleep 5s 模拟长时间处理)：");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("nodeTaskId: " + nodeTaskId + ", error: " + t.getMessage());
        System.out.println("--------------");

    }
}

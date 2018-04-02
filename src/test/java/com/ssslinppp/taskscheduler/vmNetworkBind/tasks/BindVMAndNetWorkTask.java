package com.ssslinppp.taskscheduler.vmNetworkBind.tasks;

import com.ssslinppp.taskscheduler.model.NodeTask;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/4/2 , Time: 11:16 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
public class BindVMAndNetWorkTask extends NodeTask<String> {
    public BindVMAndNetWorkTask(String id, Set<String> dependences) {
        super(id, dependences);
    }

    @Override
    public String doNodeTaskWork() throws Exception {
        System.out.println("开启绑定 NetWork and VM...");
        TimeUnit.SECONDS.sleep(4);
        System.out.println("绑定 NetWork and VM finish");
        return "Bind Finish";
    }
}

package com.ssslinppp.taskscheduler.vmNetworkBind.tasks;

import com.ssslinppp.taskscheduler.model.NodeTask;

import java.util.concurrent.TimeUnit;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/4/2 , Time: 11:12 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
public class CreateVMTask extends NodeTask<String> {
    public CreateVMTask(String id) {
        super(id);
    }


    @Override
    public String doNodeTaskWork() throws Exception {
        System.out.println("开启创建 VM...");
        TimeUnit.SECONDS.sleep(8);
        System.out.println("VM 创建完成");
        return "VM Finish";
    }
}

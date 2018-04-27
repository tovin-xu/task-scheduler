package com.ssslinppp.taskscheduler.vmNetworkBind.tasks;

import com.ssslinppp.taskscheduler.model.NodeTask;

import java.util.concurrent.TimeUnit;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/4/2 , Time: 11:15 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
public class CreataNetWorkTask extends NodeTask<String> {
    public CreataNetWorkTask(String id) {
        super(id);
    }


    @Override
    public String doNodeTaskWork() throws Exception {
        System.out.println("开启创建NetWork...");
        TimeUnit.SECONDS.sleep(3);
        throw new RuntimeException("创建network失败");
//
//        System.out.println("NetWork创建完成");
//        return "NetWork Finish";
    }
}

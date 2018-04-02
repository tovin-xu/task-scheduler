package com.ssslinppp.taskscheduler;

import com.ssslinppp.taskscheduler.model.NodeTask;
import com.ssslinppp.taskscheduler.model.NodeTaskResult;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Desc:
 * <p>
 * User: liulin ,Date: 2018/3/27 , Time: 15:42 <br/>
 * Email: liulin@cmss.chinamobile.com <br/>
 * To change this template use File | Settings | File Templates.
 */
@Data
@Builder
public class AAANodeTask extends NodeTask {
    private String name;
    private long runTime;

    public AAANodeTask(String name, long runTime) {
        super();
        this.name = name;
        this.runTime = runTime;
    }

    public AAANodeTask(String name, long runTime, String parentId, String id, Set<String> dependences) {
        super(parentId, id, dependences);
        this.name = name;
        this.runTime = runTime;
    }


    @Override
    public NodeTaskResult call() throws Exception {
        System.out.println("Begin to run AAAnodeTask【" + name + "】 finish, time escape(ms): [" + runTime + "]");
        TimeUnit.MILLISECONDS.sleep(runTime);
        if (this.getId().endsWith("F")) {
            throw new RuntimeException("NodeTaskEEE exception");
        }
        return NodeTaskResult.builder().id(this.getId()).result("This is 【" + name + "】AAANodeTask").build();
    }
}

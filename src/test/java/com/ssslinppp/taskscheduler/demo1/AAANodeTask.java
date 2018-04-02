package com.ssslinppp.taskscheduler.demo1;

import com.ssslinppp.taskscheduler.model.NodeTask;
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
public class AAANodeTask extends NodeTask<String> {
    private long runTime;

    public AAANodeTask(long runTime, String id, Set<String> dependences) {
        super(id, dependences);
        this.runTime = runTime;
    }

    @Override
    public String doNodeTaskWork() throws Exception {
        System.out.println("Begin to run AAAnodeTask【" + this.getId() + "】 finish, time escape(ms): [" + runTime + "]");
        TimeUnit.MILLISECONDS.sleep(runTime);
        if (this.getId().endsWith("F")) {
            throw new RuntimeException("NodeTaskEEE exception");
        }
        return "[finish] AAANodeTask( " + this.getId() + " ) exec finish";
    }
}

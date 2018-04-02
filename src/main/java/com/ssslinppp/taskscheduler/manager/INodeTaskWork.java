package com.ssslinppp.taskscheduler.manager;

/**
 *
 * @param <T> the result type of method {@code doNodeTaskWork}
 */
public interface INodeTaskWork<T> {
    /**
     * execute a nodeTask with a result, or throws an exception if unable to do so.
     *
     * @return nodeTask exec result
     * @throws Exception if unable to compute a result
     */
    T doNodeTaskWork() throws Exception;
}

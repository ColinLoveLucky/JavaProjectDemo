package com.qf.cobra.lock;

/**
 * 获取锁后需要处理的逻辑
 * 
 * @author YanyanMao
 *  
 * @param <T>
 */
public interface AquiredLockWorker<T> {
    T invokeAfterLockAquire() throws Exception;
}
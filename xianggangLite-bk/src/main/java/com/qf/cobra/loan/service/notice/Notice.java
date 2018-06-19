package com.qf.cobra.loan.service.notice;

/**
 * 异常通知骨架类 <功能详细描述>
 * 
 * @version [版本号, V1.0]
 * @since 2015年3月25日 下午6:12:47
 */
public abstract class Notice implements Runnable {
	public abstract void notice();

	@Override
	public void run() {
		notice();
	}
}
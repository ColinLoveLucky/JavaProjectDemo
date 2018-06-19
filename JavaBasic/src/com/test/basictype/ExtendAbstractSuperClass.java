package com.test.basictype;

public  class ExtendAbstractSuperClass extends SuperClassAbstract {

	private volatile boolean active;

	@Override
	public void m() {
		// TODO Auto-generated method stub

	}

	public synchronized String getName() {
		return "HelloName";
	}

	public void run() {
		active = true;
		while (active) {
			// TODO
		}
	}

	public void stop() {
		active = false;
	}

}

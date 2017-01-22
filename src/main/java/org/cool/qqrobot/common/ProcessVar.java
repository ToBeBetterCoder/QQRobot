package org.cool.qqrobot.common;

public class ProcessVar {
	public static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<String>();
	public static void setProcessId(String processId) {
		THREAD_LOCAL.set(processId);
	}
	public static String getProcessId() {
		return THREAD_LOCAL.get();
	}
}

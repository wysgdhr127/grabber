package com.grabber.thread.task;

import com.grabber.model.ErrorUnit;
import com.grabber.util.ErrorUtil;
import com.grabber.util.LoggerUtil;

public class ErrorThread extends Thread {
	
	@Override
	public void run() {

		while (true) {
			ErrorUnit errorUnit = ErrorUtil.take();
			
			if (errorUnit.getMission() != null) {
				LoggerUtil.mailLog.info("Sucess send Mail [to = " + errorUnit.getMission().getMailTo() + "]");
			} else {
				LoggerUtil.mailLog.info("End to Send Mail");
				break;
			}
		}
	}
}

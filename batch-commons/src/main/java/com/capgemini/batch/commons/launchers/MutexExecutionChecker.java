package com.capgemini.batch.commons.launchers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MutexExecutionChecker {
	protected static Log logger = LogFactory.getLog(MutexExecutionChecker.class);
	
	private boolean stopExecutionRequested = false;
	private String batchInExecutionFilePath;
	private long batchInExecutionMaxTimeSeconds = -1;

	
	public void finalize() {
		logger.info("End");
	}

	public boolean isStopExecutionRequested() {
		return stopExecutionRequested;
	}

	public void setStopExecutionRequested(boolean stopExecutionRequested) {
		this.stopExecutionRequested = stopExecutionRequested;
	}

	public String getBatchInExecutionFilePath() {
		return batchInExecutionFilePath;
	}

	public void setBatchInExecutionFilePath(String batchInExecutionFilePath) {
		this.batchInExecutionFilePath = batchInExecutionFilePath;
	}

	public long getBatchInExecutionMaxTimeSeconds() {
		return batchInExecutionMaxTimeSeconds;
	}

	public void setBatchInExecutionMaxTimeSeconds(long batchInExecutionMaxTimeSeconds) {
		this.batchInExecutionMaxTimeSeconds = batchInExecutionMaxTimeSeconds;
	}
	
	public boolean isFileInExecutionPresent() {
		String inExecutionFilePath = batchInExecutionFilePath;
		if (inExecutionFilePath == null)
			return true;
		File inExecutionFile = new File(inExecutionFilePath);
		if (inExecutionFile.exists()) {
			long maxTimeSecondsExecutionPermitted = batchInExecutionMaxTimeSeconds;
			if (maxTimeSecondsExecutionPermitted > 0) {
				long lastModifiedSeconds = (inExecutionFile.lastModified() / 1000);
				long actualTimeSeconds = ((new Date()).getTime()) / 1000;
				if ((actualTimeSeconds - lastModifiedSeconds) > maxTimeSecondsExecutionPermitted) {
					logger.info("Superato tempo massimo di esecuzione");
					return false;
				} else
					return true;
			}
			return true;
		}
		return false;
	}

	protected void createInExecutionFile() {
		String inExecutionFilePath = batchInExecutionFilePath;
		if (inExecutionFilePath == null)
			return;

		try {
			FileOutputStream fos = new FileOutputStream(inExecutionFilePath);
			String tmp = "In esecuzione da " + new Date();
			fos.write(tmp.getBytes());
			fos.close();
			logger.info("In execution file <" + inExecutionFilePath + "> created");
		} catch (Exception e) {
			logger.error("Creating in execution file <" + inExecutionFilePath + "> - Exception occured <" + e + ">", e);
		}
	}

	protected void deleteInExecutionFile() {
		String inExecutionFilePath = batchInExecutionFilePath;
		if (inExecutionFilePath == null)
			return;

		boolean deleteResult = (new File(inExecutionFilePath)).delete();

		logger.info("In execution file <" + inExecutionFilePath + "> deleted - result = " + deleteResult);
	}


}

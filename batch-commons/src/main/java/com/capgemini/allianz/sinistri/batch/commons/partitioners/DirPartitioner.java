package com.capgemini.allianz.sinistri.batch.commons.partitioners;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;

public class DirPartitioner extends JobExecutionListenerSupport implements Partitioner {

	// Logger
	private static final Logger log = LoggerFactory.getLogger(DirPartitioner.class);

	@Resource(name = "fileApplicationProperties")
	private Properties fileApplicationProperties;

	@Value("1")
	private int gridSize;

	@Value("#{stepExecution}")
	private StepExecution stepExecution;

	/**
	 * Funzione di partizionamento
	 */
	@Override
	public Map<String, ExecutionContext> partition(int arg0) {

		log.info("Inizio partizionamento");
		log.debug(fileApplicationProperties.toString());

		int partitionNumber = 1;

		Map<String, ExecutionContext> result = new HashMap<>();

		int maxFilePerDir = Integer
				.decode(fileApplicationProperties.getProperty("dir.partitioner.maxFilePerDir") == null ? ("" + Integer.MAX_VALUE)
						: fileApplicationProperties.getProperty("dir.partitioner.maxFilePerDir"));

		String[] inputDirs = StringUtils.split(fileApplicationProperties.getProperty("dir.partitioner.inputDirs"), ",");
		String[] fileSuffixs = StringUtils.split(fileApplicationProperties.getProperty("dir.partitioner.fileSuffixs"),
				",");

		for (int id = 0; id < inputDirs.length; id++) {
			String inputDir = inputDirs[id];
			Collection<File> c = null;
			if (fileSuffixs == null || fileSuffixs.length == 0)
				c = Arrays.asList(new File(inputDir).listFiles());
			else
				c = FileFilterUtils.filterList(new SuffixFileFilter(fileSuffixs), new File(inputDir).listFiles());

			if (CollectionUtils.isNotEmpty(c)) {

				log.debug("trovati {} file nella directory {}. Verranno elaborati al piu' {} file.", c.size(), inputDir, "maxFilePerDir = " + maxFilePerDir);

				Iterator<File> splitIterator = c.iterator();

				int fileCounter = 0;
				while (splitIterator.hasNext() && fileCounter < maxFilePerDir) {
					ExecutionContext value = new ExecutionContext();
					File f = splitIterator.next();
					if (!f.isDirectory()) {
						value.putString("fileIn", "file:" + f.getAbsolutePath());
						result.put("partition_" + partitionNumber, value);
						partitionNumber++;
						fileCounter++;
					} else
						log.warn("skipped dir " + f.getAbsolutePath());
				}

			} else {
				log.warn("non sono stati trovati file [{}] nella directory {}", fileSuffixs.toString(), inputDir);
			}
		}

		log.info("Fine partizionamento: create [" + result.size() + "] partizioni");
		return result;
	}

}

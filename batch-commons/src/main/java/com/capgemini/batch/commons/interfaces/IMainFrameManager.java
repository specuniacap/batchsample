package com.capgemini.batch.commons.interfaces;

import com.capgemini.batch.commons.dto.InputDescriptor;
import com.capgemini.batch.commons.dto.OutputDescriptor;

public interface IMainFrameManager {
	public OutputDescriptor callServizioInfoFax1(InputDescriptor in);
	public OutputDescriptor callServizioTkdebq00(InputDescriptor in);
		
	public OutputDescriptor callCreaRecordSinifax(InputDescriptor in);
	public OutputDescriptor callCreaRecoprdInsertVarVinfoNew(InputDescriptor in);
	public OutputDescriptor creaRecordTask(InputDescriptor in);
}

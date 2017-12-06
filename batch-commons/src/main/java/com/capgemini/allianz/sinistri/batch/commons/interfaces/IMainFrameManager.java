package com.capgemini.allianz.sinistri.batch.commons.interfaces;

import com.capgemini.allianz.sinistri.batch.commons.dto.InputDescriptor;
import com.capgemini.allianz.sinistri.batch.commons.dto.OutputDescriptor;

public interface IMainFrameManager {
	public OutputDescriptor callServizioInfoFax1(InputDescriptor in);
	public OutputDescriptor callServizioTkdebq00(InputDescriptor in);
		
	public OutputDescriptor callCreaRecordSinifax(InputDescriptor in);
	public OutputDescriptor callCreaRecoprdInsertVarVinfoNew(InputDescriptor in);
	public OutputDescriptor createRecordTask(InputDescriptor in);
}

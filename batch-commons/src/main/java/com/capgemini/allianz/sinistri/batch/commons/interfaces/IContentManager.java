package com.capgemini.allianz.sinistri.batch.commons.interfaces;

import com.capgemini.allianz.sinistri.batch.commons.dto.InputDescriptor;
import com.capgemini.allianz.sinistri.batch.commons.dto.OutputDescriptor;

public interface IContentManager {
	public OutputDescriptor callCaricaDocumento(InputDescriptor in);
}

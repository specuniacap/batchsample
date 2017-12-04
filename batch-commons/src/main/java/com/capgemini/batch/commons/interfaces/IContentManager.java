package com.capgemini.batch.commons.interfaces;

import com.capgemini.batch.commons.dto.InputDescriptor;
import com.capgemini.batch.commons.dto.OutputDescriptor;

public interface IContentManager {
	public OutputDescriptor callCaricaDocumento(InputDescriptor in);
}

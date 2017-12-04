package com.capgemini.batch.commons.interfaces;

import com.capgemini.batch.commons.dto.InputDescriptor;
import com.capgemini.batch.commons.dto.OutputDescriptor;

public interface IPreviewManager {
	public OutputDescriptor createPreview(InputDescriptor in);
}

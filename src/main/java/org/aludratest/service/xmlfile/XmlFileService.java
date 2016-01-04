package org.aludratest.service.xmlfile;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;
import org.aludratest.service.file.FileService;

/**
 * Interface for a service for processing generic XML documents.
 * 
 * @author falbrech
 */
@ServiceInterface(name = "XML File Service", description = "Offers XML related access and verifaction methods.")
@ConfigProperties({ @ConfigProperty(name = "encoding", description = "The encoding to use for XML file creation. This MUST be equal to the encoding used for the file service configuration.", defaultValue = "UTF-8", required = true, type = String.class) })
public interface XmlFileService extends AludraService {

	/** Provides an object to parse and save XML documents from and to streams. */
	@Override
	XmlFileInteraction perform();

	/** Provides an object to verify XML documents. */
	@Override
	XmlFileVerification verify();

	/**
	 * Provides an object for performing queries on XML documents and analyze their differences.
	 */
	@Override
	XmlFileCondition check();

	/**
	 * Provides the internally used {@link FileService} instance
	 * 
	 * @return the internally used {@link FileService} instance
	 */
	FileService getFileService();

}

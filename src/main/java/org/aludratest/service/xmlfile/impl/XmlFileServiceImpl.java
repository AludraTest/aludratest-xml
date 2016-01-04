package org.aludratest.service.xmlfile.impl;

import org.aludratest.config.Preferences;
import org.aludratest.content.xml.XmlContent;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.Implementation;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.file.FileService;
import org.aludratest.service.xmlfile.XmlFileCondition;
import org.aludratest.service.xmlfile.XmlFileInteraction;
import org.aludratest.service.xmlfile.XmlFileService;
import org.aludratest.service.xmlfile.XmlFileVerification;

@Implementation({ XmlFileService.class })
public class XmlFileServiceImpl extends AbstractConfigurableAludraService implements XmlFileService {

	private FileService fileService;

	/** The action object that implements all XmlFileService action interfaces */
	private XmlFileAction action;

	/** A copy of the encoding property from configuration. Used for XML writes */
	private String encoding = "UTF-8";

	@Override
	public String getPropertiesBaseName() {
		return "xmlService";
	}

	@Override
	public void configure(Preferences preferences) {
		encoding = preferences.getStringValue("encoding", "UTF-8");
	}

	@Override
	public void initService() {
		this.fileService = aludraServiceContext.getNonLoggingService(ComponentId.create(FileService.class, getInstanceName()));
		XmlContent contentHandler = aludraServiceContext.newComponentInstance(XmlContent.class);
		this.action = new XmlFileAction(contentHandler, fileService, encoding);
	}

	@Override
	public String getDescription() {
		return XmlFileServiceImpl.class.getSimpleName();
	}

	@Override
	public void setSystemConnector(SystemConnector connector) {
		// not supported / required for XmlFileService
	}

	@Override
	public void close() {
		fileService.close();
	}

	@Override
	public XmlFileInteraction perform() {
		return action;
	}

	@Override
	public XmlFileVerification verify() {
		return action;
	}

	@Override
	public XmlFileCondition check() {
		return action;
	}

	@Override
	public FileService getFileService() {
		return fileService;
	}


}

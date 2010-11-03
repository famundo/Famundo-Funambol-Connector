package com.famundo;

/**
 * Enum the diffrent objects to sync
 * @author dudi
 *
 */
public class FumundoObjectType {
	static public final FumundoObjectType CONTACT
		= new FumundoObjectType("contact", "contact", "scard", "text/x-s4j-sifc");
	
	/**
	 * the famundo http path name of the object
	 */
	private String httpPath;
	
	/**
	 * the SIF XML root tag
	 */
	private String xmlRoot;
	
	/**
	 * sourceURI syncml sourc e.g scard
	 */
	private String sourceURI;
	
    /** The mime-type */
    private String mimeType;
	
	/**
	 * @param httpPath String famundo's https path name for the object 
	 * @param xmlRoot String the SIF XML root tag
	 */
	private FumundoObjectType(String httpPath, String xmlRoot, String sourceURI, String mimeType) {
		this.httpPath  = httpPath;
		this.xmlRoot   = xmlRoot;
		this.sourceURI = sourceURI;
		this.mimeType  = mimeType; 
	}

	/**
	 * Gets the enums famundo's https path name for the object 
	 * @return String
	 */
	public String getHttpPath() {
		return httpPath;
	}

	/**
	 * Gets the enums xmlRoot String the SIF XML root tag
	 * @return String
	 */
	public String getXmlRoot() {
		return xmlRoot;
	}

	public String getSourceURI() {
		return sourceURI;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	static FumundoObjectType getFromSourceURI(String sourceURI)	{
		FumundoObjectType[] type = { CONTACT };
		for (int i = 0; i < type.length; i++) {
			if(type[i].getSourceURI().equals(sourceURI)) {
				return type[i];
			}
		}
		return null;
	}
}

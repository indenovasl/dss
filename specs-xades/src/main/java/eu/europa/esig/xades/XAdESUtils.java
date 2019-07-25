package eu.europa.esig.xades;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import eu.europa.esig.dss.jaxb.parsers.XmlDefinerUtils;
import eu.europa.esig.xmldsig.XmlDSigUtils;
import eu.europa.esig.xmldsig.jaxb.ObjectFactory;

public final class XAdESUtils {

	public static final String XADES_SCHEMA_LOCATION = "/xsd/XAdES.xsd";
	public static final String XADES_141_SCHEMA_LOCATION = "/xsd/XAdESv141.xsd";

	private XAdESUtils() {
	}

	private static JAXBContext jc;
	private static Schema schema;

	public static JAXBContext getJAXBContext() throws JAXBException {
		if (jc == null) {
			jc = JAXBContext.newInstance(ObjectFactory.class, eu.europa.esig.xades.jaxb.xades132.ObjectFactory.class,
					eu.europa.esig.xades.jaxb.xades141.ObjectFactory.class);
		}
		return jc;
	}

	public static Schema getSchema() throws SAXException {
		if (schema == null) {
			SchemaFactory sf = XmlDefinerUtils.getSecureSchemaFactory();
			List<Source> xsdSources = getXSDSources();
			schema = sf.newSchema(xsdSources.toArray(new Source[xsdSources.size()]));
		}
		return schema;
	}

	public static List<Source> getXSDSources() {
		List<Source> xsdSources = XmlDSigUtils.getXSDSources();
		xsdSources.add(new StreamSource(XAdESUtils.class.getResourceAsStream(XADES_SCHEMA_LOCATION)));
		xsdSources.add(new StreamSource(XAdESUtils.class.getResourceAsStream(XADES_141_SCHEMA_LOCATION)));
		return xsdSources;
	}

}
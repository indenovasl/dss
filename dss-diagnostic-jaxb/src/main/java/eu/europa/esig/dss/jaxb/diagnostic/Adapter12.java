//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.12 at 07:50:40 AM CEST 
//


package eu.europa.esig.dss.jaxb.diagnostic;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import eu.europa.esig.dss.validation.XmlRevocationRefOrigin;

public class Adapter12
    extends XmlAdapter<String, XmlRevocationRefOrigin>
{


    public XmlRevocationRefOrigin unmarshal(String value) {
        return (eu.europa.esig.dss.jaxb.parsers.RevocationRefOriginParser.parse(value));
    }

    public String marshal(XmlRevocationRefOrigin value) {
        return (eu.europa.esig.dss.jaxb.parsers.RevocationRefOriginParser.print(value));
    }

}

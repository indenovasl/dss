package eu.europa.esig.dss.enumerations;

/**
 * ETSI EN 319 412-1 V1.1.1
 * 
 * -- Semantics identifiers
 * 
 * id-etsi-qcs-semantics-identifiers OBJECT IDENTIFIER ::= { itu-t(0)
 * identified-organization(4) etsi(0) id-cert-profile(194121) 1 }
 * 
 * -- Semantics identifier for natural person identifier
 * 
 * id-etsi-qcs-semanticsId-Natural OBJECT IDENTIFIER ::= {
 * id-etsi-qcs-semantics-identifiers 1 }
 * 
 * -- Semantics identifier for legal person identifier
 * 
 * id-etsi-qcs-SemanticsId-Legal OBJECT IDENTIFIER ::= {
 * id-etsi-qcs-semantics-identifiers 2 }
 * 
 */
public enum SemanticsIdentifier implements OidDescription {

	qcsSemanticsIdNatural("Semantics identifier for natural person", "0.4.0.194121.1.1"),

	qcsSemanticsIdLegal("Semantics identifier for legal person", "0.4.0.194121.1.2");

	private final String description;
	private final String oid;

	SemanticsIdentifier(String description, String oid) {
		this.description = description;
		this.oid = oid;
	}

	@Override
	public String getOid() {
		return oid;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public static SemanticsIdentifier fromOid(String oid) {
		for (SemanticsIdentifier semanticsIdentifier : SemanticsIdentifier.values()) {
			if (semanticsIdentifier.oid.equals(oid)) {
				return semanticsIdentifier;
			}
		}
		return null;
	}
}
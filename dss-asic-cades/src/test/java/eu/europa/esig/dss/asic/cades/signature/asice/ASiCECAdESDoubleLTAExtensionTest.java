/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.asic.cades.signature.asice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import eu.europa.esig.dss.asic.cades.ASiCWithCAdESContainerExtractor;
import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.asic.cades.validation.ASiCEWithCAdESManifestParser;
import eu.europa.esig.dss.asic.common.ASiCExtractResult;
import eu.europa.esig.dss.asic.common.AbstractASiCContainerExtractor;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.test.signature.PKIFactoryAccess;
import eu.europa.esig.dss.validation.ManifestFile;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;

public class ASiCECAdESDoubleLTAExtensionTest extends PKIFactoryAccess {
	
	@Test
	public void test() throws IOException {
		
		List<DSSDocument> documentToSigns = new ArrayList<>();
		documentToSigns.add(new InMemoryDocument("Hello World !".getBytes(), "test.text", MimeType.TEXT));
		documentToSigns.add(new InMemoryDocument("Bye World !".getBytes(), "test2.text", MimeType.TEXT));

		ASiCWithCAdESSignatureParameters signatureParameters = new ASiCWithCAdESSignatureParameters();
		signatureParameters.bLevel().setSigningDate(new Date());
		signatureParameters.setSigningCertificate(getSigningCert());
		signatureParameters.setCertificateChain(getCertificateChain());
		signatureParameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_LT);
		signatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);

		ASiCWithCAdESService service = new ASiCWithCAdESService(getCompleteCertificateVerifier());
		service.setTspSource(getGoodTsa());

		ToBeSigned dataToSign = service.getDataToSign(documentToSigns, signatureParameters);
		SignatureValue signatureValue = getToken().sign(dataToSign, signatureParameters.getDigestAlgorithm(), getPrivateKeyEntry());
		DSSDocument signedDocument = service.signDocument(documentToSigns, signatureParameters, signatureValue);

		// signedDocument.save("target/signed.asice");

		service.setTspSource(getGoodTsaCrossCertification());

		ASiCWithCAdESSignatureParameters extendParameters = new ASiCWithCAdESSignatureParameters();
		extendParameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_LTA);
		extendParameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);
		DSSDocument extendedDocument = service.extendDocument(signedDocument, extendParameters);
		
		DSSDocument doubleLTADoc = service.extendDocument(extendedDocument, extendParameters);
		
		// doubleLTADoc.save("target/doubleLTA.asice");
		
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doubleLTADoc);
		validator.setCertificateVerifier(getOfflineCertificateVerifier());
		Reports reports = validator.validateDocument();
		
		// reports.print();
		
		SimpleReport simpleReport = reports.getSimpleReport();
		assertEquals(Indication.TOTAL_PASSED, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
		
		DetailedReport detailedReport = reports.getDetailedReport();
		List<String> timestampIds = detailedReport.getTimestampIds();
		assertEquals(3, timestampIds.size());
		for (String id : timestampIds) {
			assertEquals(Indication.PASSED, detailedReport.getTimestampValidationIndication(id));
		}
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();

		SignatureWrapper signatureWrapper = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		List<TimestampWrapper> timestampList = signatureWrapper.getTimestampList();
		assertEquals(3, timestampList.size());
		
		assertEquals(0, timestampList.get(0).getTimestampedRevocationIds().size());
		assertEquals(2, timestampList.get(1).getTimestampedRevocationIds().size());
		assertEquals(3, timestampList.get(2).getTimestampedRevocationIds().size());
		
		AbstractASiCContainerExtractor extractor = new ASiCWithCAdESContainerExtractor(doubleLTADoc);
        ASiCExtractResult result = extractor.extract();
        
        List<DSSDocument> manifestFiles = result.getManifestDocuments();
        assertEquals(1, manifestFiles.size());
        
    	ManifestFile manifestFile = ASiCEWithCAdESManifestParser.getManifestFile(manifestFiles.get(0));
		assertFalse(manifestFile.isTimestampManifest());
		assertFalse(manifestFile.isArchiveManifest());
        
        List<DSSDocument> archiveManifestFiles = result.getArchiveManifestDocuments();
        assertEquals(2, archiveManifestFiles.size());
        
        for (DSSDocument archiveManifest : archiveManifestFiles) {
        	manifestFile = ASiCEWithCAdESManifestParser.getManifestFile(archiveManifest);
			assertTrue(manifestFile.isTimestampManifest());
			assertTrue(manifestFile.isArchiveManifest());
        }
        
        List<DSSDocument> allManifestFiles = result.getAllManifestDocuments();
        assertEquals(3, allManifestFiles.size());
		
	}

	@Override
	protected String getSigningAlias() {
		return RSA_SHA3_USER;
	}

}

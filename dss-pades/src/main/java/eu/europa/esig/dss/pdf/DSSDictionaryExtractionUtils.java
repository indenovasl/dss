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
package eu.europa.esig.dss.pdf;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;

public class DSSDictionaryExtractionUtils {

	private static final Logger LOG = LoggerFactory.getLogger(DSSDictionaryExtractionUtils.class);

	public static Map<Long, byte[]> getCRLsFromArray(PdfDict dict, String dictionaryName, String arrayName) {
		Map<Long, byte[]> crlMap = new HashMap<>();
		final PdfArray crlArray = dict.getAsArray(arrayName);
		if (crlArray != null) {
			LOG.debug("There are {} CRLs in the '{}' dictionary", crlArray.size(), dictionaryName);
			for (int ii = 0; ii < crlArray.size(); ii++) {
				try {
					long objectNumber = crlArray.getObjectNumber(ii);
					if (!crlMap.containsKey(objectNumber)) {
						crlMap.put(objectNumber, crlArray.getBytes(ii));
					}
				} catch (Exception e) {
					LOG.debug("Unable to read CRL '{}' from the '{}' dictionary : {}", ii, dictionaryName, e.getMessage(), e);
				}
			}
		} else {
			LOG.debug("No CRLs found in the '{}' dictionary", dictionaryName);
		}
		return crlMap;
	}

	public static Map<Long, CertificateToken> getCertsFromArray(PdfDict dict, String dictionaryName, String arrayName) {
		Map<Long, CertificateToken> certMap = new HashMap<>();
		final PdfArray certsArray = dict.getAsArray(arrayName);
		if (certsArray != null) {
			LOG.debug("There are {} certificates in the '{}' dictionary", certsArray.size(), dictionaryName);
			for (int ii = 0; ii < certsArray.size(); ii++) {
				try {
					final long objectNumber = certsArray.getObjectNumber(ii);
					if (!certMap.containsKey(objectNumber)) {
						certMap.put(objectNumber, DSSUtils.loadCertificate(certsArray.getBytes(ii)));
					}
				} catch (Exception e) {
					LOG.debug("Unable to read Cert '{}' from the '{}' dictionary : {}", ii, dictionaryName, e.getMessage(), e);
				}
			}
		} else {
			LOG.debug("No Certs found in the '{}' dictionary", dictionaryName);
		}
		return certMap;
	}

	public static Map<Long, BasicOCSPResp> getOCSPsFromArray(PdfDict dict, String dictionaryName, String arrayName) {
		Map<Long, BasicOCSPResp> ocspMap = new HashMap<>();
		PdfArray ocspArray = dict.getAsArray(arrayName);
		if (ocspArray != null) {
			LOG.debug("There are {} OCSPs in the '{}' dictionary", ocspArray.size(), dictionaryName);
			for (int ii = 0; ii < ocspArray.size(); ii++) {
				try {
					final long objectNumber = ocspArray.getObjectNumber(ii);
					if (!ocspMap.containsKey(objectNumber)) {
						final OCSPResp ocspResp = new OCSPResp(ocspArray.getBytes(ii));
						final BasicOCSPResp responseObject = (BasicOCSPResp) ocspResp.getResponseObject();
						ocspMap.put(objectNumber, responseObject);
					}
				} catch (Exception e) {
					LOG.debug("Unable to read OCSP '{}' from the '{}' dictionary : {}", ii, dictionaryName, e.getMessage(), e);
				}
			}
		} else {
			LOG.debug("No OCSPs found in the '{}' dictionary", dictionaryName);
		}
		return ocspMap;
	}

}

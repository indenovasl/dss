package eu.europa.esig.dss.token;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore.PasswordProtection;
import java.security.Security;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import javax.crypto.Cipher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.spi.DSSSecurityProvider;
import eu.europa.esig.dss.spi.DSSUtils;

public class SignDigestRSATest {

	static {
		Security.addProvider(DSSSecurityProvider.getSecurityProvider());
	}

	private static final Logger LOG = LoggerFactory.getLogger(SignDigestRSATest.class);

	private static Collection<DigestAlgorithm> data() {
		Collection<DigestAlgorithm> rsaCombinations = new ArrayList<DigestAlgorithm>();
		for (DigestAlgorithm digestAlgorithm : DigestAlgorithm.values()) {
			if (SignatureAlgorithm.getAlgorithm(EncryptionAlgorithm.RSA, digestAlgorithm) != null) {
				rsaCombinations.add(digestAlgorithm);
			}
		}
		return rsaCombinations;
	}

	@ParameterizedTest(name = "DigestAlgorithm {index} : {0}")
	@MethodSource("data")
	public void testPkcs12(DigestAlgorithm digestAlgo) throws IOException {
		try (Pkcs12SignatureToken signatureToken = new Pkcs12SignatureToken("src/test/resources/user_a_rsa.p12",
				new PasswordProtection("password".toCharArray()))) {

			List<DSSPrivateKeyEntry> keys = signatureToken.getKeys();
			KSPrivateKeyEntry entry = (KSPrivateKeyEntry) keys.get(0);

			ToBeSigned toBeSigned = new ToBeSigned("Hello world".getBytes("UTF-8"));

			SignatureValue signValue = signatureToken.sign(toBeSigned, digestAlgo, entry);
			assertNotNull(signValue.getAlgorithm());
			LOG.info("Sig value : {}", Base64.getEncoder().encodeToString(signValue.getValue()));
			try {
				Signature sig = Signature.getInstance(signValue.getAlgorithm().getJCEId());
				sig.initVerify(entry.getCertificate().getPublicKey());
				sig.update(toBeSigned.getBytes());
				assertTrue(sig.verify(signValue.getValue()));
			} catch (GeneralSecurityException e) {
				Assertions.fail(e.getMessage());
			}

			try {
				Cipher cipher = Cipher.getInstance(entry.getEncryptionAlgorithm().getName());
				cipher.init(Cipher.DECRYPT_MODE, entry.getCertificate().getCertificate());
				byte[] decrypted = cipher.doFinal(signValue.getValue());
				LOG.info("Decrypted : {}", Base64.getEncoder().encodeToString(decrypted));
			} catch (GeneralSecurityException e) {
				Assertions.fail(e.getMessage());
			}

			// Important step with RSA without PSS
			final byte[] digestBinaries = DSSUtils.digest(digestAlgo, toBeSigned.getBytes());
			final byte[] encodedDigest = DSSUtils.encodeRSADigest(digestAlgo, digestBinaries);
			Digest digest = new Digest(digestAlgo, encodedDigest);

			SignatureValue signDigestValue = signatureToken.signDigest(digest, entry);
			assertNotNull(signDigestValue.getAlgorithm());
			LOG.info("Sig value : {}", Base64.getEncoder().encodeToString(signDigestValue.getValue()));

			try {
				Signature sig = Signature.getInstance(signValue.getAlgorithm().getJCEId());
				sig.initVerify(entry.getCertificate().getPublicKey());
				sig.update(toBeSigned.getBytes());
				assertTrue(sig.verify(signDigestValue.getValue()));
			} catch (GeneralSecurityException e) {
				Assertions.fail(e.getMessage());
			}

			try {
				Cipher cipher = Cipher.getInstance(entry.getEncryptionAlgorithm().getName());
				cipher.init(Cipher.DECRYPT_MODE, entry.getCertificate().getCertificate());
				byte[] decrypted = cipher.doFinal(signDigestValue.getValue());
				LOG.info("Decrypted : {}", Base64.getEncoder().encodeToString(decrypted));
			} catch (GeneralSecurityException e) {
				Assertions.fail(e.getMessage());
			}

			assertArrayEquals(signValue.getValue(), signDigestValue.getValue());
		}
	}

}
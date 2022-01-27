package eu.europa.esig.dss.cookbook.example;

import eu.europa.esig.dss.detailedreport.DetailedReportFacade;
import eu.europa.esig.dss.diagnostic.DiagnosticDataFacade;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import eu.europa.esig.dss.simplereport.SimpleReportFacade;
import eu.europa.esig.validationreport.ValidationReportFacade;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnsureXmlSamplesSynchronizedTest {

    @Test
    void constraint() throws Exception {
        assertNotNull(ValidationPolicyFacade.newFacade().getValidationPolicy(new File("src/main/asciidoc/_samples/constraint.xml")));
    }

    @Test
    void simpleReport() throws Exception {
        assertNotNull(SimpleReportFacade.newFacade().unmarshall(new File("src/main/asciidoc/_samples/simple-report-example.xml")));
    }

    @Test
    void detailedReport() throws Exception {
        assertNotNull(DetailedReportFacade.newFacade().unmarshall(new File("src/main/asciidoc/_samples/detailed-report-example.xml")));
    }

    @Test
    void detailedReportTimestamp() throws Exception {
        assertNotNull(DetailedReportFacade.newFacade().unmarshall(new File("src/main/asciidoc/_samples/timestamp-detailed-report-example.xml")));
    }

    @Test
    void diagnosticData() throws Exception {
        assertNotNull(DiagnosticDataFacade.newFacade().unmarshall(new File("src/main/asciidoc/_samples/diagnostic-data-example.xml")));
    }

    @Test
    void etsiVR() throws Exception {
        assertNotNull(ValidationReportFacade.newFacade().unmarshall(new File("src/main/asciidoc/_samples/etsi-validation-report-example.xml")));
    }

}

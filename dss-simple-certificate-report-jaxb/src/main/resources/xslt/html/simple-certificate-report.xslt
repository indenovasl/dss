<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dss="http://dss.esig.europa.eu/validation/simple-certificate-report">
                
	<xsl:output method="html" encoding="utf-8" indent="yes" omit-xml-declaration="yes" />
	
	<xsl:param name="rootTrustmarkUrlInTlBrowser">
		https://webgate.ec.europa.eu/tl-browser/#/trustmark/
	</xsl:param>
	<xsl:param name="rootCountryUrlInTlBrowser">
		https://webgate.ec.europa.eu/tl-browser/#/tl/
	</xsl:param>
	
   	<xsl:variable name="validationTime">
   		<xsl:value-of select="/dss:SimpleCertificateReport/@ValidationTime" />
   	</xsl:variable>

    <xsl:template match="/dss:SimpleCertificateReport">
		<xsl:comment>Generated by DSS v.${project.version}</xsl:comment>
		<xsl:comment>ValidationTime : <xsl:value-of select="$validationTime" /></xsl:comment>
	    
		<xsl:apply-templates select="dss:Chain"/>
    </xsl:template>
    
	<xsl:template match="dss:Chain">
    	<xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="dss:ChainItem">
    
    	 <xsl:variable name="indicationCssClass">
        	<xsl:choose>
				<xsl:when test="dss:revocation or (dss:notAfter &gt; $validationTime)">danger</xsl:when>
				<xsl:otherwise>success</xsl:otherwise>
			</xsl:choose>
        </xsl:variable>
    
		<div>
    		<xsl:attribute name="class">panel panel-<xsl:value-of select="$indicationCssClass" /></xsl:attribute>
    		<div>
    			<xsl:attribute name="class">panel-heading</xsl:attribute>
	    		<xsl:attribute name="data-target">#collapseCert-<xsl:value-of select="dss:id"/></xsl:attribute>
		       	<xsl:attribute name="data-toggle">collapse</xsl:attribute>
    			Certificate
	        </div>
    		<div>
    			<xsl:attribute name="class">panel-body collapse in</xsl:attribute>
	        	<xsl:attribute name="id">collapseCert-<xsl:value-of select="dss:id"/></xsl:attribute>
	        	
				<xsl:apply-templates select="dss:subject"/>
					
	        	<dl>
	        		<xsl:attribute name="class">dl-horizontal</xsl:attribute>
	        		
	        		<dt>Validity</dt>
	        		<dd><xsl:value-of select="dss:notBefore"/> - <xsl:value-of select="dss:notAfter"/></dd>
	        		
       				<dt>Revocation</dt>
	        		<xsl:choose>
	        			<xsl:when test="dss:revocation">
	        				<dd>
		        				<span>
									<xsl:attribute name="class">glyphicon glyphicon glyphicon-remove-sign text-danger</xsl:attribute>
									<xsl:attribute name="title">Revoked</xsl:attribute>
								</span>
	        					Revoked (reason:<xsl:value-of select="dss:revocation/dss:revocationReason" /> @ <xsl:value-of select="dss:revocation/dss:revocationDate" />)
	        				</dd>
	        			</xsl:when>
	        			<xsl:otherwise>
	        				<dd>
		        				<span>
									<xsl:attribute name="class">glyphicon glyphicon-ok-sign text-success</xsl:attribute>
									<xsl:attribute name="title">Not Revoked</xsl:attribute>
								</span>
	        				</dd>
	        			</xsl:otherwise>
	        		</xsl:choose>
	        		
	        		<xsl:if test="dss:ocspUrls or dss:crlUrls">
	        			<dt>Revocation Access</dt>
						<xsl:apply-templates select="dss:ocspUrls"/>
						<xsl:apply-templates select="dss:crlUrls"/>
	        		</xsl:if>
				    
					<xsl:apply-templates select="dss:aiaUrls"/>
					<xsl:apply-templates select="dss:cpsUrls"/>
	        		
	        		<xsl:if test="dss:qualificationAtIssuance">
		        		<dt>Qualification at issuance time</dt>
		        		<dd><xsl:value-of select="dss:qualificationAtIssuance"/></dd>
	        		</xsl:if>
	        		<xsl:if test="dss:qualificationAtValidation">
		        		<dt>Qualification at validation time</dt>
		        		<dd><xsl:value-of select="dss:qualificationAtValidation"/></dd>
	        		</xsl:if>
	        		
					<xsl:apply-templates select="dss:trustAnchors"/>
	        		
	        	</dl>
    		</div>
    	</div>
    </xsl:template>
    
     <xsl:template match="dss:subject">
     	<dl>
		    <xsl:attribute name="class">dl-horizontal</xsl:attribute>
	  		<xsl:if test="dss:commonName">
		   		<dt>Common name</dt>
		   		<dd><xsl:value-of select="dss:commonName"/></dd>
	  		</xsl:if>
	  		<xsl:if test="dss:surname">
		   		<dt>Surname</dt>
		   		<dd><xsl:value-of select="dss:surname"/></dd>
	  		</xsl:if>
	  		<xsl:if test="dss:givenName">
		   		<dt>Given name</dt>
		   		<dd><xsl:value-of select="dss:givenName"/></dd>
	  		</xsl:if>
	  		<xsl:if test="dss:pseudonym">
		   		<dt>Pseudonym</dt>
		   		<dd><xsl:value-of select="dss:pseudonym"/></dd>
	  		</xsl:if>
	  		<xsl:if test="dss:organizationName">
		   		<dt>Organization name</dt>
		   		<dd><xsl:value-of select="dss:organizationName"/></dd>
	  		</xsl:if>
	  		<xsl:if test="dss:organizationUnit">
		   		<dt>Organization Unit</dt>
		   		<dd><xsl:value-of select="dss:organizationUnit"/></dd>
	  		</xsl:if>
	  		<xsl:if test="dss:country">
		   		<dt>Country</dt>
		   		<dd><xsl:value-of select="dss:country"/></dd>
	  		</xsl:if>
	  	</dl>
	</xsl:template>
    
    <xsl:template match="dss:ocspUrls">
  		<dd>
  			<acronym>
  				<xsl:attribute name="title">Online Certificate Status Protocol</xsl:attribute>
  				OCSP
  			</acronym>
	  		<ul>
				<xsl:apply-templates select="dss:url"/>
			</ul>
		</dd>
	</xsl:template>
	
  	<xsl:template match="dss:crlUrls">
  		<dd>
  			<acronym>
  				<xsl:attribute name="title">Certificate Revocation List</xsl:attribute>
		  		CRL
		  	</acronym>
			<ul>
				<xsl:apply-templates select="dss:url"/>
			</ul>
		</dd>
	</xsl:template>
	
	<xsl:template match="dss:aiaUrls">
  		<dt>
  			<acronym>
  				<xsl:attribute name="title">Authority Information Access</xsl:attribute>
  		  		AIA
  		  	</acronym>
		</dt>
		<dd>
	  		<ul>
				<xsl:apply-templates select="dss:url"/>
			</ul>
		</dd>
	</xsl:template>
	
    <xsl:template match="dss:cpsUrls">
  		<dt>
  			<acronym>
  				<xsl:attribute name="title">Certification Practice Statements</xsl:attribute>
  		  		CPS
  		  	</acronym>
		</dt>
		<dd>
	  		<ul>
				<xsl:apply-templates select="dss:url"/>
			</ul>
		</dd>
	</xsl:template>
	
    <xsl:template match="dss:trustAnchors">
  		<dt>
  			Trust Anchor
		</dt>
		<dd>
	  		<ul>
				<xsl:apply-templates select="dss:trustAnchor"/>
			</ul>
		</dd>
	</xsl:template>
    
    <xsl:template match="dss:trustAnchor">
    	<li>
    		<a>
    			<xsl:attribute name="href">
	    			<xsl:value-of select="concat($rootCountryUrlInTlBrowser, dss:countryCode)" />
	    		</xsl:attribute>
	    		<xsl:attribute name="target">_blank</xsl:attribute>
	    		<xsl:attribute name="title"><xsl:value-of select="dss:countryCode" /></xsl:attribute>
	    		<span>
	    			<xsl:attribute name="class">
		    			small_flag <xsl:value-of select="concat('flag_', dss:countryCode)" />
		    		</xsl:attribute>
	    		</span>
    		</a>
    		
    		-
    		
    		<a>
	    		<xsl:attribute name="href">
	    			<xsl:value-of select="concat($rootTrustmarkUrlInTlBrowser, dss:countryCode, '/', dss:trustServiceProviderRegistrationId)" />
	    		</xsl:attribute>
	    		<xsl:attribute name="target">_blank</xsl:attribute>
	    		<xsl:attribute name="title">View in TL Browser</xsl:attribute>
	    		<xsl:value-of select="dss:trustServiceProvider" /> 
    		</a>
    		-> <xsl:value-of select="dss:trustServiceName" />
    	</li>
    </xsl:template>
    
    <xsl:template match="dss:url">
    	<li>
    		<a>
    			<xsl:attribute name="href"><xsl:value-of select="." /></xsl:attribute>
	    		<xsl:attribute name="target">_blank</xsl:attribute>
    			<xsl:value-of select="." />
    		</a>
    	</li>
    </xsl:template>

</xsl:stylesheet>

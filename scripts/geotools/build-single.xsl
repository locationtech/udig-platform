<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:template match="classpath">
			<!-- for each library dependency, copy to local lib directory -->
				<xsl:variable name="path">${user.home}/.maven/repository</xsl:variable>
				<xsl:element name="copy">
					<xsl:attribute name="todir">lib</xsl:attribute>
					<xsl:attribute name="flatten">true</xsl:attribute>
					<xsl:element name="fileset">
						<xsl:attribute name="dir"><xsl:value-of select="$path"/></xsl:attribute>
						<xsl:for-each select="classpathentry">
							<xsl:variable name="kind"><xsl:value-of select="@kind"/></xsl:variable>
							<xsl:variable name="path"><xsl:value-of select="@path"/></xsl:variable>
							<xsl:if test="$kind = 'var'">
								<xsl:choose>
									<xsl:when test="contains($path,'gt2')"/>
									<xsl:otherwise>
										<xsl:element name="include">
											<xsl:attribute name="name">**/<xsl:value-of select="substring-after($path,'jars/')"/></xsl:attribute>
										</xsl:element>
									</xsl:otherwise>
								</xsl:choose>

								<xsl:text>
								</xsl:text>

							</xsl:if>
						</xsl:for-each>

					</xsl:element>
				</xsl:element>

		<xsl:text>
		</xsl:text>
	</xsl:template>

</xsl:stylesheet>

<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" omit-xml-declaration="yes"/>

	<xsl:template match="classpath">
				<!-- process all the jar dependencies and reference them as local -->
				<xsl:for-each select="classpathentry">
					<xsl:variable name="t"><xsl:value-of select="@kind"/></xsl:variable>
					<xsl:variable name="p"><xsl:value-of select="@path"/></xsl:variable>

					<xsl:if test="$t = 'var'">
						<xsl:choose>
							<xsl:when test="contains($p,'gt2')">
							</xsl:when>
							<xsl:otherwise>
								<xsl:variable name="jar"><xsl:value-of select="substring-after($p,'jars/')"/></xsl:variable>
								<xsl:element name="library">
									<xsl:attribute name="name">lib/<xsl:value-of select="$jar"/></xsl:attribute>
									<xsl:text>
									</xsl:text>
									<xsl:element name="export">
										<xsl:attribute name="name">*</xsl:attribute>
									</xsl:element>
									<xsl:text>
									</xsl:text>
								</xsl:element>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>

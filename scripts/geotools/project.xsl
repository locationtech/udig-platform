<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> 
	<xsl:output method="xml"/>
	
	<!-- parameters set by the caller of the script -->
	<xsl:param name="pid"/>

	<xsl:template match="/">
		<xsl:apply-templates select="*"/>
	</xsl:template>

	<xsl:template match="*">
		<xsl:element name="{name()}">
			<xsl:if test="count(*) = 0">
					<xsl:choose>
						<xsl:when test="name() = 'name' and name(..) = 'projectDescription'">
							<xsl:value-of select="$pid"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="text()"/>
						</xsl:otherwise>
					</xsl:choose>
			</xsl:if>
			<xsl:apply-templates select="*"/>
			<xsl:if test="name() = 'buildSpec'">
				<xsl:element name="buildCommand">
					<xsl:element name="name">org.eclipse.pde.ManifestBuilder</xsl:element>
					<xsl:element name="argments"/>
				</xsl:element>
				<xsl:element name="buildCommand">
					<xsl:element name="name">org.eclipse.pde.SchemaBuilder</xsl:element>
					<xsl:element name="argments"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="name() = 'natures'">
				<xsl:element name="nature">org.eclipse.pde.PluginNature</xsl:element>
			</xsl:if>
		</xsl:element>
		
	</xsl:template>

</xsl:stylesheet>

<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="manifest">
		<xsl:element name="project">
			<xsl:attribute name="name">doc</xsl:attribute>
			<xsl:attribute name="default">doc</xsl:attribute>

			<xsl:element name="property">
				<xsl:attribute name="file">build.properties</xsl:attribute>
			</xsl:element>

			<xsl:element name="target">
				<xsl:attribute name="name">doc</xsl:attribute>
				<xsl:element name="mkdir">
					<xsl:attribute name="dir">build/doc</xsl:attribute>
				</xsl:element>
				<xsl:element name="javadoc">
					<xsl:attribute name="destdir">build/doc</xsl:attribute>
					<xsl:for-each select="plugins/plugin">
						<xsl:element name="fileset">
							<xsl:attribute name="dir">build/plugins/<xsl:value-of select="."/></xsl:attribute>
							<xsl:attribute name="includes">**/*.java</xsl:attribute>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>

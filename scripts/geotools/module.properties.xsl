<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="text"/>

	<xsl:template match="/">
		<xsl:variable name="mid"><xsl:value-of select="project/id"/></xsl:variable>
		<xsl:variable name="ver"><xsl:value-of select="project/currentVersion"/></xsl:variable>
		<xsl:text>module.id=org.geotools.</xsl:text><xsl:value-of select="$mid"/>
<xsl:text>
</xsl:text>
		<xsl:text>module.name=</xsl:text><xsl:value-of select="$mid"/>
<xsl:text>
</xsl:text>
		<xsl:text>module.version=</xsl:text><xsl:value-of select="$ver"/>

	</xsl:template>

</xsl:stylesheet>

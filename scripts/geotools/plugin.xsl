<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> 
	<xsl:output method="xml"/>
	
	<!-- parameters set by the caller of the script -->
	<xsl:param name="pid"/>
	<xsl:param name="name"/>
	<xsl:param name="ver"/>

	<xsl:template match="classpath">
		<xsl:processing-instruction name="eclipse">version=3.0</xsl:processing-instruction>	

		<xsl:text>
		</xsl:text>
		<xsl:element name="plugin">
			<xsl:attribute name="id"><xsl:value-of select="translate($pid,'-','_')"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
			<xsl:attribute name="version"><xsl:value-of select="translate($ver,'x','0')"/></xsl:attribute>
			<xsl:text>
			</xsl:text>
			<xsl:element name="runtime">
				<xsl:text>
				</xsl:text>
				<xsl:element name="library">
					<xsl:attribute name="name"><xsl:value-of select="$name"/>.jar</xsl:attribute>	
					<xsl:text>
					</xsl:text>
					<xsl:element name="export">
						<xsl:attribute name="name">*</xsl:attribute>
					</xsl:element>
					<xsl:text>
					</xsl:text>
				</xsl:element>
				<xsl:text>
				</xsl:text>

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
			</xsl:element> <!-- runtime -->
			<xsl:text>
			</xsl:text>
			<xsl:text>
			</xsl:text>

			<!-- figure out all the other gt plugins this plugin needs -->
			<xsl:element name="requires">	
				<xsl:for-each select="classpathentry">
					<xsl:variable name="t"><xsl:value-of select="@kind"/></xsl:variable>
					<xsl:variable name="p"><xsl:value-of select="@path"/></xsl:variable>

					<xsl:if test="$t = 'var'">
						<xsl:if test="contains($p,'gt2')">
							<xsl:variable name="id"><xsl:value-of select="substring-after($p,'jars/')"/></xsl:variable>
							<xsl:element name="import">
								<xsl:choose>
									<xsl:when test="contains(substring-after($id,'-'),'-') != 'true'">
										<xsl:attribute name="plugin">org.geotools.<xsl:value-of select="substring-before($id,'-')"/></xsl:attribute>
									</xsl:when>
									<xsl:when test="contains(substring-after(substring-after($id,'-'),'-'),'-') != 'true'">
										<xsl:attribute name="plugin">org.geotools.<xsl:value-of select="concat(substring-before($id,'-'),'_',substring-before(substring-after($id,'-'),'-'))"/></xsl:attribute>
									</xsl:when>
								</xsl:choose>

							</xsl:element>
							<xsl:text>
							</xsl:text>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			
			</xsl:element>

			<xsl:text>
			</xsl:text>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>

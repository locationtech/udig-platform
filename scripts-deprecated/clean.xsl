<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="psf">
		<xsl:element name="project">
			<xsl:attribute name="name">clean</xsl:attribute>
			<xsl:attribute name="default">clean</xsl:attribute>

			<xsl:element name="property">
				<xsl:attribute name="file">build.properties</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>
				
			<xsl:element name="target">
				<xsl:attribute name="name">clean</xsl:attribute>

				<xsl:text>
				</xsl:text>
				<xsl:element name="delete">
					<xsl:attribute name="dir">build/${buildLabel}</xsl:attribute>
				</xsl:element>

				<xsl:text>
				</xsl:text>
				<xsl:element name="delete">
					<xsl:attribute name="dir">build/tmp</xsl:attribute>
				</xsl:element>

				<xsl:element name="condition">
					<xsl:attribute name="property">doclean</xsl:attribute>
					<xsl:element name="and">
						<xsl:element name="available">
							<xsl:attribute name="file">build/plugins</xsl:attribute>
							<xsl:attribute name="type">dir</xsl:attribute>
						</xsl:element> 
						<xsl:text>
						</xsl:text>
						<xsl:element name="available">
							<xsl:attribute name="file">build/features</xsl:attribute>
							<xsl:attribute name="type">dir</xsl:attribute>
						</xsl:element> 
						<xsl:text>
						</xsl:text>
					</xsl:element>
				</xsl:element>

				<xsl:element name="antcall">
					<xsl:attribute name="target">cleanAll</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">cleanAll</xsl:attribute>
				<xsl:attribute name="if">doclean</xsl:attribute>
				<xsl:text>
				</xsl:text>
				<xsl:apply-templates select="provider/project"/>
			</xsl:element>
			<xsl:text>
			</xsl:text>

		</xsl:element>
	</xsl:template>

	<xsl:template match="project">
		<xsl:variable name="ref"><xsl:value-of select="@reference"/></xsl:variable>
		<xsl:if test="contains($ref,'features')">	
			<xsl:variable name="fid"><xsl:value-of select="substring-after(substring-after($ref,','),',')"/></xsl:variable>
			<xsl:variable name="path">build/features/<xsl:value-of select="$fid"/></xsl:variable>
			<xsl:element name="delete">
				<xsl:attribute name="file"><xsl:value-of select="$path"/>/build.xml</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="delete">
				<xsl:attribute name="file">build/assemble.<xsl:value-of select="$fid"/>.all.xml</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>
			<xsl:element name="delete">
				<xsl:attribute name="file">build/assemble.<xsl:value-of select="$fid"/>.xml</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>
			<xsl:element name="delete">
				<xsl:attribute name="file">build/package.<xsl:value-of select="$fid"/>.xml</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>
			<xsl:element name="delete">
				<xsl:attribute name="file">build/package.<xsl:value-of select="$fid"/>.all.xml</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>
		</xsl:if>

		<xsl:if test="contains($ref,'plugins')">
			<xsl:variable name="path">build/plugins/<xsl:value-of select="substring-after(substring-after($ref,','),',')"/></xsl:variable>
			<xsl:element name="delete">
				<xsl:attribute name="file"><xsl:value-of select="$path"/>/build.xml</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>
			<xsl:element name="delete">
				<xsl:attribute name="dir"><xsl:value-of select="$path"/>/temp.folder</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>
			<xsl:element name="delete">
				<xsl:element name="fileset">
					<xsl:attribute name="dir"><xsl:value-of select="$path"/></xsl:attribute>
					<xsl:attribute name="includes">*.jar</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:text>
			</xsl:text>
		</xsl:if>

    <xsl:if test="contains($ref,'fragments')">
      <xsl:variable name="frid"><xsl:value-of select="substring-after(substring-after($ref,','),',')"/></xsl:variable>
      <xsl:variable name="path">build/plugins/<xsl:value-of select="$frid"/></xsl:variable>
      <xsl:element name="delete">
        <xsl:attribute name="dir"><xsl:value-of select="$path"/></xsl:attribute>
      </xsl:element>
      <xsl:text>
      </xsl:text>
    </xsl:if>

	</xsl:template>
</xsl:stylesheet>

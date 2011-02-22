<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="psf">
		<xsl:element name="project">
			<xsl:attribute name="name">log</xsl:attribute>
			<xsl:attribute name="default">log</xsl:attribute>

			<xsl:element name="property">
				<xsl:attribute name="file">build.properties</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">log</xsl:attribute>
				<xsl:text>
				</xsl:text>

				<xsl:element name="delete">
					<xsl:element name="fileset">
						<xsl:attribute name="dir">${basedir}</xsl:attribute>
						<xsl:attribute name="includes">*.log</xsl:attribute>
					</xsl:element>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="touch">
					<xsl:attribute name="file">build.log</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:for-each select="provider/project">
					<xsl:variable name="ref"><xsl:value-of select="@reference"/></xsl:variable>
					<xsl:if test="contains($ref,'plugins')">
						<xsl:variable name="pid"><xsl:value-of select="substring-after(substring-after($ref,','),',')"/></xsl:variable>

						<xsl:element name="available">
							<xsl:attribute name="file">build/plugins/<xsl:value-of select="$pid"/>/temp.folder</xsl:attribute>
							<xsl:attribute name="property"><xsl:value-of select="$pid"/>.exists</xsl:attribute>
						</xsl:element>

						<xsl:text>
						</xsl:text>
						<xsl:element name="antcall">
							<xsl:attribute name="target"><xsl:value-of select="$pid"/>.log</xsl:attribute>
						</xsl:element>
						<xsl:text>
						</xsl:text>

					</xsl:if>
				</xsl:for-each>
			<xsl:element name="move">
				<xsl:attribute name="file">build.log</xsl:attribute>
				<xsl:attribute name="tofile">build.log</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
			<xsl:element name="copy">
				<xsl:attribute name="file">build.log</xsl:attribute>
				<xsl:attribute name="todir">${log.dir}</xsl:attribute>
			</xsl:element>
			</xsl:element>

			<xsl:for-each select="provider/project">
				<xsl:variable name="ref"><xsl:value-of select="@reference"/></xsl:variable>
				<xsl:if test="contains($ref,'plugins')">
						<xsl:variable name="pid"><xsl:value-of select="substring-after(substring-after($ref,','),',')"/></xsl:variable>

					<xsl:variable name="path">build/plugins/<xsl:value-of select="$pid"/>/temp.folder</xsl:variable>
					<xsl:element name="target">
						<xsl:attribute name="name"><xsl:value-of select="$pid"/>.log</xsl:attribute>
						<xsl:attribute name="if"><xsl:value-of select="$pid"/>.exists</xsl:attribute>
						<xsl:text>
						</xsl:text>
						<xsl:element name="concat">
							<xsl:attribute name="destfile">build.log</xsl:attribute>
							<xsl:attribute name="append">true</xsl:attribute>
							<xsl:text>
							</xsl:text>
							<xsl:element name="fileset">
								<xsl:attribute name="dir"><xsl:value-of select="$path"/></xsl:attribute>
								<xsl:attribute name="includes">*.log</xsl:attribute>
							</xsl:element>
							<xsl:text>
							</xsl:text>
						</xsl:element>
						<xsl:text>
						</xsl:text>
					</xsl:element>
					<xsl:text>
					</xsl:text>
				</xsl:if>
			</xsl:for-each>
			<xsl:text>
			</xsl:text>

		</xsl:element>
	</xsl:template>

</xsl:stylesheet>

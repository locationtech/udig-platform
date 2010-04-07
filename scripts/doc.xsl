<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="psf">
		<xsl:element name="project">
			<xsl:attribute name="name">doc</xsl:attribute>
			<xsl:attribute name="default">doc</xsl:attribute>
				
			<xsl:element name="property">
				<xsl:attribute name="file">build.properties</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">doc</xsl:attribute>
				<xsl:text>
				</xsl:text>

				<xsl:element name="delete">
					<xsl:attribute name="dir">${javadoc.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="mkdir">
					<xsl:attribute name="dir">${javadoc.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="delete">
					<xsl:attribute name="dir">${schemadoc.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="mkdir">
					<xsl:attribute name="dir">${schemadoc.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="delete">
					<xsl:element name="fileset">
						<xsl:attribute name="dir">${basedir}</xsl:attribute>
						<xsl:element name="include">
							<xsl:attribute name="name">${javadoc.archive}</xsl:attribute>	
						</xsl:element>
						<xsl:element name="include">
							<xsl:attribute name="name">${schemadoc.archive}</xsl:attribute>	
						</xsl:element>
					</xsl:element>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="javadoc">
					<xsl:attribute name="destdir">${javadoc.dir}</xsl:attribute>

					<xsl:for-each select="provider/project">
						<xsl:variable name="ref"><xsl:value-of select="@reference"/></xsl:variable>
						<xsl:if test="contains($ref,'plugins')">
							<xsl:variable name="pid"><xsl:value-of select="substring-after(substring-after($ref,','),',')"/></xsl:variable>
							<xsl:choose>
								<xsl:when test="contains($pid,'tests')">

								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="fileset">
										<xsl:attribute name="dir">${buildDirectory}/plugins/<xsl:value-of select="$pid"/></xsl:attribute>
										<xsl:attribute name="includes">**/*.java</xsl:attribute>
									</xsl:element>
									<xsl:text>
									</xsl:text>
								</xsl:otherwise>
							</xsl:choose>

						</xsl:if>
					</xsl:for-each>

				</xsl:element>

				<xsl:element name="zip">
					<xsl:attribute name="destfile">${javadoc.archive}</xsl:attribute>
					<xsl:attribute name="basedir">${javadoc.dir}</xsl:attribute>
				</xsl:element>

        <xsl:for-each select="provider/project">
          <xsl:variable name="ref"><xsl:value-of select="@reference"/></xsl:variable>
          <xsl:if test="contains($ref,'plugins')">
            <xsl:variable name="pid"><xsl:value-of select="substring-after(substring-after($ref,','),',')"/></xsl:variable>
						<xsl:element name="mkdir">
							<xsl:attribute name="dir">${schemadoc.dir}/<xsl:value-of select="$pid"/></xsl:attribute>
						</xsl:element>
						<xsl:element name="copy"> 
							<xsl:attribute name="todir">${schemadoc.dir}/<xsl:value-of select="$pid"/></xsl:attribute>
							<xsl:attribute name="flatten">true</xsl:attribute>
            	<xsl:element name="fileset">
              	<xsl:attribute name="dir">${buildDirectory}/plugins/</xsl:attribute>
              	<xsl:attribute name="includes"><xsl:value-of select="$pid"/>/doc/*.html</xsl:attribute>
            	</xsl:element>
            	<xsl:text>
            	</xsl:text>
						</xsl:element> <!-- copy -->
          </xsl:if>
        </xsl:for-each>

	      <xsl:element name="zip">
 	       <xsl:attribute name="destfile">${schemadoc.archive}</xsl:attribute>
				 <xsl:element name="fileset">
 	       	<xsl:attribute name="dir">${schemadoc.dir}</xsl:attribute>
					<xsl:attribute name="includes">**/*.html</xsl:attribute>
				</xsl:element>
 	     </xsl:element>

      </xsl:element>


		</xsl:element>

	</xsl:template>

</xsl:stylesheet>

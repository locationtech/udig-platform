<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="manifest">

		<xsl:element name="project">
			<xsl:attribute name="name">Build specific targets and properties</xsl:attribute>
			<xsl:attribute name="default">noDefault</xsl:attribute>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">allElements</xsl:attribute>

				<xsl:text>
				</xsl:text>

				<xsl:for-each select="features/feature">
					<xsl:text>
					</xsl:text>
					<xsl:element name="ant">
						<xsl:attribute name="antfile">${genericTargets}</xsl:attribute>
						<xsl:attribute name="target">${target}</xsl:attribute>
						<xsl:text>
						</xsl:text>
						<xsl:element name="property">
							<xsl:attribute name="name">type</xsl:attribute>
							<xsl:attribute name="value">feature</xsl:attribute>
						</xsl:element>
						<xsl:text>
						</xsl:text>
						<xsl:element name="property">
							<xsl:attribute name="name">id</xsl:attribute>
							<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
						</xsl:element>
					</xsl:element>
					<xsl:text>
					</xsl:text>
				</xsl:for-each>
				<xsl:text>
				</xsl:text>
			</xsl:element> <!-- allElements -->

			<xsl:text>
			</xsl:text>

			<xsl:for-each select="features/feature">
				<xsl:element name="target">
					<xsl:attribute name="name">assemble.<xsl:value-of select="."/></xsl:attribute>
					<xsl:text>
					</xsl:text>

					<xsl:element name="ant">
						<xsl:attribute name="antfile">${assembleScriptName}</xsl:attribute>
						<xsl:attribute name="dir">${buildDirectory}</xsl:attribute>
					</xsl:element>
				</xsl:element>
				<xsl:text>
				</xsl:text>
			</xsl:for-each>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">preSetup</xsl:attribute>
				<xsl:text>
				</xsl:text>
				<xsl:element name="tstamp"/>
			</xsl:element>
				<xsl:element name="copy">
					<xsl:attribute name="todir">build/plugins</xsl:attribute>
					<xsl:element name="fileset">
						<xsl:attribute name="dir">build/fragments</xsl:attribute>
						<xsl:attribute name="excludes">**/.svn</xsl:attribute>
					</xsl:element>
				</xsl:element>
				<xsl:text>
				</xsl:text>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">postSetup</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">preFetch</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">postFetch</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">preGenerate</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">postGenerate</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">preProcess</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">postProcess</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">preAssemble</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">postAssemble</xsl:attribute>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">postBuild</xsl:attribute>
				<xsl:text>
				</xsl:text>
				<xsl:element name="available">
					<xsl:attribute name="property">build.exists</xsl:attribute>
					<xsl:attribute name="file">build/${buildLabel}/uDigRelease-${buildId}.zip</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="antcall">
					<xsl:attribute name="target">package</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="ant">
					<xsl:attribute name="antfile">gen.xml</xsl:attribute>
					<xsl:attribute name="target">log</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="antcall">
					<xsl:attribute name="antfile">doc.xml</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="antcall">
				<xsl:element name="ant">
					<xsl:attribute name="antfile">gen.xml</xsl:attribute>
					<xsl:attribute name="target">doc</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="available">
					<xsl:attribute name="property">doc.exists</xsl:attribute>
					<xsl:attribute name="file">doc.xml</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="antcall">
					<xsl:attribute name="target">doc</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>

				<xsl:element name="antcall">
					<xsl:attribute name="target">test</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">test</xsl:attribute>
				<xsl:text>
				</xsl:text>
				<xsl:element name="delete">
					<xsl:attribute name="dir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="mkdir">
					<xsl:attribute name="dir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="unzip">
					<xsl:attribute name="src">${rcp.file}</xsl:attribute>
					<xsl:attribute name="dest">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="unzip">
					<xsl:attribute name="src">uDig.zip</xsl:attribute>
					<xsl:attribute name="dest">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="zip">
					<xsl:attribute name="destfile">uDig.zip</xsl:attribute>
					<xsl:attribute name="basedir">${test.dir}</xsl:attribute>
					<xsl:element name="fileset">
						<xsl:attribute name="dir">${test.dir}</xsl:attribute>
						<xsl:attribute name="includes">eclipse</xsl:attribute>
					</xsl:element>
					<xsl:text>
					</xsl:text>
				</xsl:element>
				<xsl:element name="copy">
					<xsl:attribute name="file">uDig.zip</xsl:attribute>
					<xsl:attribute name="todir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="delete">
						<xsl:attribute name="dir">${test.dir}${file.separator}eclipse</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="copy">
					<xsl:attribute name="file">uDig-tests.zip</xsl:attribute>
					<xsl:attribute name="todir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="copy">
					<xsl:attribute name="file">test.xml</xsl:attribute>
					<xsl:attribute name="todir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="copy">
					<xsl:attribute name="file">test.properties</xsl:attribute>
					<xsl:attribute name="todir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="copy">
					<xsl:attribute name="file">JUNIT.XSL</xsl:attribute>
					<xsl:attribute name="todir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="copy">
					<xsl:attribute name="file">runtests.sh</xsl:attribute>
					<xsl:attribute name="todir">${test.dir}</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="exec">
					<xsl:attribute name="dir">${test.dir}</xsl:attribute>
					<xsl:attribute name="executable">sh</xsl:attribute>
					<xsl:text>
					</xsl:text>
					<xsl:element name="arg">
						<xsl:attribute name="line">runtests.sh -os ${baseos} -ws ${basews} -arch ${basearch}</xsl:attribute>
					</xsl:element>
					<xsl:text>
					</xsl:text>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="zip">
					<xsl:attribute name="destfile">uDig-testreport.zip</xsl:attribute>
					<xsl:attribute name="basedir">${test.dir}${file.separator}results${file.separator}html</xsl:attribute>
					<xsl:element name="fileset">
						<xsl:attribute name="dir">${test.dir}${file.separator}results${file.separator}html</xsl:attribute>
						<xsl:attribute name="includes">*.html</xsl:attribute>
					</xsl:element>
					<xsl:text>
					</xsl:text>
				</xsl:element>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">doc</xsl:attribute>
				<xsl:attribute name="if">doc.exists</xsl:attribute>
				<xsl:text>
				</xsl:text>
				<xsl:element name="ant">
					<xsl:attribute name="antfile">doc.xml</xsl:attribute>
				</xsl:element>
				<xsl:text>
				</xsl:text>
				<xsl:element name="zip">
					<xsl:attribute name="destfile">uDig-javadoc.zip</xsl:attribute>
					<xsl:attribute name="basedir">build/doc</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">package</xsl:attribute>
				<xsl:attribute name="if">build.exists</xsl:attribute>
				<xsl:text>
				</xsl:text>
				<xsl:element name="copy">
					<xsl:attribute name="file">build/${buildType}/uDigRelease-${buildId}.zip</xsl:attribute>
					<xsl:attribute name="tofile">uDig.zip</xsl:attribute>
				</xsl:element>
				<xsl:element name="copy">
					<xsl:attribute name="file">build/${buildType}/uDigTest-${buildId}.zip</xsl:attribute>
					<xsl:attribute name="tofile">uDig.zip</xsl:attribute>
				</xsl:element>
			</xsl:element>
			<xsl:text>
			</xsl:text>

			<xsl:element name="target">
				<xsl:attribute name="name">noDefault</xsl:attribute>
				<xsl:text>
				</xsl:text>
				<xsl:element name="echo">
					<xsl:attribute name="message">You must specify a target when invoking this file</xsl:attribute>
				</xsl:element>

			</xsl:element>

		</xsl:element>  <!-- project -->

	</xsl:template>
</xsl:stylesheet>

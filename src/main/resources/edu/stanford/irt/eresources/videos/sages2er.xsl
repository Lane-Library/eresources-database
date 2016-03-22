<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">


	<xsl:template match="html">
		<xsl:variable name="pageId" select="@id"></xsl:variable>

		<xsl:for-each select=".//div[@class='index-post']">
			<xsl:variable name="id" select="position()" />
			<eresource>
				<xsl:attribute name="id"><xsl:value-of select="concat($pageId, $id)" /></xsl:attribute>
				<xsl:attribute name="type">sages</xsl:attribute>
				<xsl:attribute name="update">1969010100000</xsl:attribute>
				<title>
					<xsl:value-of select="./div/h3/a/text()"/>
				</title>
				<primaryType>Visual Material</primaryType>
				<type>Instructional Video</type>
				<type>Video</type>
				<keywords>
					<xsl:value-of select="concat('sages ',./div/h3, ' ', ./div/p/text())"></xsl:value-of>
				</keywords>
				<year>
					<xsl:value-of select="replace(./div/dl[@class='post-meta']/dd[1]/text(),'.*/(\d{4})','$1')" />
				</year>
				<er-date><xsl:value-of select="replace(./div/dl[@class='post-meta']/dd[1]/text(),'(\d{1,2})/(\d{1,2})/(\d{4}).*','$3 $1 $2')"/></er-date>
				<version>
					<link>
						<url>
							<xsl:value-of select="./div/h3/a/@href"></xsl:value-of>
						</url>
					</link>
				</version>
				<description>
					<xsl:value-of select="./div/p/text()"></xsl:value-of>
				</description>
			</eresource>
		</xsl:for-each>
		
	</xsl:template>
</xsl:stylesheet>
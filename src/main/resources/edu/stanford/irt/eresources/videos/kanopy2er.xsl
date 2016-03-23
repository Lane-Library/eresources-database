<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml"
	version="2.0">

	<xsl:template match="//div[@class='results']/ul/li">
		<xsl:variable name="page_id">
			<xsl:value-of select="ancestor::*/page_id/text()" />
		</xsl:variable>
		<xsl:variable name="title" select="./div/div[@class='span7']/a/text()" />
		<xsl:variable name="description" select="normalize-space(./div/div[@class='span7']/ul)" />
		<eresource>
			<xsl:attribute name="id"><xsl:value-of select="concat($page_id, position())" /></xsl:attribute>
			<xsl:attribute name="type">kanopy</xsl:attribute>
			<xsl:attribute name="update">1969010100000</xsl:attribute>
			<title>
				<xsl:value-of select="$title" />
			</title>
			<primaryType>Visual Material</primaryType>
			<type>Instructional Video</type>
			<type>Video</type>
			<version>
				<link>
					<url>
						<xsl:value-of select="concat('https://stanford.kanopystreaming.com', ./div/div[@class='span7']/a/@href)" />
					</url>
				</link>
			</version>
			<description>
				<xsl:value-of select="$description" />
			</description>
			<keywords>
				<xsl:value-of select="concat('stanford kanopystreaming kanopy streaming ' ,' ', $title, ' ', $description)" />
			</keywords>
		</eresource>
	</xsl:template>

</xsl:stylesheet>
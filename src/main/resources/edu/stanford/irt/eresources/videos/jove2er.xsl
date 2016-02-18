<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">


	
	<xsl:template match="article[contains(@class, 'article_summary_container')]">
			<xsl:variable name="id" select="substring-before(substring-after(@id,'_'),'_')" ></xsl:variable> 
			<eresource>
				<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
				<xsl:attribute name="type">jove</xsl:attribute>
				<xsl:attribute name="update">1969010100000</xsl:attribute>
				<title>
					 <xsl:value-of select="./h2/a/text()"/> 
				</title>
				<primaryType>Visual Material</primaryType>
				<type>Instructional Video</type>
				<type>Video</type>
				<version>
					<link>
						<url>
							<xsl:value-of select="concat('http://www.jove.com',./h2/a/@href)"/>
						</url>
					</link>
				</version>
			</eresource>
	</xsl:template> 
</xsl:stylesheet>
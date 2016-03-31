<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">


	
	<xsl:template match="div[@class='article-thumbnail']">
		<xsl:variable name="url" select="./a/@href"/>
		<xsl:if test="not(contains($url, 'notification'))">
			<xsl:variable name="id"  select="position()"/>
			 
			<xsl:variable name="title" select="./a/@title" /> 
			<eresource>
				<xsl:attribute name="id">jomi-<xsl:value-of select="$id"/></xsl:attribute>
				<xsl:attribute name="recordId"><xsl:value-of select="$id"/></xsl:attribute>
				<xsl:attribute name="type">instructional_videos</xsl:attribute>
				<xsl:attribute name="update">1969010100000</xsl:attribute>
				<title>
					 <xsl:value-of select="$title"/> 
				</title>
				<primaryType>Visual Material</primaryType>
				<type>Video: Instructional</type>
				<type>Video: Surgery</type>
				<type>Video</type>
				<keywords>
					<xsl:value-of select="concat('jomi ' ,' ', $title, ' ')" />
				</keywords>
				<version>
					<link>
						<url>
							<xsl:value-of select="$url"/>
						</url>
					</link>
				</version>
				<xsl:if test="./div[@class='article-overlay']/p/a/text() != ''">
					<publicationAuthorsText>
						<xsl:value-of select="./div[@class='article-overlay']/p/a/text()" />
					</publicationAuthorsText>
				</xsl:if>
			
			</eresource>
			</xsl:if>
	</xsl:template> 
	
	
</xsl:stylesheet>
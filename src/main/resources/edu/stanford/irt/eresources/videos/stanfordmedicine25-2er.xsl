<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">


	<xsl:template match="html">
		
		<xsl:variable name="pageid">
			<xsl:value-of  select="@pageid"/>
		</xsl:variable>
		<xsl:variable name="url">
			<xsl:value-of  select="@url"/>
		</xsl:variable>
		
		
			<xsl:if test="count(.//div[@class='videoWrapper']) != 0">
		
			<xsl:variable name="description" select=".//h2[contains(., 'Introduction' )]/../../../div[@class='parbase section text']//p"/>
			<xsl:variable name="title" select="normalize-space(.//hgroup[ ./h1[@class='black']/text() != ''])"></xsl:variable>
			<eresource>
				<xsl:attribute name="id"><xsl:value-of select="$pageid" /></xsl:attribute>
				<xsl:attribute name="type">stanfordmedicine25</xsl:attribute>
				<xsl:attribute name="update">1969010100000</xsl:attribute>
				<title>
					<xsl:value-of select="$title"/> 
				</title>
				<primaryType>Visual Material</primaryType>
				<type>Instructional Video</type>
				<type>Video</type>
				 <keywords>
					<xsl:value-of select="concat('physical exam ', ' stanfordmedicine25 stanfordmedicine ', $title , ' ', string-join($description, ' ' ))"></xsl:value-of>					
				</keywords>
				<version>
					<link>
						<url>
							 <xsl:value-of select="$url"/> 
						</url>
					</link>
				</version>
				<description>
					<xsl:value-of select="$description"></xsl:value-of>
				</description>
			</eresource>
			
			</xsl:if>
		
	</xsl:template>
</xsl:stylesheet>
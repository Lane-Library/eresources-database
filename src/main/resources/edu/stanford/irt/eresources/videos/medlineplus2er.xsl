<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:h="http://www.w3.org/1999/xhtml" version="2.0">

<!-- Luckily the surgery html page need the namespace http://www.w3.org/1999/xhtml -->
<!--template for the Health Videos page -->
<xsl:template match="html">
	<eresource>
	<xsl:variable name="title" select=".//div[@id='d-article']/div/div/h1/text()"/>
	<xsl:variable name="description" select=".//div[@class='ibox-note']/p/text()"/>
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:attribute name="type">medlineplus</xsl:attribute>
			<xsl:attribute name="update">19690101000000</xsl:attribute>
			<title>
				   <xsl:value-of select="$title"/>
			</title>
			<primaryType>Visual Material</primaryType>
			<type>Video</type>
			<type>Instructional Video</type>
			<keywords>
				   <xsl:value-of select="concat($title, ' ',  $description[1])"/>
			</keywords>
			<year><xsl:value-of select="replace(.//span[@itemprop='dateModified']/text(),'.*/(\d{4})','$1')"/></year>
			<er-date><xsl:value-of select="replace(.//span[@itemprop='dateModified']/text(),'(\d{1,2})/(\d{1,2})/(\d{4}).*','$3 $1 $2')"/></er-date>
			<version>
				<link>
					<url>
					 <xsl:value-of select="@url"/>
					</url>
				</link>
			</version>
			<description>
				<xsl:value-of select=" $description[1]"/>
			</description>
		</eresource>
</xsl:template>	


<!--template for the surgery  Videos page -->
<xsl:template match="h:html">
	<xsl:variable name="title" select=".//div[@class='nlmTitle']/text()"/>
	<xsl:variable name="description" select=".//meta[@property='og:description']/@content" />
	<xsl:variable name="date">
		<xsl:choose>
			<xsl:when test=".//div[@class='nlmTime']/script != ''">
				<xsl:value-of select="substring-before(substring-after(.//div[@class='nlmTime']/script, 'utcYear = '), ';')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>0</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<eresource>
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:attribute name="type">medlineplus</xsl:attribute>
			<xsl:attribute name="update">19690101000000</xsl:attribute>
			<title>
			 	<xsl:value-of select="$title"/>
			</title>
			<primaryType>Visual Material</primaryType>
			<type>Video</type>
			<type>Instructional Video</type>
			<keywords>
				   <xsl:value-of select="concat($title,' ', $description)"/>
			</keywords>
			<year><xsl:value-of select="$date"/></year>
			<version>
				<link>
					<url>
						<xsl:value-of select="@url"/>
					</url>
				</link>
			</version>
			<description>
				<xsl:value-of select="$description"/>
			</description>
	</eresource>
	
</xsl:template>	
	

	
</xsl:stylesheet>
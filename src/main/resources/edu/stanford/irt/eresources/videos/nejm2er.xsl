<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" version="2.0">


<xsl:template match="item">
	<xsl:variable name="title" select="title"/>
	<xsl:variable name="description" select="description"/>
	<eresource>
			<xsl:attribute name="id">nejm-<xsl:value-of select="substring-after(@rdf:about, 'NEJMvcm')"/></xsl:attribute>
			<xsl:attribute name="recordId"><xsl:value-of select="substring-after(@rdf:about, 'NEJMvcm')"/></xsl:attribute>
			<xsl:attribute name="type">instructional_videos</xsl:attribute>
			<xsl:attribute name="update">19690101000000</xsl:attribute>
			<title><xsl:value-of select="$title"/></title>
			<primaryType>Visual Material</primaryType>
			<type>Video: Instructional</type>
			<type>Video</type>
			<keywords>
				<xsl:value-of select="concat('nejm ' , $title, ' ' , $description)"/>
			</keywords>
			<year><xsl:value-of select="substring-before(dc:date, '-')"/></year>
			<er-date><xsl:value-of select="replace(./dc:date,'(\d{4})-(\d{1,2})-(\d{1,2}).*','$1 $2 $3')"/></er-date>
			<version>
				<link>
					<url>
						<xsl:value-of select="@rdf:about"/>
					</url>
				</link>
			</version>
	</eresource>
	
</xsl:template>



</xsl:stylesheet>
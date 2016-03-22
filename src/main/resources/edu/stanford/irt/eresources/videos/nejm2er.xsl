<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">



<xsl:template match="item">
	<eresource>
			<xsl:attribute name="id"><xsl:value-of select="substring-after(@rdf:about, 'NEJMvcm')"/></xsl:attribute>
			<xsl:attribute name="type">nejm</xsl:attribute>
			<xsl:attribute name="update">19690101000000</xsl:attribute>
			<title><xsl:value-of select="title"/></title>
			<primaryType>Visual Material</primaryType>
			<type>Instructional Video</type>
			<type>Video</type>
			<keywords>
				<xsl:value-of select="concat('nejm' , $title, ' ' , $description)"/>
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
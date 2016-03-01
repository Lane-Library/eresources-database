<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">



	<xsl:template match="table[@id='pageContent_GridView1']">
		<xsl:for-each select="tr[position() >1]">

			<eresource>
				<xsl:attribute name="id"><xsl:value-of select="./td[1]/text()" /></xsl:attribute>
				<xsl:attribute name="type">accessmedicine</xsl:attribute>
				<xsl:attribute name="update">19690101000000</xsl:attribute>
				<title>
					<xsl:value-of select="./td[2]" />
				</title>
				<primaryType>Visual Material</primaryType>
				<type>Video</type>
				<type>Instructional Video</type>
				<keywords>
					<xsl:value-of select="concat(./td[2], ' ', ./td[3]),' ',./td[6]" />
				</keywords>
				<year>
					<xsl:value-of select="replace(./td[6],'.*/(\d{4})','$1')" />
				</year>
				<er-date><xsl:value-of select="replace(./td[6],'(\d{1,2})/(\d{1,2})/(\d{4}).*','$3 $1 $2')"/></er-date>
				<version>
					<link>
						<url>
							<xsl:value-of select="./td[7]" />
						</url>
					</link>
				</version>
				<xsl:if test="./td[3]/text() != ''">
					<publicationAuthorsText>
						<xsl:value-of select="normalize-space(./td[3]/text()[0])" />
					</publicationAuthorsText>
				</xsl:if>
			</eresource>

		</xsl:for-each>
	</xsl:template>




</xsl:stylesheet>
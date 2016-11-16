<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">

	<xsl:template match="channel/item">
		<eresource  id="laneblog-{substring-after(guid,'?p=')}" recordId="{substring-after(guid,'?p=')}" type="laneblog"
			update="19690101000000">
			<title>
				<xsl:value-of select="title" />
			</title>
			<primaryType>lanepage</primaryType>
			<type>lanesite</type>
			<keywords>
				<xsl:value-of select="concat(title, ' ', description)" />
			</keywords>
			<year><xsl:value-of select="replace(pubDate,'.* (\d{4}).*','$1')"/></year>
			<er-date><xsl:value-of select="replace(pubDate,'.*, (\d{2}) ([A-Z][a-z]{2}) (\d{4}).*','$3 $2 $1')"/></er-date>
			<version>
				<link>
					<label>
						<xsl:value-of select="concat(substring(description, 1, 36),' . . .')" />
					</label>
					<url>
						<xsl:value-of select="link" />
					</url>
				</link>
			</version>
		</eresource>
	</xsl:template>

</xsl:stylesheet>
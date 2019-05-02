<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="2.0">

    <xsl:template match="channel/item">
        <!--  wordpress doesn't always produce numeric page/post ids -->
        <xsl:variable name="blog-id">
            <xsl:choose>
                <xsl:when test="replace(guid,'[^\d]','') castable as xs:integer">
                    <xsl:value-of select="replace(guid,'[^\d]','')"/>
                </xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <eresource  id="laneblog-{$blog-id}" recordId="{$blog-id}" type="web"
            update="19690101000000">
            <title>
                <xsl:value-of select="title" />
            </title>
            <primaryType>Lane Web Page</primaryType>
            <type>Lane Web Page</type>
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
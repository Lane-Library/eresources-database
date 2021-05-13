<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="2.0">

    <!-- basic name ordering for dublin core creator field: Jay Smith -> Smith, Jay -->
    <xsl:template name="dc-creator">
        <xsl:param name="creator"/>
            <xsl:choose>
                <xsl:when test="contains($creator,', ')">
                    <xsl:value-of select="normalize-space($creator)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:analyze-string select="normalize-space($creator)" regex="(.*) (.*)">
                        <xsl:matching-substring>
                            <xsl:value-of select="regex-group(2)"/>
                            <xsl:text>, </xsl:text>
                            <xsl:value-of select="regex-group(1)"/>
                        </xsl:matching-substring>
                    </xsl:analyze-string>
                </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns="http://www.w3.org/1999/xhtml"
        version="2.0">

    <xsl:template match="html">
        <eresource  id="web-{@id}" recordId="{@id}" type="web" update="{@update}">
            <xsl:variable name="keywords">
                <xsl:apply-templates/>
            </xsl:variable>
            <title><xsl:value-of select="@title"/></title>
            <primaryType>Lane Web Page</primaryType>
            <type>Lane Web Page</type>
            <keywords> <xsl:value-of select="$keywords"/> </keywords>
            <year><xsl:value-of select="substring(@update,1,4)"/></year>
            <er-date><xsl:value-of select="substring(@update,1,8)"/></er-date>
            <description>
                <xsl:value-of select="@description"/>
            </description>
            <version>
                <link>
                    <url><xsl:value-of select="@link"/></url>
                </link>
            </version>
        </eresource>
    </xsl:template>
    
    <xsl:template match="@alt | @title | @href | @content | text()" priority="1">
        <xsl:variable name="text" select="normalize-space()"/>
        <xsl:if test="string-length($text) &gt; 0">
            <xsl:value-of select="$text"/>
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="child::node()">
        <xsl:apply-templates select="attribute::node() | child::node()"/>
    </xsl:template>
    
    <xsl:template match="script | style | link | meta | nav | header | footer | div[@class='hero-unit']"/>
    
    <xsl:template match="attribute::node()"/>
    
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:h="http://www.w3.org/1999/xhtml" version="2.0">
    
    <xsl:param name="lane-host"/>

    <!--  http://stackoverflow.com/questions/33256226/warning-messages-appeared-after-upgrade-saxon-to-9-5-1-8 -->    
    <xsl:template match="dummy-template-to-suppress-namespace-check-message-when-no-html-files-are-processed"/>

    <xsl:template match="h:html">
        <eresource  id="web-{@id}" recordId="{@id}" type="web" update="{@update}">
            <xsl:variable name="url">
                <xsl:value-of select="$lane-host"/>
                <xsl:value-of select="@file"/>
            </xsl:variable>
            <xsl:variable name="keywords">
                <xsl:apply-templates/>
            </xsl:variable>
            <xsl:variable name="title" select="normalize-space(h:head/h:title)"/>
            <xsl:variable name="excerpt">
                <xsl:value-of select="substring($keywords, string-length($title) + 2, 65)"/>
            </xsl:variable>
            <title><xsl:value-of select="$title"/></title>
            <primaryType>lanepage</primaryType>
            <type>Lane Web Page</type>
            <keywords> <xsl:value-of select="$keywords"/> </keywords>
            <xsl:choose>
                <!-- kludge to down-weight history content by using it's publication date instead of file date
                    these pages have lots of content but are rarely clicked
                    might be nice to parse date from some meta element on page
                -->
                <xsl:when test="contains($url,'med-history/wilson')">
                    <year>1998</year>
                    <er-date>19980101</er-date>
                </xsl:when>
                <xsl:otherwise>
                    <year><xsl:value-of select="substring(@update,1,4)"/></year>
                    <er-date><xsl:value-of select="substring(@update,1,8)"/></er-date>
                </xsl:otherwise>
            </xsl:choose>
            <version>
                <link>
                    <label>
                        <xsl:value-of select="$excerpt"/><xsl:text> . . .</xsl:text>
                    </label>
                    <url><xsl:value-of select="$url"/></url>
                </link>
            </version>
        </eresource>
    </xsl:template>
    
    <xsl:template match="@alt | @title | @href | text()" priority="1">
        <xsl:variable name="text" select="normalize-space()"/>
        <xsl:if test="string-length($text) &gt; 0">
            <xsl:value-of select="$text"/>
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="child::node()">
        <xsl:apply-templates select="attribute::node() | child::node()"/>
    </xsl:template>
    
    <xsl:template match="h:script | h:style"/>
    
    <xsl:template match="attribute::node()"/>
    
</xsl:stylesheet>

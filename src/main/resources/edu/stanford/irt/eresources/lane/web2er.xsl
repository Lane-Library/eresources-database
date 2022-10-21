<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:h="http://www.w3.org/1999/xhtml"
        xmlns:xi="http://www.w3.org/2001/XInclude"
        version="2.0">

    <xsl:param name="lane-host"/>

    <xsl:include href="classpath:/edu/stanford/irt/eresources/lane/dc-creator.xsl"/>

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
            <title><xsl:value-of select="$title"/></title>
            <primaryType>Lane Web Page</primaryType>
            <type>Lane Web Page</type>
            <keywords> <xsl:value-of select="$keywords"/> </keywords>
            <year><xsl:value-of select="substring(@update,1,4)"/></year>
            <er-date><xsl:value-of select="substring(@update,1,8)"/></er-date>
            <version>
                <link>
                    <url><xsl:value-of select="$url"/></url>
                </link>
            </version>
            <xsl:for-each select=".//h:meta[@name='DC.Creator']">
                <xsl:variable name="creator">
                    <xsl:call-template name="dc-creator">
                        <xsl:with-param name="creator" select="@content"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:if test="contains($creator,', ')">
                    <publicationAuthor><xsl:value-of select="$creator"/></publicationAuthor>
                    <publicationAuthorFacetable><xsl:value-of select="$creator"/></publicationAuthorFacetable>
                </xsl:if>
            </xsl:for-each>
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
    
    <xsl:template match="h:script | h:style | xi:include"/>
    
    <xsl:template match="attribute::node()"/>
    
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    version="2.0">

    <xsl:include href="classpath:/edu/stanford/irt/eresources/lane/dc-creator.xsl"/>

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
        <xsl:variable name="creator">
            <xsl:call-template name="dc-creator">
                <xsl:with-param name="creator" select="normalize-space(dc:creator)"/>
            </xsl:call-template>
        </xsl:variable>
        <eresource  id="laneblog-{$blog-id}" recordId="{$blog-id}" type="web"
            update="19690101000000">
            <title>
                <xsl:value-of select="title" />
            </title>
            <primaryType>Lane Web Page</primaryType>
            <type>Lane Web Page</type>
            <keywords>
                <xsl:value-of select="concat(title, ' ', description, ' ', $creator, ' ', link)" />
            </keywords>
            <year><xsl:value-of select="replace(pubDate,'.* (\d{4}).*','$1')"/></year>
            <er-date><xsl:value-of select="replace(pubDate,'.*, (\d{2}) ([A-Z][a-z]{2}) (\d{4}).*','$3 $2 $1')"/></er-date>
            <xsl:if test="contains($creator,', ')">
                <publicationAuthor><xsl:value-of select="$creator"/></publicationAuthor>
                <publicationAuthorFacetable><xsl:value-of select="$creator"/></publicationAuthorFacetable>
            </xsl:if>
            <version>
                <link>
                    <!-- LANEWEB-10684: image elements appearing in search results; may need to strip other tags -->
                    <xsl:variable name="apostrophe">'</xsl:variable>
                    <xsl:variable name="quote">"</xsl:variable>
                    <xsl:variable name="desc">
                        <xsl:value-of select="replace(description,'&lt;img .*/&gt;','')"/>
                    </xsl:variable>
                    <xsl:variable name="desc">
                        <xsl:value-of select="replace($desc,'&amp;#160;',' ')"/>
                    </xsl:variable>
                    <xsl:variable name="desc">
                        <xsl:value-of select="replace($desc,'&amp;#8217;',$apostrophe)"/>
                    </xsl:variable>
                    <xsl:variable name="desc">
                        <xsl:value-of select="replace($desc,'&amp;#038;','&amp;')"/>
                    </xsl:variable>
                    <xsl:variable name="desc">
                        <xsl:value-of select="replace($desc,'&amp;#8230;','...')"/>
                    </xsl:variable>
                    <xsl:variable name="desc">
                        <xsl:value-of select="replace($desc,'&amp;#822[01];',$quote)"/>
                    </xsl:variable>
                    <label>
                        <xsl:value-of select="concat(substring($desc, 1, 46),' . . .')" />
                    </label>
                    <url>
                        <xsl:value-of select="link" />
                    </url>
                </link>
            </version>
        </eresource>
    </xsl:template>

</xsl:stylesheet>
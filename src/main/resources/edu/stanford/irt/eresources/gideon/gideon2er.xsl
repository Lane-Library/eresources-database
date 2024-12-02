<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://lane.stanford.edu/eresources#" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0">

    <xsl:template match="/records">
        <xsl:apply-templates select="record[not(type = 'delete') and (not(contains(page_url, '#')) or subcategory = 'general' or subcategory = 'summary')]"/>
    </xsl:template>

    <xsl:template match="record">
        <xsl:variable name="id" select="id" />
        <xsl:variable name="title" select="title" />
        <xsl:variable name="description" select="normalize-space(abstract)" />
        <xsl:variable name="category">
            <xsl:choose>
                <xsl:when test="category = 'ebooks'">eBooks</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat(upper-case(substring(category, 1, 1)), substring(category, 2))" />
                </xsl:otherwise>
            </xsl:choose>            
        </xsl:variable>
        <xsl:variable name="subjects">
            <xsl:for-each select="keywords/keyword">
                <xsl:value-of select="concat(., ' ')" />
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="keywords">
                <xsl:value-of select="concat('gideon ', $title, ' ', $description, ' ', $subjects, ' ', $category)" />
        </xsl:variable>
        <xsl:variable name="year" select="substring(update_date,0,4)" />
        <xsl:variable name="er-date">
            <xsl:value-of select="update_date" />
        </xsl:variable>
        <xsl:variable name="primary-type">
            <xsl:choose>
                <xsl:when test="category = 'ebooks'">Book Digital</xsl:when>
                <xsl:otherwise>Database</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <eresource id="gideon-{$id}" recordId="{$id}" type="gideon" update="19690101000000">
            <title>
                <xsl:value-of select="$title" />
            </title>
            <title_short>
                <xsl:value-of select="$title" />
            </title_short>
            <primaryType>
                <xsl:value-of select="$primary-type" />
            </primaryType>
            <type><xsl:value-of select="$primary-type"/></type>
            <xsl:if test="$primary-type = 'Book Digital'">
                <type>Book</type>
            </xsl:if>
            <keywords>
                <xsl:value-of select="$keywords" />
            </keywords>
            <year>
                <xsl:value-of select="$year" />
            </year>
            <er-date>
                <xsl:value-of select="$er-date" />
            </er-date>
            <description>
                <xsl:value-of select="$description" />
            </description>
            <xsl:for-each select="keywords/keyword">
                <mesh><xsl:value-of select="."/></mesh>
            </xsl:for-each>
            <version>
                <link>
                    <label>GIDEON &#8211; <xsl:value-of select="$category" /></label>
                    <url><xsl:value-of select="page_url" /></url>    
                </link>
            </version>
        </eresource>
    </xsl:template>

</xsl:stylesheet>

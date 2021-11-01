<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="2.0">
    
    <!--
    TODO: 
     - issns for covers (maybe re-architect bookcovers to use native solr data instead of sul-1234, bib-1234 lookups from bookcovers?) 
     - broad mesh? available in NLM interface but not API
       https://www-ncbi-nlm-nih-gov.laneproxy.stanford.edu/nlmcatalog/?term=101609037
       https://www.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nlmcatalog&retmode=xml&id=101609037
     - dedup with searchworks (unnecessary?)
     - ?term="" links like 101609037 (maybe unnecessary if limited to "full" participation?)
     -->

    <xsl:template match="NLMCatalogRecord">
        <xsl:variable name="year">
            <xsl:choose>
                <xsl:when test="PublicationInfo/PublicationEndYear = '9999'">
                    <xsl:value-of select="format-dateTime(current-dateTime(),'[Y,4]')"/>
                </xsl:when>
                <xsl:when test="PublicationInfo/PublicationEndYear">
                    <xsl:value-of select="PublicationInfo/PublicationEndYear"/>
                </xsl:when>
                <xsl:otherwise>0000</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="summary-holdings">
            <xsl:choose>
                <xsl:when test="PublicationInfo/PublicationEndYear = '9999'">
                    <xsl:value-of select="replace(//@earliestVolume,';','-, ')"/>
                    <xsl:text>- </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="//@earliestVolume"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <eresource id="pmcj-{NlmUniqueID}" recordId="{NlmUniqueID}" type="pmcj" update="19690101000000">
            <title>
                <xsl:value-of select="TitleMain/Title" />
            </title>
            <xsl:for-each select="TitleAlternate/Title">
                <title_alt>
                    <xsl:value-of select="."/>
                </title_alt>
            </xsl:for-each>
            <xsl:for-each select="MeshHeadingList/MeshHeading/DescriptorName[@MajorTopicYN='Y']">
                <mesh>
                    <xsl:value-of select="."/>
                </mesh>
            </xsl:for-each>
            <!-- broad mesh missing from efetch; seems to be present in nlmcatalog view
                 https://www.ncbi.nlm.nih.gov/nlmcatalog/?term=101549567
            <xsl:for-each select="BroadJournalHeadingList/BroadJournalHeading">
                <mesh_broad>
                    <xsl:value-of select="."/>
                </mesh_broad>
            </xsl:for-each>
             -->
            <title_abbr>
                <xsl:value-of select="MedlineTA" />
            </title_abbr>
            <primaryType>Journal Digital</primaryType>
            <type>Journal</type>
            <year><xsl:value-of select="$year"/></year>
            <version>
                <link>
                    <url><xsl:value-of select="//@journalUrl"/></url>
                    <publisher>PubMed Central</publisher>
                    <label><xsl:value-of select="//@freeAccess"/></label>
                    <summary-holdings><xsl:value-of select="$summary-holdings"/></summary-holdings>
                </link>
            </version>
            <xsl:variable name="keywords">
                <xsl:apply-templates/>
            </xsl:variable>
            <keywords> <xsl:value-of select="$keywords"/> </keywords>
        </eresource>
    </xsl:template>

    <xsl:template match="text()" priority="1">
        <xsl:variable name="text" select="normalize-space()"/>
        <xsl:if test="string-length($text) &gt; 0">
            <xsl:value-of select="$text"/>
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="child::node()">
        <xsl:apply-templates select="attribute::node() | child::node()"/>
    </xsl:template>

</xsl:stylesheet>
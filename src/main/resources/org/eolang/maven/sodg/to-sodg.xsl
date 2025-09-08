<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="to-sodg" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <!--
  Here we convert all objects into SODG format.
  -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="/object/o" mode="sodg"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o" mode="sodg">
    <xsl:variable name="root" select="position()"/>
    <xsl:if test="eo:abstract(.)">
      <i>
        <xsl:attribute name="name">
          <xsl:value-of select="'formation'"/>
        </xsl:attribute>
        <a>
          <xsl:text>b</xsl:text>
          <xsl:value-of select="position()"/>
        </a>
        <xsl:for-each select="o">
          <a>
            <xsl:value-of select="@name"/>
          </a>
        </xsl:for-each>
      </i>
    </xsl:if>
    <xsl:for-each select="//o">
      <xsl:if test="not(eo:abstract(.)) and @base and @name and not(starts-with(@base, 'ξ.'))">
        <i>
          <xsl:attribute name="name">
            <xsl:value-of select="'dispatch'"/>
          </xsl:attribute>
          <a>
            <xsl:text>b</xsl:text>
            <xsl:value-of select="position()"/>
          </a>
          <a>
            <xsl:text>b</xsl:text>
            <xsl:value-of select="count(../preceding-sibling::*) + 1"/>
          </a>
          <a>
            <xsl:value-of select="@name"/>
          </a>
        </i>
      </xsl:if>
      <xsl:if test="not(eo:abstract(.)) and @base and @name and starts-with(@base, 'ξ.')">
        <i>
          <xsl:attribute name="name">
            <xsl:value-of select="'dispatch'"/>
          </xsl:attribute>
          <a>
            <xsl:text>b</xsl:text>
            <xsl:value-of select="position()"/>
          </a>
          <a>
            <xsl:text>b</xsl:text>
            <xsl:value-of select="count(preceding-sibling::*)"/>
          </a>
          <a>
            <!-- no information about instructions here yet -->
            <!-- should clean `ξ.qty.mul` to just `mul` -->
            <xsl:value-of select="preceding-sibling::o/@name"/>
          </a>
        </i>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>

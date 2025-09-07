<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="to-sodg" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <!--
  Here we convert all objects into SODG format.
  -->
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
      <xsl:apply-templates select="//o" mode="sodg"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o" mode="sodg">
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
            <xsl:value-of select="eo:escape(@name)"/>
          </a>
        </xsl:for-each>
      </i>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>

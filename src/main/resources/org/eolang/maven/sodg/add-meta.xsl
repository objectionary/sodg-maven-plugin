<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="add-meta" version="2.0">
  <!--
  Here we add instructions for meta information.
  -->
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:param name="name"/>
  <xsl:param name="value"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
      <xsl:variable name="v">
        <xsl:text>meta-</xsl:text>
        <xsl:value-of select="$name"/>
      </xsl:variable>
      <xsl:call-template name="i">
        <xsl:with-param name="name" select="'meta'"/>
        <xsl:with-param name="args" select="(eo:var($v), $value)"/>
      </xsl:call-template>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o" mode="sodg">
    <!-- ignore them -->
  </xsl:template>
  <xsl:template match="node()|@*" mode="#default">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*" mode="#current"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

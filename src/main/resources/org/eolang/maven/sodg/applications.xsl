<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="applications" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <!--
  Here we inject all application instructions.
  -->
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="i"/>
      <xsl:for-each select="//o[@base][not(@base='Φ.org.eolang.bytes')][exists(eo:arguments(.))]">
        <xsl:variable name="object" select="."/>
        <xsl:for-each select="eo:arguments(.)">
          <xsl:call-template name="i">
            <xsl:with-param name="name" select="'application'"/>
            <xsl:with-param name="args" select="(concat(eo:object($object), '-app', position()), if (position()=1) then eo:object($object) else concat(eo:object($object), '-app', position() - 1), if (@as) then string(@as) else concat('α', position() - 1), eo:result(.))"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:if test="@name">
          <xsl:call-template name="i">
            <xsl:with-param name="name" select="'put'"/>
            <xsl:with-param name="args" select="(eo:object(ancestor::o[not(@base)][1]), @name, eo:result(.))"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<!--
* SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
* SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="to-sodg" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <!--
  Here we convert all objects into SODG format with the following instructions:
  1. `formation(o, attr0, attr1, ...)`
  2. `dispatch(o, base, attr)`
  3. `delta(o, data)`
  4. `lambda(o, name)`
  -->
  <xsl:template match="/object/sodg">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="i"/>
      <!-- Reserve b0 for the global scope used by qualified base paths. -->
      <xsl:if test="//o[starts-with(@base, 'Φ.') and not(o[1]/@base='Φ.bytes') and not(@base='Φ.bytes')]">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'formation'"/>
          <xsl:with-param name="args" select="'b0'"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:apply-templates select="//o[not(@base='Φ.bytes') and not(eo:has-data(.))]" mode="sodg"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="o" mode="sodg">
    <xsl:variable name="oid" select="eo:object(.)"/>
    <xsl:variable name="owner" select="ancestor::o[not(@base)][1]"/>
    <xsl:choose>
      <xsl:when test="eo:abstract(.) and not(@name=$eo:lambda) or o[1]/@base='Φ.bytes' and (not(@name) or not($owner))">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'formation'"/>
          <xsl:with-param name="args" select="($oid, o/@name ! string())"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="@base='∅' or @base='ξ' or o[1]/@base='Φ.bytes'">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'dispatch'"/>
          <xsl:with-param name="args" select="($oid, eo:object($owner), @name)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="starts-with(@base, '.')">
        <xsl:call-template name="i">
          <xsl:with-param name="name" select="'dispatch'"/>
          <xsl:with-param name="args" select="($oid, eo:result(o[1]), substring-after(@base, '.'))"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="@base">
        <xsl:variable name="parts" select="tokenize(@base, '\.')"/>
        <xsl:variable name="base" select="if ($parts[1]='Φ') then 'b0' else eo:object($owner)"/>
        <xsl:for-each select="$parts[position() &gt; 1]">
          <xsl:call-template name="i">
            <xsl:with-param name="name" select="'dispatch'"/>
            <xsl:with-param name="args" select="(if (position()=last()) then $oid else concat($oid, '-base', position()), if (position()=1) then $base else concat($oid, '-base', position() - 1), .)"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>
    <xsl:if test="eo:atom(.)">
      <xsl:call-template name="i">
        <xsl:with-param name="name" select="'lambda'"/>
        <xsl:with-param name="args" select="($oid, @name)"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="o[1]/@base='Φ.bytes'">
      <xsl:call-template name="i">
        <xsl:with-param name="name" select="'delta'"/>
        <xsl:with-param name="args" select="($oid, o[1]/o[1]/text())"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>

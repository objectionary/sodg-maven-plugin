<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="drop-lambda" version="2.0">
  <!-- Drop all the lambda as arguments. -->
  <xsl:import href="/org/eolang/maven/sodg/_macros.xsl"/>
  <xsl:param name="sheet"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="a[normalize-space(.) = 'λ']"/>
</xsl:stylesheet>

<?xml version="1.0" encoding="utf-8"?>
<!--Stylesheet synthesized using the "Literate XSLT"(TM) environment.-->
<!--See http://www.CraneSoftwrights.com/links/res-lxslt.htm for available-->
<!--resources, training and training materials from Crane Softwrights Ltd.-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/XSL/Format" xmlns:po="urn:oasis:names:tc:ubl:Order:1.0:0.70" xmlns:cat="urn:oasis:names:tc:ubl:CommonAggregateTypes:1.0:0.70" version="1.0">



<!--

Copyright (C) - Crane Softwrights Ltd. 
              - http://www.CraneSoftwrights.com/links/res-ubls.htm

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

- Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer. 
- Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution. 
- The name of the author may not be used to endorse or promote products 
  derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR 
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Note: for your reference, the above is the "Modified BSD license", this text
      was obtained 2002-12-16 at http://www.xfree86.org/3.3.6/COPYRIGHT2.html#5

-->

<!--Override this with a non-empty string to trigger HTML-orientation-->
   <xsl:variable name="html-output"/>

<!--Override these A4 page dimensions with US-letter dimensions-->
   <xsl:variable name="long-page-edge" select="'297mm'"/>
   <xsl:variable name="short-page-edge" select="'210mm'"/>
   <xsl:template match="/">
      <root>
<!--check expected document element-->
         <xsl:if test="not(po:Order)">
            <xsl:message terminate="yes">
               <xsl:text>Unexpected instance for stylesheet:
{</xsl:text>
               <xsl:value-of select="namespace-uri(*)"/>
               <xsl:text>}</xsl:text>
               <xsl:value-of select="local-name(*)"/>
               <xsl:text>
Expecting:
{</xsl:text>
               <xsl:text>urn:oasis:names:tc:ubl:Order:1.0:0.70</xsl:text>
               <xsl:text>}Order</xsl:text>
            </xsl:message>
         </xsl:if>
<!--describe page geometries-->
         <layout-master-set>
            <simple-page-master master-name="onepage" page-width="{$short-page-edge}" page-height="{$long-page-edge}" margin-top="5mm" margin-left="10mm">
               <region-body region-name="body"/>
            </simple-page-master>
            <page-sequence-master master-name="layout">
               <single-page-master-reference master-reference="onepage"/>
            </page-sequence-master>
         </layout-master-set>
<!--begin each page every 20 lines-->
         <xsl:apply-templates select="po:Order/cat:OrderLine[ position() mod 20 = 1]"/>
      </root>
   </xsl:template>


<!--
Each page sequence contains a table of the form nested inside of a table
with the marginalia.

This is the table that defines the marginalia.
-->
   <xsl:template match="cat:OrderLine">
      <page-sequence master-reference="layout">
         <title>
            <xsl:value-of select="/po:Order/cat:ID"/>
            <xsl:text> - </xsl:text>
            <xsl:value-of select="/po:Order/cat:IssueDate"/>
            <xsl:text>- Purchase Order</xsl:text>
         </title>
         <flow flow-name="body" xsl:use-attribute-sets=" body-font">
            <table width="183mm + 10mm" table-layout="fixed">
               <table-column column-width="10mm"/>
               <table-column column-width="183mm div 2"/>
               <table-column column-width="183mm div 2"/>
               <table-body>
                  <table-row height="5mm">
                     <table-cell number-rows-spanned="2" display-align="after" height="262mm">
                        <block-container reference-orientation="90">
<!--meta data for this form in print-->
                           <block font-family="Arial" font-size="6pt" text-indent="2mm" space-after="2pt" space-after.conditionality="retain">
                              <xsl:if test="not($html-output)">
                                 <xsl:value-of select="translate( 'Order_Office.xsl $Revision: 1.1 $$Date: 2004/03/26 10:13:37 $','$','')"/>
                                 <basic-link external-destination="http://www.CraneSoftwrights.com/links/res-ublf.htm">
                                    <xsl:text>http://www.CraneSoftwrights.com/links/res-ublf.htm</xsl:text>
                                 </basic-link>
                              </xsl:if>
                           </block>
                        </block-container>
                     </table-cell>
                     <table-cell number-columns-spanned="2" display-align="after">
                        <block font-weight="bold" text-align-last="justify">
                           <xsl:text>
                OFFICE ORDER
                </xsl:text>
                           <leader/>
                           <xsl:text>Page </xsl:text>
                           <xsl:value-of select="count( preceding-sibling::cat:OrderLine ) div 20 + 1"/>
                           <xsl:text> of </xsl:text>
                           <xsl:value-of select="floor( ( count( ../cat:OrderLine ) - 1 ) div 20 ) + 1"/>
                        </block>
                     </table-cell>
                  </table-row>
                  <table-row height="262mm - 5mm">
                     <table-cell number-columns-spanned="2" number-rows-spanned="2">
<!--nest the form data in another complete table-->
                        <xsl:call-template name="do-form-data"/>
                     </table-cell>
                  </table-row>
                  <table-row>
                     <table-cell display-align="after">
                        <block>
                           <block text-align="end" font-weight="bold" font-family="Arial" font-size="7pt" end-indent="2pt">
                              <xsl:text>OFFICE</xsl:text>
                           </block>
                           <block text-align="end" font-weight="bold" font-family="Arial" font-size="7pt" end-indent="2pt">
                              <xsl:text>ORDER</xsl:text>
                           </block>
                        </block>
                     </table-cell>
                  </table-row>
               </table-body>
            </table>
<!--meta data for this form in HTML-->
            <block font-family="Arial" font-size="6pt" text-indent="8mm">
               <xsl:if test="$html-output">
                  <xsl:value-of select="translate( 'Order_Office.xsl $Revision: 1.1 $$Date: 2004/03/26 10:13:37 $','$','')"/>
                  <basic-link external-destination="http://www.CraneSoftwrights.com/links/res-ublf.htm">
                     <xsl:text>http://www.CraneSoftwrights.com/links/res-ublf.htm</xsl:text>
                  </basic-link>
               </xsl:if>
            </block>
         </flow>
      </page-sequence>
   </xsl:template>


<!--
The form table builds the form using a background grid of rows and 
columns, superimposed by table cells that span the background grid both
horizontally and vertically.  Each table cell is given a header and then
the field contents are created by applied templates in order to make them
available for being overridden by an importing stylesheet.
-->
   <xsl:template name="do-form-data">
      <table table-layout="fixed" width="100%">
         <table-column column-width="( 183mm div 16 )" number-columns-repeated="16"/>
         <xsl:apply-templates select="ancestor::po:Order[1]" mode="prelude"/>
         <xsl:call-template name="do-table-items"/>
         <xsl:apply-templates select="ancestor::po:Order[1]" mode="postlude"/>
      </table>
   </xsl:template>


<!--
The following rows of the table are oriented around the header and go before
the line items in the form.
-->
   <xsl:template match="po:Order" mode="prelude">
      <table-body>
         <table-row height="( 262mm div 32 ) * 1">
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-start-style="none">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block xsl:use-attribute-sets=" heading-font">
                        <xsl:copy-of select="$heading-orderno"/>
                     </block>
                     <xsl:apply-templates select="cat:ID" mode="order"/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-end-style="none" border-after-style="none">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
         </table-row>
         <table-row height="( 262mm div 32 ) * 1">
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-start-style="none" border-before-style="none">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-issuedate"/>
                     </block>
                     <xsl:apply-templates select="cat:IssueDate"/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-end-style="none" border-before-style="none">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
         </table-row>
         <table-row height="( 262mm div 32 ) * 3">
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-start-style="none">
               <block-container height="3 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt" overflow="error-if-overflow">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-buyer"/>
                     </block>
                     <xsl:apply-templates select="cat:BuyerParty"/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-end-style="none">
               <block-container height="3 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt" overflow="error-if-overflow">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-seller"/>
                     </block>
                     <xsl:apply-templates select="cat:SellerParty"/>
                  </wrapper>
               </block-container>
            </table-cell>
         </table-row>
         <table-row height="( 262mm div 32 ) * 1">
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-start-style="none">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-contact"/>
                     </block>
                     <xsl:apply-templates select="cat:BuyerParty/cat:BuyerContact/cat:Name"/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-end-style="none" border-after-style="none">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-ordercontact"/>
                     </block>
                     <xsl:apply-templates select="cat:OrderContact/cat:Name"/>
                  </wrapper>
               </block-container>
            </table-cell>
         </table-row>
         <table-row height="( 262mm div 32 ) * 3">
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-start-style="none">
               <block-container height="3 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-deliveryaddress"/>
                     </block>
                     <xsl:apply-templates select="(cat:OrderLine/cat:DeliveryRequirement/cat:DeliverToAddress)[1]"/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="8" border="solid 1pt" border-end-style="none">
               <block-container height="3 * ( 262mm div 32 ) -1pt" width="8 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-requesteddeliverydate"/>
                     </block>
                     <xsl:apply-templates select="(cat:OrderLine/cat:DeliveryRequirement/cat:DeliverySchedule/cat:RequestedDeliveryDate)[1]"/>
                  </wrapper>
               </block-container>
            </table-cell>
         </table-row>
         <table-row height="( 262mm div 32 ) * 1">
            <table-cell number-rows-spanned="1" number-columns-spanned="16" border="none 1pt">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="16 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
         </table-row>
         <table-row height="( 262mm div 32 ) * 1">
            <table-cell number-rows-spanned="1" number-columns-spanned="1" border="none 1pt" display-align="after">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="1 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-lineno"/>
                     </block>
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="2" border="none 1pt" display-align="after">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="2 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-partnumber"/>
                     </block>
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="5" border="none 1pt" display-align="after">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="5 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-description"/>
                     </block>
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="2" border="none 1pt" display-align="after">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="2 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-quantity"/>
                     </block>
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="3" border="none 1pt" display-align="after">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="3 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-unitprice"/>
                     </block>
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="3" border="none 1pt" display-align="after">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="3 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-amount"/>
                     </block>
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
         </table-row>
      </table-body>
   </xsl:template>

   <xsl:template match="cat:OrderLine" mode="do-items" name="do-all-items">
      <table-row height="( 262mm div 32 ) * 1">
         <table-cell number-rows-spanned="1" number-columns-spanned="1" border="solid 1pt" display-align="center">
            <block-container height="1 * ( 262mm div 32 ) -1pt" width="1 * ( 183mm div 16 )  -1pt">
               <wrapper start-indent="2pt" end-indent="2pt">
                  <xsl:apply-templates select="cat:BuyersID"/>
               </wrapper>
            </block-container>
         </table-cell>
         <table-cell number-rows-spanned="1" number-columns-spanned="2" border="solid 1pt" display-align="center">
            <block-container height="1 * ( 262mm div 32 ) -1pt" width="2 * ( 183mm div 16 )  -1pt">
               <wrapper start-indent="2pt" end-indent="2pt">
                  <xsl:apply-templates select="cat:Item/cat:SellersItemIdentification/cat:ID"/>
               </wrapper>
            </block-container>
         </table-cell>
         <table-cell number-rows-spanned="1" number-columns-spanned="5" border="solid 1pt" display-align="center">
            <block-container height="1 * ( 262mm div 32 ) -1pt" width="5 * ( 183mm div 16 )  -1pt">
               <wrapper start-indent="2pt" end-indent="2pt">
                  <xsl:apply-templates select="cat:Item/cat:Description"/>
               </wrapper>
            </block-container>
         </table-cell>
         <table-cell number-rows-spanned="1" number-columns-spanned="2" border="solid 1pt" display-align="center">
            <block-container height="1 * ( 262mm div 32 ) -1pt" width="2 * ( 183mm div 16 )  -1pt">
               <wrapper start-indent="2pt" end-indent="2pt">
                  <xsl:apply-templates select="cat:Quantity"/>
               </wrapper>
            </block-container>
         </table-cell>
         <table-cell number-rows-spanned="1" number-columns-spanned="3" border="solid 1pt" display-align="center">
            <wrapper start-indent="2pt" end-indent="2pt">
               <xsl:apply-templates select="cat:Item/cat:BasePrice/cat:PriceAmount"/>
            </wrapper>
         </table-cell>
         <table-cell number-rows-spanned="1" number-columns-spanned="3" border="solid 1pt" display-align="center">
            <wrapper start-indent="2pt" end-indent="2pt">
               <xsl:apply-templates select="cat:LineExtensionAmount"/>
            </wrapper>
         </table-cell>
      </table-row>
   </xsl:template>


<!--
The following rows of the table are oriented around the line items
-->
   <xsl:template name="do-table-items">
      <table-body>
         <xsl:apply-templates select="(self::cat:OrderLine|following-sibling::cat:OrderLine[position()&lt;20])" mode="do-items"/>
      </table-body>
   </xsl:template>


<!--
The following rows of the table are oriented around the summary and go after
the line items in the form.
-->
   <xsl:template match="po:Order" mode="postlude">
      <table-body>
         <table-row height="( 262mm div 32 ) * 1">
            <table-cell number-rows-spanned="1" number-columns-spanned="11" border="none 1pt" display-align="center">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="11 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block/>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="2" border="none 1pt">
               <block-container height="1 * ( 262mm div 32 ) -1pt" width="2 * ( 183mm div 16 )  -1pt">
                  <wrapper start-indent="2pt" end-indent="2pt">
                     <block font-family="Arial" font-size="6pt" space-after="1pt" text-align="end" xsl:use-attribute-sets="heading-font ">
                        <xsl:copy-of select="$heading-total"/>
                     </block>
                  </wrapper>
               </block-container>
            </table-cell>
            <table-cell number-rows-spanned="1" number-columns-spanned="3" border="solid 1pt" display-align="center">
               <wrapper start-indent="2pt" end-indent="2pt">
                  <xsl:apply-templates select="cat:LineExtensionTotalAmount"/>
               </wrapper>
            </table-cell>
         </table-row>
      </table-body>
   </xsl:template>

   <xsl:template match="cat:BuyerParty/cat:BuyerContact/cat:Name">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:BuyersID">
      <block text-align="center">
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:CityName">
      <inline>
         <xsl:apply-templates/>
      </inline>
   </xsl:template>

   <xsl:template match="cat:Country/cat:Code">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:CountrySub-Entity">
      <inline>
         <xsl:text>, </xsl:text>
         <inline>
            <xsl:apply-templates/>
         </inline>
      </inline>
   </xsl:template>

   <xsl:template match="cat:DeliverToAddress">
      <block>
         <xsl:call-template name="address"/>
      </block>
   </xsl:template>

   <xsl:template match="cat:ID" mode="order">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:IssueDate">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:Item/cat:BasePrice/cat:PriceAmount">
      <block text-align-last="justify" text-align="end">
         <inline>
            <xsl:apply-templates select="@currencyID"/>
         </inline>
         <leader/>
         <xsl:value-of select="format-number( ., '#0.00' )"/>
      </block>
   </xsl:template>

   <xsl:template match="cat:Item/cat:Description">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:Item/cat:SellersItemIdentification/cat:ID">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:LineExtensionAmount">
      <block text-align-last="justify" text-align="end">
         <inline>
            <xsl:apply-templates select="@currencyID"/>
         </inline>
         <leader/>
         <xsl:value-of select="format-number( ., '#0.00' )"/>
      </block>
   </xsl:template>

   <xsl:template match="cat:LineExtensionTotalAmount">
      <block text-align-last="justify" text-align="end">
         <inline>
            <xsl:apply-templates select="@currencyID"/>
         </inline>
         <leader/>
         <xsl:value-of select="format-number( ., '#0.00' )"/>
      </block>
   </xsl:template>

   <xsl:template match="cat:OrderContact/cat:Name">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:PartyName/cat:Name">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:PostalZone">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:Quantity">
      <block text-align="end">
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:RequestedDeliveryDate">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:SellerParty">
      <block>
         <xsl:call-template name="name-address"/>
      </block>
   </xsl:template>

   <xsl:template match="cat:Street">
      <block>
         <xsl:apply-templates/>
      </block>
   </xsl:template>

   <xsl:template match="cat:Address" name="address">
      <block>
         <xsl:apply-templates select="cat:Street"/>
         <block>
            <xsl:apply-templates select="cat:CityName"/>
            <xsl:apply-templates select="cat:CountrySub-Entity"/>
         </block>
         <xsl:apply-templates select="cat:Country/cat:Code"/>
         <xsl:apply-templates select="cat:PostalZone"/>
      </block>
   </xsl:template>

   <xsl:template match="cat:BuyerParty" name="name-address">
      <block>
         <xsl:apply-templates select="cat:PartyName/cat:Name"/>
         <xsl:apply-templates select="cat:Address"/>
      </block>
   </xsl:template>

<!--default font used for body text of the form-->

   <xsl:attribute-set name="body-font">
      <xsl:attribute name="font-family">Times</xsl:attribute>
      <xsl:attribute name="font-size">10pt</xsl:attribute>
      <xsl:attribute name="line-height">1.1</xsl:attribute>
   </xsl:attribute-set>

<!--all field headings-->

   <xsl:attribute-set name="heading-font">
      <xsl:attribute name="font-family">Arial</xsl:attribute>
      <xsl:attribute name="font-size">6pt</xsl:attribute>
      <xsl:attribute name="space-after">1pt</xsl:attribute>
   </xsl:attribute-set>

<!--top-level variables-->

   <xsl:variable name="heading-amount">
      <xsl:text>Amount</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-buyer">
      <xsl:text>Buyer</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-contact">
      <xsl:text>Contact</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-deliveryaddress">
      <xsl:text>Delivery address:</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-description">
      <xsl:text>Description</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-issuedate">
      <xsl:text>Issue Date</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-lineno">
      <xsl:text>Line no.</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-ordercontact">
      <xsl:text>Order Contact</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-orderno">
      <xsl:text>Order No.</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-partnumber">
      <xsl:text>Part Number</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-quantity">
      <xsl:text>Quantity</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-requesteddeliverydate">
      <xsl:text>Requested Delivery Date</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-seller">
      <xsl:text>Seller</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-total">
      <xsl:text>Total</xsl:text>
   </xsl:variable>
   <xsl:variable name="heading-unitprice">
      <xsl:text>Unit Price</xsl:text>
   </xsl:variable>
</xsl:stylesheet>
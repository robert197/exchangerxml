/*
 * $Id: EditorProperties.java,v 1.7 2004/10/19 14:39:44 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.util.Vector;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertiesFile;
import com.cladonia.xml.properties.PropertyList;

/**
 * Handles the properties for the Text Editor.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/10/19 14:39:44 $
 * @author Dogsbay
 */
public class EditorProperties extends PropertiesFile {
	public static final String TEXT_EDITOR		= "text-editor";
	
	private static final int MAX_SEARCHES = 10;
	private static final int DEFAULT_SPACES = 4;

	private static final String SEARCH_MATCH_CASE		= "search-match-case";
	private static final String SEARCH_DIRECTION_DOWN	= "search-direction-down";

	private static final String WRAP_WHILE_TYPING	= "wrap-while-typing";
	private static final String WRAP_TEXT			= "wrap-text";
	private static final String SOFT_WRAPPING		= "soft-wrapping";
	private static final String WRAPPING_COLUMN		= "wrapping-column";

	private static final String FORMAT_TYPE	= "format-type";
	public static final int FORMAT_CUSTOM	= 0;
	public static final int FORMAT_COMPACT	= 1;
	public static final int FORMAT_STANDARD	= 2;

	private static final String FORMAT_CUSTOM_PAD						= "format-custom-pad";
	private static final String FORMAT_CUSTOM_NEWLINE					= "format-custom-newline";
	private static final String FORMAT_CUSTOM_STRIP						= "format-custom-strip";
	private static final String FORMAT_CUSTOM_PRESERVE_MIXED_CONTENT	= "format-custom-preserve-mixed-content";
	private static final String FORMAT_CUSTOM_INDENT					= "format-custom-indent";

	private static final String SPACES 					= "spaces";
	private static final String SEARCH 					= "search";
	private static final String TAG_COMPLETION			= "tag-completion";
	private static final String SHOW_MARGIN				= "show-margin";
	private static final String SHOW_FOLDING_MARGIN		= "show-folding-margin";
	private static final String SHOW_OVERVIEW_MARGIN	= "show-overview-margin";
	private static final String SHOW_ANNOTATION_MARGIN	= "show-annotation-margin";
	private static final String SHOW_FRAGMENTS_TOOLBAR	= "show-fragments-toolbar";
	private static final String SMART_INDENTATION		= "smart-indentation";
	private static final String TEXT_PROMPTING			= "text-prompting";
	private static final String ERROR_HIGHLIGHTING		= "error-highlighting";
//	private static final String STRICT_TEXT_PROMPTING	= "strict_text-prompting";

	private static final String CONVERT_GT_TO_ENTITY		= "convert-gt-to-entity";
	private static final String CONVERT_GT_TO_CHARACTER		= "convert-gt-to-character";

	private static final String CONVERT_LT_TO_ENTITY		= "convert-lt-to-entity";
	private static final String CONVERT_LT_TO_CHARACTER		= "convert-lt-to-character";

	private static final String CONVERT_AMP_TO_ENTITY		= "convert-amp-to-entity";
	private static final String CONVERT_AMP_TO_CHARACTER	= "convert-amp-to-character";

	private static final String CONVERT_APOS_TO_ENTITY		= "convert-apos-to-entity";
	private static final String CONVERT_APOS_TO_CHARACTER	= "convert-apos-to-character";

	private static final String CONVERT_QUOT_TO_ENTITY		= "convert-quot-to-entity";
	private static final String CONVERT_QUOT_TO_CHARACTER	= "convert-quot-to-character";

	private static final String CONVERT_CHARACTER_TO_NAMED_ENTITY	= "convert-character-to-named-entity";
	private static final String CONVERT_NAMED_ENTITY_TO_CHARACTER	= "convert-named-entity-to-character";

	private static final String CONVERT_CHARACTER_ENTITY	= "convert-character-entity";

	private static final String ESCAPE_UNICODE_CHARACTERS	= "escape-unicode-characters";
	private static final String UNICODE_START_ENTRY			= "unicode-start-entry";

	private PropertyList searches = null;

	/**
	 * Constructor for the editor properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the editor.
	 */
	public EditorProperties(String fileName, String rootName) {
		super(loadPropertiesFile(fileName, rootName));
		
		searches = getList( SEARCH, MAX_SEARCHES);
	}

	/**
	 * Check to find out if the search matches the case.
	 *
	 * @return true when the search matches case.
	 */
	public boolean isMatchCase() {
		return getBoolean( SEARCH_MATCH_CASE, false);
	}

	/**
	 * Set the match-case search property.
	 *
	 * @param matchCase the search property.
	 */
	public void setMatchCase( boolean matchCase) {
		set( SEARCH_MATCH_CASE, matchCase);
	}

//	/**
//	 * While typing the text should wrap.
//	 *
//	 * @return true when text wrapping while typing.
//	 */
//	public boolean isWrapWhileTyping() {
//		return getBoolean( WRAP_WHILE_TYPING, false);
//	}

//	 /**
//	  * While typing the text should wrap.
//	  *
//	  * @param wrap true when text should wrap while typing.
//	 */
//	public void setWrapWhileTyping( boolean wrap) {
//		set( WRAP_WHILE_TYPING, wrap);
//	}

	/**
	 * The editor should wrap the text.
	 *
	 * @return true when soft wrapping is required.
	 */
	public boolean isSoftWrapping() {
		return getBoolean( SOFT_WRAPPING, false);
	}

	 /**
	  * The editor should wrap the text.
	  *
	  * @param wrap true when soft wrapping is required.
	 */
	public void setSoftWrapping( boolean wrap) {
		set( SOFT_WRAPPING, wrap);
	}

	/**
	 * Formatting should wrap the text.
	 *
	 * @return true when text wrapping is required.
	 */
	public boolean isWrapText() {
		return getBoolean( WRAP_TEXT, false);
	}

	 /**
	  * Formatting should wrap the text.
	  *
	  * @param wrap true when text wrapping is required.
	 */
	public void setWrapText( boolean wrap) {
		set( WRAP_TEXT, wrap);
	}

	/**
	 * Get the wrapping column.
	 *
	 * @return the wrapping column.
	 */
	public int getWrappingColumn() {
		return getInteger( WRAPPING_COLUMN, 80);
	}

	 /**
	  * Set the wrapping column.
	  *
	  * @param column the wrapping column.
	 */
	public void setWrappingColumn( int column) {
		set( WRAPPING_COLUMN, column);
	}

	/**
	 * Get the formatting type.
	 *
	 * @return the formatting type.
	 */
	public int getFormatType() {
		return getInteger( FORMAT_TYPE, FORMAT_STANDARD);
	}

	 /**
	  * Get the formatting type.
	  *
	  * @param type the formatting type.
	 */
	public void setFormatType( int type) {
		set( FORMAT_TYPE, type);
	}

	/**
	 * When custom formatting type, check to see if padding should be available.
	 *
	 * @return true text should be padded.
	 */
	public boolean isCustomPadText() {
		return getBoolean( FORMAT_CUSTOM_PAD, false);
	}

	 /**
	  * Set wether text should be padded for a custom formatting type.
	  *
	  * @param enable true when text should be padded .
	 */
	public void setCustomPadText( boolean enable) {
		set( FORMAT_CUSTOM_PAD, enable);
	}

	/**
	 * When custom formatting type, check to see if tags should be indented.
	 *
	 * @return true tags should be indented.
	 */
	public boolean isCustomIndent() {
		return getBoolean( FORMAT_CUSTOM_INDENT, false);
	}

	 /**
	  * Set wether tags should be indented for a custom formatting type.
	  *
	  * @param enable true when tags should be indented.
	 */
	public void setCustomIndent( boolean enable) {
		set( FORMAT_CUSTOM_INDENT, enable);
	}

	/**
	 * When custom formatting type, check to see if mixed content should be preserved.
	 *
	 * @return wether mixed content should be preserved.
	 */
	public boolean isCustomPreserveMixedContent() {
		return getBoolean( FORMAT_CUSTOM_PRESERVE_MIXED_CONTENT, false);
	}

	 /**
	  * Set wether mixed content should be preserved for a custom formatting type.
	  *
	  * @param enable true when mixed content should be preserved.
	 */
	public void setCustomPreserveMixedContent( boolean enable) {
		set( FORMAT_CUSTOM_PRESERVE_MIXED_CONTENT, enable);
	}

	/**
	 * When custom formatting type, check to see if whitespace should be stripped.
	 *
	 * @return true whitespace should be stripped.
	 */
	public boolean isCustomStrip() {
		return getBoolean( FORMAT_CUSTOM_STRIP, false);
	}

	 /**
	  * Set wether text should be stripped for a custom formatting type.
	  *
	  * @param enable true when text should be stripped.
	 */
	public void setCustomStrip( boolean enable) {
		set( FORMAT_CUSTOM_STRIP, enable);
	}

	/**
	 * When custom formatting type, check to see if newlines should be added.
	 *
	 * @return true newlines should be added.
	 */
	public boolean isCustomNewline() {
		return getBoolean( FORMAT_CUSTOM_NEWLINE, false);
	}

	 /**
	  * Set wether newlines should be added.
	  *
	  * @param enable true when newlines should be added.
	 */
	public void setCustomNewline( boolean enable) {
		set( FORMAT_CUSTOM_NEWLINE, enable);
	}

	/**
	 * Check to find out if the tag completion is enabled.
	 *
	 * @return true when the tag completion is enabled.
	 */
	public boolean isTagCompletion() {
		return getBoolean( TAG_COMPLETION, true);
	}

	/**
	 * Set the tag completion property.
	 *
	 * @param complete the tag completion property.
	 */
	public void setTagCompletion( boolean complete) {
		set( TAG_COMPLETION, complete);
	}

	/**
	 * Check to find out if the margin should be visible.
	 *
	 * @return true when the margin is visible.
	 */
	public boolean isShowMargin() {
		return getBoolean( SHOW_MARGIN, true);
	}

	/**
	 * Set the show margin completion property.
	 *
	 * @param show the show margin property.
	 */
	public void setShowMargin( boolean show) {
		set( SHOW_MARGIN, show);
	}

	/**
	 * Check to find out if the overview margin should be visible.
	 *
	 * @return true when the overview margin is visible.
	 */
	public boolean isShowOverviewMargin() {
		return getBoolean( SHOW_OVERVIEW_MARGIN, true);
	}

	/**
	 * Set the show overview margin completion property.
	 *
	 * @param show the show overview margin property.
	 */
	public void setShowOverviewMargin( boolean show) {
		set( SHOW_OVERVIEW_MARGIN, show);
	}

	/**
	 * Check to find out if the folding margin should be visible.
	 *
	 * @return true when the folding margin is visible.
	 */
	public boolean isShowFoldingMargin() {
		return getBoolean( SHOW_FOLDING_MARGIN, true);
	}

	/**
	 * Set the show folding margin completion property.
	 *
	 * @param show the show folding margin property.
	 */
	public void setShowFoldingMargin( boolean show) {
		set( SHOW_FOLDING_MARGIN, show);
	}

	/**
	 * Check to find out if the bookmark margin should be visible.
	 *
	 * @return true when the bookmark margin is visible.
	 */
	public boolean isShowAnnotationMargin() {
		return getBoolean( SHOW_ANNOTATION_MARGIN, true);
	}

	/**
	 * Set the show bookmark margin completion property.
	 *
	 * @param show the show bookmark margin property.
	 */
	public void setShowAnnotationMargin( boolean show) {
		set( SHOW_ANNOTATION_MARGIN, show);
	}

	/**
	 * Check to find out if the fragments toolbar should be visible.
	 *
	 * @return true when the fragments toolbar is visible.
	 */
	public boolean isShowFragmentsToolbar() {
		return getBoolean( SHOW_FRAGMENTS_TOOLBAR, true);
	}

	/**
	 * Set the show fragments toolbar property.
	 *
	 * @param show the show fragments toolbar property.
	 */
	public void setShowFragmentsToolbar( boolean show) {
		set( SHOW_FRAGMENTS_TOOLBAR, show);
	}

	/**
	 * Check to find out if smart indentation should be used.
	 *
	 * @return true when smart indentation should be used.
	 */
	public boolean isSmartIndentation() {
		return getBoolean( SMART_INDENTATION, true);
	}

	/**
	 * Set the smart indentation property.
	 *
	 * @param smart the smart indentation property.
	 */
	public void setSmartIndentation( boolean smart) {
		set( SMART_INDENTATION, smart);
	}

	/**
	 * Check to find out if text prompting should be used.
	 *
	 * @return true when text prompting should be used.
	 */
	public boolean isTextPrompting() {
		return getBoolean( TEXT_PROMPTING, true);
	}

	/**
	 * Set the text prompting property.
	 *
	 * @param prompt the text prompting property.
	 */
	public void setTextPrompting( boolean prompt) {
		set( TEXT_PROMPTING, prompt);
	}

	/**
	 * Check to find out if error highlighting should be used.
	 *
	 * @return true when error highlighting should be used.
	 */
	public boolean isErrorHighlighting() {
		return getBoolean( ERROR_HIGHLIGHTING, true);
	}

	/**
	 * Set the error highlighting property.
	 *
	 * @param highlight the error highlighting property.
	 */
	public void setErrorHighlighting( boolean highlight) {
		set( ERROR_HIGHLIGHTING, highlight);
	}

	/**
	 * Check to find out if text prompting should be strict.
	 *
	 * @return true when text prompting should be strict.
	 */
//	public boolean isStrictTextPrompting() {
//		return getBoolean( STRICT_TEXT_PROMPTING, true);
//	}

	/**
	 * Set the strict text prompting property.
	 *
	 * @param strict the strict text prompting property.
	 */
//	public void setStrictTextPrompting( boolean strict) {
//		set( STRICT_TEXT_PROMPTING, strict);
//	}

	/**
	 * Check to find out if the search direction is down.
	 *
	 * @return true when the search direction is down.
	 */
	public boolean isDirectionDown() {
		return getBoolean( SEARCH_DIRECTION_DOWN, true);
	}

	/**
	 * Set the search direction.
	 *
	 * @param downward the search direction.
	 */
	public void setDirectionDown( boolean downward) {
		set( SEARCH_DIRECTION_DOWN, downward);
	}

	/**
	 * Adds a Search string to the Editor.
	 *
	 * @param search the search.
	 */
	public void addSearch( String search) {
		searches.add( search);
	}

	/**
	 * Returns the list of searches.
	 *
	 * @return the list of searches.
	 */
	public Vector getSearches() {
		return searches.get();
	}
	
	public void setConvertGtToEntity( boolean convert) {
		set( CONVERT_GT_TO_ENTITY, convert);
	}

	public boolean isConvertGtToEntity() {
		return getBoolean( CONVERT_GT_TO_ENTITY, false);
	}

	public void setConvertLtToEntity( boolean convert) {
		set( CONVERT_LT_TO_ENTITY, convert);
	}

	public boolean isConvertLtToEntity() {
		return getBoolean( CONVERT_LT_TO_ENTITY, true);
	}

	public void setConvertAmpToEntity( boolean convert) {
		set( CONVERT_AMP_TO_ENTITY, convert);
	}

	public boolean isConvertAmpToEntity() {
		return getBoolean( CONVERT_AMP_TO_ENTITY, false);
	}

	public void setConvertAposToEntity( boolean convert) {
		set( CONVERT_APOS_TO_ENTITY, convert);
	}

	public boolean isConvertAposToEntity() {
		return getBoolean( CONVERT_APOS_TO_ENTITY, false);
	}

	public void setConvertQuotToEntity( boolean convert) {
		set( CONVERT_QUOT_TO_ENTITY, convert);
	}

	public boolean isConvertQuotToEntity() {
		return getBoolean( CONVERT_QUOT_TO_ENTITY, false);
	}
	
	
	public void setConvertGtToCharacter( boolean convert) {
		set( CONVERT_GT_TO_CHARACTER, convert);
	}

	public boolean isConvertGtToCharacter() {
		return getBoolean( CONVERT_GT_TO_CHARACTER, false);
	}

	public void setConvertLtToCharacter( boolean convert) {
		set( CONVERT_LT_TO_CHARACTER, convert);
	}

	public boolean isConvertLtToCharacter() {
		return getBoolean( CONVERT_LT_TO_CHARACTER, false);
	}

	public void setConvertAmpToCharacter( boolean convert) {
		set( CONVERT_AMP_TO_CHARACTER, convert);
	}

	public boolean isConvertAmpToCharacter() {
		return getBoolean( CONVERT_AMP_TO_CHARACTER, false);
	}

	public void setConvertAposToCharacter( boolean convert) {
		set( CONVERT_APOS_TO_CHARACTER, convert);
	}

	public boolean isConvertAposToCharacter() {
		return getBoolean( CONVERT_APOS_TO_CHARACTER, false);
	}

	public void setConvertQuotToCharacter( boolean convert) {
		set( CONVERT_QUOT_TO_CHARACTER, convert);
	}

	public boolean isConvertQuotToCharacter() {
		return getBoolean( CONVERT_QUOT_TO_CHARACTER, false);
	}

	public void setConvertNamedEntityToCharacter( boolean convert) {
		set( CONVERT_NAMED_ENTITY_TO_CHARACTER, convert);
	}

	public boolean isConvertNamedEntityToCharacter() {
		return getBoolean( CONVERT_NAMED_ENTITY_TO_CHARACTER, false);
	}

	public void setConvertCharacterToNamedEntity( boolean convert) {
		set( CONVERT_CHARACTER_TO_NAMED_ENTITY, convert);
	}

	public boolean isConvertCharacterToNamedEntity() {
		return getBoolean( CONVERT_CHARACTER_TO_NAMED_ENTITY, false);
	}

	public void setEscapeUnicodeCharacters( boolean escape) {
		set( ESCAPE_UNICODE_CHARACTERS, escape);
	}

	public boolean isEscapeUnicodeCharacters() {
		return getBoolean( ESCAPE_UNICODE_CHARACTERS, false);
	}
	
	public void setConvertCharacterEntity( boolean convert) {
		set( CONVERT_CHARACTER_ENTITY, convert);
	}

	public boolean isConvertCharacterEntity() {
		return getBoolean( CONVERT_CHARACTER_ENTITY, false);
	}

	public void setUnicodeStartEntry( int start) {
		set( UNICODE_START_ENTRY, start);
	}

	public int getUnicodeStartEntry() {
		return getInteger( UNICODE_START_ENTRY, 255);
	}
} 

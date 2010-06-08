/*
 * Id: 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.cladonia.xml.editor.XmlEditorPane;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.ComponentOrientation;

/**
 * @author Thomas Curley
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ImportUtilities {
    
    public static String[] xmlTypes = {"<ELEMENT>", "=\"ATTRIBUTE\"", "<!--SKIP-->"};
    public static final int NUMBER_OF_BAD_CHARS = 8;
    public static String[] badChars = {"<", ">", "=", ",", "\"", "&","[","]"};
    public static String[] goodChars = { "","", "", "", "", "","",""};
    public static String[] goodCharsNoConvert = { "&","<", ">", "=", ",", "\"","[","]"};
    //public static String[] goodCharsConvert = { "&amp;","&lt;", "&gt;", "&equals;", "&comma;", "&quot;","&lsqb;","&rsqb"};
    public static String[] splitExpressionsText = {"Comma","Semicolon","Tab","Space"};
    public static String[] splitExpressions = {",", ";", "\t", " "};
    
    static int mainWidth = 500;
    static int mainHeight = 500;
    
    
    public static void removeBadChars(JTable table,boolean skipHeaderBoolean) throws Exception {
        //	for each cell in row, remove bad chars
        int columns = table.getColumnCount();
        int rows = table.getRowCount();
        int startRow = 0;
        
        cleanHeaderCells(table);
        startRow=1;
        
        for (int rCnt = startRow; rCnt < rows; ++rCnt) {
            //		for each row
            for (int cCnt = 0; cCnt < columns; ++cCnt) {
                //			for each column in a row
                if(table.getValueAt(rCnt,cCnt)==null) {
//                  set this cell to ""
                    table.setValueAt("",rCnt,cCnt);
                }
                else {
                    String cellValue = (String) table.getValueAt(rCnt, cCnt);
                    
                    if(cellValue.length()>0) {
                        
                        String newCellValue = removeBadCharsFromString(cellValue,skipHeaderBoolean); 
                        if(!cellValue.equals(newCellValue)) {
                            
                            table.setValueAt(newCellValue, rCnt, cCnt);
                        }
                    }
                    else {
                        //set this cell to ""
                        table.setValueAt("",rCnt,cCnt);
                    }
                }
                
            }
        }
    }
    
    private static void cleanHeaderCells(JTable table) {
        
        for (int cCnt = 0; cCnt < table.getColumnCount(); ++cCnt) {
            //			for each column in a row
            if(table.getValueAt(0,cCnt)==null) {
//              set this cell to ""
                table.setValueAt("",0,cCnt);
            }
            else {
                String cellValue = (String) table.getValueAt(0, cCnt);
                
                if(cellValue.length()>0) {
                    //System.out.println(cellValue);
                    
                    for (int cnt = 0; cnt < badChars.length; ++cnt) {
                        //check if cellValue contains any of the bad chars
                        int indexValue = -1;
                        try {
                            indexValue = cellValue.indexOf(badChars[cnt]);
                            if (cellValue.indexOf(badChars[cnt]) != -1) {
                                //change char
                                //System.out.println(badChars[cnt]+":"+goodChars[cnt]);
                                cellValue = cellValue.replaceAll("\\"+badChars[cnt],goodChars[cnt]);
                                table.setValueAt(cellValue,0,cCnt);
                            }
                        } catch (NullPointerException e) {
                            System.out.println("null");
                        }
                    }
                }
            }
        }
        
    }
    
    private static String removeBadCharsFromString(String cellValue, boolean skipBoolean) throws Exception {
        
        /*for (int cnt = 0; cnt < badChars.length; ++cnt) {
         //check if cellValue contains any of the bad chars
          int indexValue = -1;
          try {
          indexValue = cellValue.indexOf(badChars[cnt]);
          if (cellValue.indexOf(badChars[cnt]) != -1) {
          //change char
           //System.out.println(badChars[cnt]+":"+goodChars[cnt]);
            cellValue = cellValue.replaceAll("\\"+badChars[cnt],goodChars[cnt]);
            }
            } catch (NullPointerException e) {
            System.out.println("null");
            }
            }*/
        
        return(substituteSelectedCharacters(cellValue,skipBoolean));
    }
    
    public static Vector removeBadChars(Vector data, boolean skipHeaderBoolean) throws Exception {
        
        //	for each cell in row, remove bad chars
        int rows = data.size();
        int columns = ((String[])data.get(0)).length;
        
        for (int rCnt = 0; rCnt < rows; ++rCnt) {
            //		for each row
            
            boolean rowWasChanged = false;
            String[] rowData = (String[])data.get(rCnt);
            for (int cCnt = 0; cCnt < columns; ++cCnt) {
                //			for each column in a row
                String cellValue = rowData[cCnt];
                if(cellValue.length()>0) {
                    String newCellValue = removeBadCharsFromString(cellValue,skipHeaderBoolean);
                    
                    if(!cellValue.equals(newCellValue)) {
                        rowWasChanged=true;
                        rowData[cCnt] = newCellValue;
                    }
                }
                else {
                    //set this cell to ""
                    rowWasChanged=true;
                    rowData[cCnt] = "";
                }
                
                
            }
            if(rowWasChanged) {
                data.set(rCnt,rowData);
            }
        }
        
        
        return(data);
    }
    
    public static void addNewRow(int index, JTable table, DefaultTableModel tableModel) throws Exception {
        String[] row = new String[table.getColumnCount()];
        for (int cnt = 0; cnt < table.getColumnCount(); ++cnt) {
            row[cnt] = "Heading" + cnt;
        }
        tableModel.insertRow(index, row);
    }
    
    public static DefaultTableModel addRowsToTable(Vector rows, int colCount) throws Exception{
        int rowCount = rows.size();
        //int rowsFromTM = tableModel.getRowCount();
        DefaultTableModel tableModel = new DefaultTableModel();
        //Vector headerValues = new Vector();
        
        /*for (int cnt = 0; cnt < rowsFromTM; ++cnt) {
         tableModel.removeRow(0);
         }*/
        tableModel.setColumnCount(colCount);
        for (int cnt = 0; cnt < rowCount; ++cnt) {
            String[] rowData = (String[]) rows.get(cnt);
            tableModel.addRow(rowData);
            
        }
        
        return(tableModel);
    }
    
    public static void updateOrder(XmlEditorPane xmlEditor, JTable table,
            String docField, String rowField, boolean convertCharsToEntites) throws Exception {
        
        //		String rootElement = gui.setDocText.getText();
        String rootElement = docField;
        String elementName = rowField;
        //		String elementName = gui.setElementText.getText();
        StringBuffer toWrite = new StringBuffer("");
        toWrite.append("<?xml version=\"1.0\" encoding=\""
                +  "UTF-8" + "\"?>\n");
        toWrite.append("<" + rootElement + ">");
        for (int oCnt = 1; oCnt < 2; ++oCnt) {
            //			add root element
            toWrite.append("\n<" + elementName);
            for (int cCnt = 0; cCnt < table.getColumnCount(); ++cCnt) {
                TableColumn col = table.getColumnModel().getColumn(cCnt);
                String type = (String)col.getHeaderValue(); 
                //String type = (String) headerValues.get(cCnt);
                if (type.equalsIgnoreCase(xmlTypes[1])) {
                    String titleValue = (String) table.getValueAt(0, cCnt);
                    if(titleValue.length()>0) {
                        toWrite.append(" " + titleValue + "=\"");
                        try {
                            toWrite.append(table.getValueAt(oCnt, cCnt));
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                            //there is no second row so no values
                        }
                        toWrite.append("\"");
                        
                    }
                }
            }
            toWrite.append(">\n");
            
            for (int cCnt = 0; cCnt < table.getColumnCount(); ++cCnt) {
                TableColumn col = table.getColumnModel().getColumn(cCnt);
                String type = (String)col.getHeaderValue(); 
                //String type = (String) headerValues.get(cCnt);
                if (type.equalsIgnoreCase(xmlTypes[0])) {
                    String titleValue = (String) table.getValueAt(0, cCnt);
                    if(titleValue.length()>0) {
                        toWrite.append("\t<" + titleValue + ">");
                        
                        try {
                            toWrite.append(table.getValueAt(oCnt, cCnt));
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                            //there is no second row so no values
                        }
                        toWrite.append("</" + titleValue + ">\n");
                    }
                }
            }
            
            toWrite.append("</" + elementName + ">");
            
        }
        toWrite.append("\n</" + rootElement + ">\n");
        xmlEditor.setText(toWrite.toString());
        xmlEditor.setCaretPosition( 0);
        
    }
    
    public static void removeSpacesFromHeadings(JTable table) throws Exception{
        //	heading row is always 1
        int columns = table.getColumnCount();
        //	System.out.println(columns);
        for (int cCnt = 0; cCnt < columns; ++cCnt) {
            //		for each column in a row
            String headingCellValue = (String) table.getValueAt(0, cCnt);
            if(headingCellValue.length()>0) {
                for (int cnt = 0; cnt < ImportUtilities.badChars.length; ++cnt) {
                    //			check if cellValue contains any of the bad chars
                    if (headingCellValue.indexOf(" ") != -1) {
                        //				change char
                        headingCellValue = headingCellValue.replaceAll(" ", "_");
                        table.setValueAt(headingCellValue, 0, cCnt);
                        //System.out.println(cCnt + ": "+cellValue);
                    }
                    if (headingCellValue.indexOf("(") != -1) {
                        //				change char
                        headingCellValue = headingCellValue.replaceAll("\\(","");
                        table.setValueAt(headingCellValue, 0, cCnt);
                        //System.out.println(cCnt + ": "+cellValue);
                    }
                    if (headingCellValue.indexOf(")") != -1) {
                        //				change char
                        headingCellValue = headingCellValue.replaceAll("\\)","");
                        table.setValueAt(headingCellValue, 0, cCnt);
                        //System.out.println(cCnt + ": "+cellValue);
                    }
                }
                //get the value again and check the first char for a number
                headingCellValue = (String) table.getValueAt(0, cCnt);
                char firstChar = headingCellValue.charAt(0);
                if(Character.isDigit(firstChar)) {
                    //error, insert "_" before digit
                    String underScore = "_";
                    underScore += headingCellValue;
                    table.setValueAt(underScore,0,cCnt);
                }
            }
        }
    }
    
    public static DefaultTableModel splitTextFile(File file, String encoding, int delimiterIndex, boolean convertCharsToEntites) throws 
    java.io.UnsupportedEncodingException,FileNotFoundException,IOException, Exception {
        
        InputStream in = new FileInputStream(file);
        InputStreamReader inReader = new InputStreamReader(in, encoding);
        BufferedReader reader = new BufferedReader(inReader);
        String line;
        boolean first = true;
        int commaCount = 0;
        int rowCount = 0;
        Vector rows = new Vector();
        String splitExpression = null;
        int newSelectedIndex = 0;
        
        if(delimiterIndex==-1) {
            //if its the first one set it to comma
            splitExpression = ImportUtilities.splitExpressions[0];
        }
        else {
            splitExpression = ImportUtilities.splitExpressions[delimiterIndex];
        }
        
        //}
        int columnCount = 0;
        reader = new BufferedReader(inReader);
        while ((line = reader.readLine()) != null) {
            
            String[] separated = line.split(splitExpression);
            commaCount = separated.length;
            if (first) {
                first = false;
                columnCount = commaCount;
            }
            else {
                if(commaCount>columnCount) {
                    columnCount = commaCount;
                }
            }
            boolean skipRow = false;
            if(separated.length==1) {
                if(separated[0].length()==0) {
                    skipRow = true;
                }
            }
            if(!skipRow) {
                rowCount++;
                rows.add(separated);
            }
        }
        
        DefaultTableModel tableModel = addRowsToTable(rows, columnCount);
        return(tableModel);
    }
    
    public static DefaultTableModel splitExcelFile(File toSplit, int acceptFormula, int tableIndex, boolean convertCharsToEntites) throws Exception {
        
        //file = toSplit;
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(toSplit));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        
        if(tableIndex>-1) {
            HSSFSheet sheet = wb.getSheetAt(tableIndex-1);
            
            boolean isBlankRow = true;
            
            int numCols = 0;
            int numRows = 0;
            //
            //int firstRow = 0;
            int firstColumn = 0;
            int iColumn = 0;
            //firstRow = getFirstRow(sheet,-1);
            
            /*if(firstRow==-1) {
             //MessageHandler.showError("Error, Cannot Read Sheet: "+tableIndex,"Import From Excel Error");
              return(null);
              }
              else {*/
            HSSFRow row = sheet.getRow(sheet.getFirstRowNum());
            firstColumn = getFirstColumn(sheet);
            
            numCols = getNumberOfCells(sheet); 
            
            //row = sheet.getRow(firstRow);
            //numRows = sheet.getLastRowNum() - firstRow;
            
            Vector rows = new Vector();
            /*System.out.println(firstColumn+":"+numCols);
             System.out.println(sheet.getFirstRowNum()+" To "+sheet.getLastRowNum());
             System.out.println(sheet.getPhysicalNumberOfRows());*/
            for (int rCnt = sheet.getFirstRowNum(); rCnt < sheet.getPhysicalNumberOfRows(); ++rCnt) {
                //reset the blank row boolean
                isBlankRow = true;
                
                row = sheet.getRow(rCnt);
                //System.out.println(numCols + ":" + firstColumn);
                String[] separated = new String[numCols - firstColumn];
                
                for (int cCnt = firstColumn; cCnt < numCols; ++cCnt) {
                    try {
                        HSSFCell cell = row.getCell((short) cCnt);
                        if (cell != null) {
                            switch(cell.getCellType()) {
                                
                                case HSSFCell.CELL_TYPE_NUMERIC://System.out.println(rCnt+":"+cCnt+ " is numeric");
                                    
                                    double value = row.getCell((short) cCnt).getNumericCellValue();
                                
                                try {
                                    //get the long value which eliminates the decimal point
                                    long iValue = (new Double(value)).longValue();
                                    //get the double value from this long value
                                    double longValue = (new Long(iValue)).doubleValue();
                                    //subtract the two, if answer is 0 then
                                    //value can be converted,
                                    //if not then it can't
                                    if(value-longValue==0) {
                                        //use long value
                                        separated[cCnt - firstColumn] = String.valueOf(iValue);
                                    }//end if
                                    else {
                                        //use double value
                                        separated[cCnt - firstColumn] = String.valueOf(value);
                                    }//end else
                                }//end try
                                catch (NumberFormatException e) {
                                    //use double value
                                    separated[cCnt - firstColumn] = String.valueOf(value);
                                }//end catch
                                
                                break;
                                
                                case HSSFCell.CELL_TYPE_STRING://System.out.println(rCnt+":"+cCnt+ " is string");
                                    
                                    isBlankRow = false;
                                separated[cCnt - firstColumn] = row.getCell((short) cCnt).getStringCellValue();
                                
                                break;
                                
                                case HSSFCell.CELL_TYPE_FORMULA://System.out.println(rCnt+":"+cCnt+ " is formula");
                                    
                                    isBlankRow = false;
                                if (acceptFormula == 0) {
                                    //prompt
                                    String[] options = { "Cell Value", "Formula Text"};
                                    Object formulaValue = JOptionPane.showInputDialog(
                                            null,
                                            "This worksheet contains formulas\n"
                                            + "What format would you like to import "
                                            + "the formula cells by: ",
                                            "Import From Table",
                                            JOptionPane.INFORMATION_MESSAGE,
                                            null, options, options[0]);
                                    if (formulaValue.toString().equalsIgnoreCase(
                                            options[0])) {
                                        //accept values
                                        acceptFormula = 2;
                                    }//end if
                                    else {
                                        acceptFormula = 1;
                                    }//end else
                                }//end if
                                else if (acceptFormula == 1) {
                                    //accept formula
                                    separated[cCnt - firstColumn] = row.getCell((short) cCnt)
                                    .getCellFormula();
                                    
                                }//end else
                                else if (acceptFormula == 2) {
                                    //dont accept formula
                                    double doubleValue = row.getCell((short) cCnt)
                                    .getNumericCellValue();
                                    Double dValue = new Double(doubleValue);
                                    if (dValue.isNaN()) {
                                        //should have been a string
                                        separated[cCnt - firstColumn] = row.getCell((short) cCnt)
                                        .getStringCellValue();
                                        
                                    }//end if
                                    else {
                                        try {
                                            
                                            //get the long value which eliminates the decimal point
                                            long iValue = (new Double(doubleValue)).longValue();
                                            
                                            //get the double value from this long value
                                            double longValue = (new Long(iValue)).doubleValue();
                                            
                                            //subtract the two, if answer is 0 then
                                            //value can be converted,
                                            //if not then it can't
                                            if(doubleValue-longValue==0) {
                                                //use long value
                                                separated[cCnt - firstColumn] = String.valueOf(iValue);
                                                
                                            }//end if
                                            else {
                                                //use double value
                                                separated[cCnt - firstColumn] = String.valueOf(doubleValue);
                                                
                                            }//end else
                                            
                                        }//end try
                                        catch (NumberFormatException e) {
                                            //use double value
                                            separated[cCnt - firstColumn] = String.valueOf(doubleValue);
                                            
                                        }//end catch
                                    }//end else
                                }//end else
                                
                                break;
                                
                                case HSSFCell.CELL_TYPE_ERROR://System.out.println(rCnt+":"+cCnt+ " is error");
                                    
                                    separated[cCnt - firstColumn] = "";
                                
                                break;
                                case HSSFCell.CELL_TYPE_BOOLEAN://System.out.println(rCnt+":"+cCnt+ " is boolean");
                                    
                                    isBlankRow = false;
                                boolean booleanValue = row.getCell((short) cCnt)
                                .getBooleanCellValue();
                                separated[cCnt - firstColumn] = String.valueOf(booleanValue);
                                
                                break;
                                case HSSFCell.CELL_TYPE_BLANK://System.out.println(rCnt+":"+cCnt+ " is blank");
                                    
                                    separated[cCnt - firstColumn] = "";
                                
                                break;
                                
                            }
                            
                            
                        }//end if cell!=null
                        else {
                            
                        }//end else
                        
                    }catch (Exception e) {
                        //just a blank cell
                        separated[cCnt - firstColumn] = "";
                        
                        
                    } //end try catch
                    
                    
                } //end for cCnt
                
                if(!isBlankRow) {
                    rows.add(separated);
                }
                //HSSFCell cell;
            } //end for rCnt    
            
            DefaultTableModel tableModel = addRowsToTable(rows, numCols - firstColumn);
            /*fileName = file.getAbsolutePath();
             fileName = fileName.substring(0, fileName.lastIndexOf("."));
             fileName += ".xml";*/
            return(tableModel);
            
        } //end else
        //}
        
        return(null);
        
    }
    
    
    public static int getFirstColumn(HSSFSheet sheet) throws Exception {
        
        
        int minimum = 0;
        boolean isFirstTime = true;
        for(int cnt=sheet.getFirstRowNum();cnt<sheet.getPhysicalNumberOfRows();++cnt) {
            
            //get the first row
            HSSFRow row = sheet.getRow(cnt);
            boolean found = false;
            //now find the first column that isn't null or empty
            short icnt = 0;
            while((icnt<row.getLastCellNum())&&(found!=true)) {
                
                try {
                    HSSFCell cell = row.getCell(icnt);
                    //System.out.println(icnt+":"+cell.getCellType());
                    if(cell!=null) {
                        //System.out.println(cell.getCellType());
                        if(icnt<minimum) {
                            minimum = icnt;
                        }
                        if(isFirstTime) {
                            minimum = icnt;
                            isFirstTime=false;
                        }
                        found=true;
                        
                        
                    }
                    
                }
                catch (NullPointerException e) {
                    // TODO Auto-generated catch block
                    //System.out.println(icnt+" is null");
                    e.printStackTrace();
                    
                }
                //System.out.println("minimum for row: "+cnt+ " is "+minimum);
                ++icnt;
            }
            
        }
        
        return(minimum);	    
    }
    
    public static int getNumberOfCells(HSSFSheet sheet) throws Exception {
        
        int maxNumberOfCells = 0;
        for(int cnt=sheet.getFirstRowNum();cnt<sheet.getPhysicalNumberOfRows();++cnt) {
            
            //get the first row
            HSSFRow row = sheet.getRow(cnt);
            if(row.getPhysicalNumberOfCells()>maxNumberOfCells) 
                maxNumberOfCells = row.getPhysicalNumberOfCells();
        }
        return(maxNumberOfCells);
    }
    
    public static String[] getWorkSheets(File toSplit) throws FileNotFoundException, IOException, Exception {
        
        
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(toSplit));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        
        
        String[] workSheets = new String[wb.getNumberOfSheets()+1];
        //this.tablesCombo.addItem("- Please Select -");
        workSheets[0] = "- Please Select -";
        for (int cnt = 0; cnt < wb.getNumberOfSheets(); ++cnt) {
            workSheets[cnt+1] = wb.getSheetName(cnt);
            
        }
        
        
        return(workSheets);
        
        
    }
    
    public static void processHeaderClick(int index, JTable table) throws Exception{
        //String previousValue = (String) headerValues.get(index);
        TableColumn col = table.getColumnModel().getColumn(index);
        String previousValue = (String)col.getHeaderValue();
        //find the index from the xmltypes
        for (int cnt = 0; cnt < ImportUtilities.xmlTypes.length; ++cnt) {
            if (previousValue.equalsIgnoreCase(ImportUtilities.xmlTypes[cnt])) {
                //found
                //if value is less than length-1 then just increment it,else
                // reset to zero
                if (cnt == ImportUtilities.xmlTypes.length - 1) {
                    
                    col.setHeaderValue(ImportUtilities.xmlTypes[0]);
                } else {
                    col.setHeaderValue(ImportUtilities.xmlTypes[cnt + 1]);
                }
            }
        }
    }
    
    public ImportUtilities () {
        
    }
    
    public class CellRenderer extends DefaultTableCellRenderer {
        
        public CellRenderer() {
            
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean focused, int row,
                int column) {
            
            //System.out.println("row:"+row+" Col:"+column);
            if (row == 0) {
                setEnabled(true);
                setComponentOrientation(ComponentOrientation.UNKNOWN);
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setBackground(Color.WHITE);
                Font font = table.getFont();
                Font newFont = new Font(font.getName(), Font.BOLD, font
                        .getSize());
                setFont(newFont);
            }
            else {
                setEnabled(false);
                Font font = table.getFont();
                Font newFont = new Font(font.getName(), Font.PLAIN, font
                        .getSize());
                setFont(newFont);
            }
            setValue(value);
            return this;
        }
        
        public void updateUI() {
            
            super.updateUI();
            //setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        }
    }
    
    public class HeaderRenderer extends DefaultTableCellRenderer {
        
        public HeaderRenderer() {
            
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            // This call is needed because DefaultTableCellRenderer calls
            // setBorder()
            // in its constructor, which is executed after updateUI()
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        }
        
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean focused, int row,
                int column) {
            
            JTableHeader h = table != null ? table.getTableHeader() : null;
            //System.out.println("row:"+row+" Col:"+column);
            if (h != null) {
                setEnabled(h.isEnabled());
                setComponentOrientation(h.getComponentOrientation());
                setForeground(h.getForeground());
                setBackground(h.getBackground());
                Font font = h.getFont();
                Font newFont = new Font(font.getName(), Font.BOLD, font
                        .getSize());
                setFont(newFont);
                //setBackground(Color.YELLOW);
                setValue(value);
            }
            else {
                /*
                 * Use sensible values instead of random leftover values from
                 * the last call
                 */
                setEnabled(true);
                setComponentOrientation(ComponentOrientation.UNKNOWN);
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setBackground(UIManager.getColor("TableHeader.background"));
                setFont(UIManager.getFont("TableHeader.font"));
                setValue(value);
            }
            return this;
        }
        
        public void updateUI() {
            
            super.updateUI();
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        }
    }
    
    public static String createXMLFile(JTable table, String docField, String rowField, boolean convertCharsToEntites) throws Exception{
        StringBuffer toWrite = new StringBuffer("");
        
        //		String rootElement = gui.setDocText.getText();
        String rootElement = docField;
        String elementName = rowField;
        //		String elementName = gui.setElementText.getText();
        
        toWrite.append("<?xml version=\"1.0\" encoding=\""
                + "UTF-8" + "\"?>\n");
        toWrite.append("<" + rootElement + ">");
        
        int colCount = table.getColumnCount();
        int rowCount = table.getRowCount();
        if(rowCount==1) {
            rowCount=2;
        }
        
        for (int oCnt = 1; oCnt < rowCount; ++oCnt) {
            //add root element
            toWrite.append("\n<" + elementName);
            for (int cCnt = 0; cCnt < colCount; ++cCnt) {
                TableColumn col = table.getColumnModel().getColumn(cCnt);
                String type = (String)col.getHeaderValue(); 
                if (type.equalsIgnoreCase(ImportUtilities.xmlTypes[1])) {
                    String titleValue = (String) table.getValueAt(0, cCnt);
                    if(titleValue.length()>0) {
                        toWrite.append(" " + titleValue + "=\"");
                        try {
                            toWrite.append(table.getValueAt(oCnt, cCnt));
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                            //there is no second row so no values
                        }
                        toWrite.append("\"");
                    }
                    
                }
            }
            toWrite.append(">\n");
            for (int cCnt = 0; cCnt < colCount; ++cCnt) {
                TableColumn col = table.getColumnModel().getColumn(cCnt);
                String type = (String)col.getHeaderValue(); 
                if (type.equalsIgnoreCase(ImportUtilities.xmlTypes[0])) {
                    String titleValue = (String) table.getValueAt(0, cCnt);
                    if(titleValue.length()>0) {
                        toWrite.append("\t<" + titleValue + ">");
                        try {
                            toWrite.append(table.getValueAt(oCnt, cCnt));
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                            //there is no second row so no values
                        }
                        
                        toWrite.append("</" + titleValue + ">\n");
                    }
                }
            }
            toWrite.append("</" + elementName + ">");
            
        }
        toWrite.append("\n</" + rootElement + ">\n");
        
        
        
        return(toWrite.toString());
    }
    
    public static String createDBXMLFile(JTable table, String docField, String rowField,
            Connection con,boolean reuseConnection, String driver, String dsn, String user, String password,
            String tableName, String whereString, String orderByString,String groupByString, boolean convertCharsToEntites) throws Exception {
        
        
        String rootElement = docField;
        String elementName = rowField;
        
        StringBuffer toWrite = new StringBuffer("");
        toWrite.append("<?xml version=\"1.0\" encoding=\""
                + "UTF-8" + "\"?>\n");
        toWrite.append("<" + rootElement + ">");
        
        
        //get the vector of data from the database
        //use 0 for number of rows to get all rows
        Vector data = getRowsFromDatabase(con,0,reuseConnection,driver,dsn,user,password,tableName,whereString,
                orderByString,groupByString);
        data = removeBadChars(data,convertCharsToEntites);
        //get the headers
        
        
        //cycle through vector to add data to xml file
        int rowCount = data.size();
        
        
        for (int oCnt = 1; oCnt < rowCount; ++oCnt) {
            String[] rowData = (String[])data.get(oCnt);
            int columnCount = rowData.length;
            
            //			add root element
            toWrite.append("\n<" + elementName);
            for (int cCnt = 0; cCnt < columnCount; ++cCnt) {
                TableColumn col = table.getColumnModel().getColumn(cCnt);
                String type = (String)col.getHeaderValue(); 
                if (type.equalsIgnoreCase("=\"ATTRIBUTE\"")) {
                    toWrite.append(" " + table.getValueAt(0, cCnt) + "=\""
                            + rowData[cCnt] + "\"");
                }
            }
            toWrite.append(">\n");
            for (int cCnt = 0; cCnt < columnCount; ++cCnt) {
                TableColumn col = table.getColumnModel().getColumn(cCnt);
                String type = (String)col.getHeaderValue(); 
                if (type.equalsIgnoreCase("<ELEMENT>")) {
                    toWrite.append("\t<" + table.getValueAt(0, cCnt) + ">");
                    toWrite.append(rowData[cCnt]);
                    toWrite.append("</" + table.getValueAt(0, cCnt) + ">\n");
                }
            }
            toWrite.append("</" + elementName + ">");
            
        }
        toWrite.append("\n</" + rootElement + ">\n");
        return(toWrite.toString());
        
        
    }
    
    public static Connection getConnection(String driver, String dsn, String user, String password) throws Exception {
        Connection con1 = null;
        Class.forName(driver);
        con1 = DriverManager.getConnection(dsn, user, password);
        
        return (con1);
    }
    
    public static DefaultTableModel splitDatabaseTable(Connection con, boolean reuseConnection,
            String driver, String dsn, String user, String password,
            String tableName,String whereString,String orderByString,String groupByString, boolean convertCharsToEntites) throws Exception {
        
        
        /*if(reuseConnection==false) {
         //close and reopen connection
          con.close();
          con = getConnection(driver,dsn,user,password);
          }*/
        //reset rows to null
        Vector rows = new Vector();
        
        rows = getRowsFromDatabase(con,5,reuseConnection,driver,dsn,user,password,tableName,whereString,
                orderByString,groupByString);
        
        DefaultTableModel tableModel = addRowsToTable(rows, ((String[]) rows.get(0)).length);
        
        return(tableModel);
        
        
    }
    
    public static Vector getRowsFromDatabase(Connection con, int numberOfRows,boolean reuseConnection,
            String driver, String dsn, String user, String password, String tableName,
            String whereString, String orderByString, String groupByString) throws Exception{
        
        if(reuseConnection==false) {
            //close and reopen connection
            con.close();
            con = getConnection(driver,dsn,user,password);
        }
        
        Vector temp = new Vector();
        String query = null;
        String[] separated;
        
        
        Statement stmt = con.createStatement();
        
        //allow the setMaxRows to be set
        if(numberOfRows>0)
            stmt.setMaxRows(numberOfRows);
        
        query = "select * from " + tableName;
        
        if(numberOfRows==0) {
            String whereString1 = whereString;
            String orderByString1 = orderByString;
            String groupByString1 = groupByString;
            
            //check for whereString
            //System.out.println(">"+whereString+"<"+"\n"+">"+whereString.trim()+"<");
            if(whereString1.trim().length()>0) {
                final String WHERE = "where ";
                
                //add it to the query
                if((whereString1.indexOf(WHERE)>-1)||(whereString1.indexOf(WHERE.toUpperCase())>-1)) {
                    query += " "+ whereString;
                }
                else {
                    query += " where "+ whereString;
                }
                
            }
            //check for orderbyString
            if(orderByString1.trim().length()>0) {
                //add it to the query
                final String ORDER_BY = "order by ";
                if((orderByString1.indexOf(ORDER_BY)>-1)||(orderByString1.indexOf(ORDER_BY.toUpperCase())>-1)) {
                    query += " "+ orderByString;
                }
                else {
                    query += " order by "+ orderByString;
                }
                
            }
            //check for groupbyString
            if(groupByString1.trim().length()>0) {
                //add it to the query
                final String GROUP_BY = "group by ";
                if((groupByString1.indexOf(GROUP_BY)>-1)||(groupByString1.indexOf(GROUP_BY.toUpperCase())>-1)) {
                    query += " "+ groupByString;
                }
                else {
                    query += " group by "+ groupByString;
                }
            }
            
        }
        
        ResultSet data = stmt.executeQuery(query);
        ResultSetMetaData rmeta = data.getMetaData();
        separated = new String[rmeta.getColumnCount()];
        //get column names
        for (int ccnt = 1; ccnt < rmeta.getColumnCount() + 1; ++ccnt) {
            separated[ccnt - 1] = rmeta.getColumnName(ccnt);
            //System.out.println(rmeta.getColumnName(ccnt));
        }
        temp.add(separated);
        while (data.next()) {
            String[] sep = new String[rmeta.getColumnCount()];
            for (int cnt = 1; cnt < rmeta.getColumnCount() + 1; ++cnt) {
                sep[cnt - 1] = data.getString(cnt);
            }
            temp.add(sep);
        }
        
        return(temp);
    }
    
    /**
     * Substitutes the selected characters with entities.
     */
    public static String substituteSelectedCharacters(String text, boolean skip) {
        
        if ( text != null) {
            
            
            StringBuffer newText =  new StringBuffer();
            
            for ( int i = 0; i < text.length(); i++) {
                char character = text.charAt( i);
                
                if  (character == '<') {
                    if(skip)
                        newText.append( "&lt;");
                    else 
                        newText.append( (char)character);
                    
                } else if ( character == '>' ) {
                    if(skip)
                        newText.append( "&gt;");
                    else
                        newText.append( (char)character);
                } else if ( character == '&' ) {
                    if(skip) {
                        newText.append( "&amp;");
                    }
                    else {
                        newText.append( (char)character);
                    }
                                        
                                        
                } else if ( character == '\'' ) {
                    if(skip)
                        newText.append( "&apos;");
                    else
                        newText.append( (char)character);
                    
                } else if ( character == '\"' ) {
                    if(skip) 
                        newText.append( "&quot;");
                    else
                        newText.append( (char)character);
                    
                /*} else if (  character > 127) {
                    if(!skip) {
                        String name = CommonEntities.getEntityName( character);
                        
                        try {
                            if ( !name.equals( "lt") && !name.equals( "gt") && !name.equals( "amp") && !name.equals( "apos") && !name.equals( "quot")) {
                                newText.append( "&");
                                newText.append( name);
                                newText.append( ";");
                            }
                        }
                        catch (NullPointerException e) {
                            //problem with converting char to entity
                            //just use that char
                            newText.append((char)character);
                        }
                    }
                    else {
                        newText.append( (char)character);
                    }*/
                } else {
                    newText.append( (char)character);
                }
            }
                                   
            return(newText.toString());
        }
        else {
            return(text);
        }
        
    }
}

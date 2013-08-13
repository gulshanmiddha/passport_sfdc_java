/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sfdc.Org;

/**
 *
 * @author Vikram
 */
public class XLSHandler {
    
    private Vector vectorDataExcelXLSX = new Vector();  
    private static Logger LOGGER = Logger.getLogger(XLSHandler.class);
    private ArrayList<String> populateListWithFileData(Vector vectorData) {
      ArrayList<String> retList = new ArrayList<String>();
        // Looping every row data in vector
        for(int i=0; i<vectorData.size(); i++) {
            Vector vectorCellEachRowData = (Vector) vectorData.get(i);
            StringBuffer rowData = new StringBuffer();
            // looping every cell in each row
            for(int j=0; j<vectorCellEachRowData.size(); j++) {
                
                if(vectorCellEachRowData.get(j).toString() != null && vectorCellEachRowData.get(j).toString().trim().length() > 0){
                    rowData.append(vectorCellEachRowData.get(j).toString() + "##");
                }
                
            }
            retList.add(rowData.toString());
        }
    
        return retList;
    }
    
      private Vector readDataExcelXLSX(String fileName) {
        Vector vectorData = new Vector();
         
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
             
            XSSFWorkbook xssfWorkBook = new XSSFWorkbook(fileInputStream);
             
            // Read data at sheet 0
            XSSFSheet xssfSheet = xssfWorkBook.getSheetAt(0);
            
            for(Integer i=0;i< xssfSheet.getLastRowNum(); i++)
            {
            	
            }
            
            
            Iterator rowIteration = xssfSheet.rowIterator();
            Row headerRow = xssfSheet.getRow(0);
            
            // Looping every row at sheet 0
            while (rowIteration.hasNext()) {
                XSSFRow xssfRow = (XSSFRow) rowIteration.next();
                Iterator cellIteration = xssfRow.cellIterator();
                 
                Vector vectorCellEachRowData = new Vector();
                 
                // Looping every cell in each row at sheet 0
                while (cellIteration.hasNext()) {
                    XSSFCell xssfCell = (XSSFCell) cellIteration.next();
                    vectorCellEachRowData.addElement(xssfCell);
                }
                 
                vectorData.addElement(vectorCellEachRowData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         
        return vectorData;
    }
      
      public ArrayList<HashMap<String, String>> getExcelRows(String fileName)
      {
    	  ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
    	  
    	  try
          {
              FileInputStream file = new FileInputStream(new File(fileName));
              HashMap<Integer, String> headerMap = new HashMap<Integer, String>();
              //Create Workbook instance holding reference to .xlsx file
              XSSFWorkbook workbook = new XSSFWorkbook(file);
              //LOGGER.info("Reading Excel File: " + fileName);
              //Get first/desired sheet from the workbook
              XSSFSheet sheet = workbook.getSheetAt(0);
              for(Integer i=0;i <= sheet.getLastRowNum(); i++)
              {
            	  Row row = sheet.getRow(i);
            	  HashMap<String, String> rowMap = new HashMap<String, String>();
            	  for(int cn=0; cn < row.getLastCellNum(); cn++) 
            	  {
	           	       Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);	           	       
	           	       if(i==0)
	           	       {
	           	    	   headerMap.put(cn, cell.toString().toUpperCase());
	           	       }
	           	       else
	           	    	   rowMap.put(headerMap.get(cn), cell.toString());	
	           	       //LOGGER.info(cell.toString() + "\t");
           	   		}
            	  if(i > 0)
            		  rows.add(rowMap);
            	 // LOGGER.info("\n");                            
              }
              file.close();
          } 
          catch (Exception e) 
          {
              e.printStackTrace();
          }
    	  
    	  return rows;
      }
      
      private ArrayList<String> readExcelFileData(String fileName){
          
          vectorDataExcelXLSX = readDataExcelXLSX(fileName);
          
          ArrayList<String> retList = populateListWithFileData(vectorDataExcelXLSX);
          
          return retList;
       }
      
      public void writeExcelFileData2(String fileName, ArrayList<HashMap<String, String>> rows)
      {
    	  ArrayList<String> excelRow = new ArrayList<String>();
    	  ArrayList<String> header = new ArrayList<String>(); 
    	  if(rows!=null && rows.size()>0)
    	  {
    		  String rowtext = "";
    		  for(String key: rows.get(0).keySet())
    		  {
    			  header.add(key);
    			  rowtext = rowtext + key + "##";
    		  }
    		  excelRow.add(rowtext);
    	  }
    	  
    	  for(HashMap<String, String> row: rows)
    	  {
    		  String rowtext = "";
    		  for(String key: header)
    		  {
    			  rowtext = rowtext + row.get(key) + "##";
    		  }
    		  excelRow.add(rowtext);
    	  }
    	  if(excelRow.size()>0)
    	  {
    		  writeToExcel(fileName, excelRow);
    	  }
      }
      
      public void writeExcelFileData(String fileName, ArrayList<HashMap<String, String>> rows)
      {
    	  try
    	  {
    		  FileInputStream file = new FileInputStream(new File(fileName));          
    		  XSSFWorkbook workbook = new XSSFWorkbook(file);          
    		  XSSFSheet sheet = workbook.getSheet("sheet0");
    		  
    		  //Get Header
    		  ArrayList<String> headerRow = new ArrayList<String>(); 
    		  if(rows.size() > 0)
    		  {
    			  for(String key: rows.get(0).keySet())
    			  {
    				  headerRow.add(key);
    			  }
    		  }
    		  
              int rownum = 0;
              
              Row row = sheet.createRow(rownum++);
              int cellnum = 0;
              for (String key : headerRow)
              {
                 Cell cell = row.createCell(cellnum++);                     
                 cell.setCellValue(key);                     
              }
              
              for (HashMap<String, String> rowMap: rows)
              {                  
            	  row = sheet.createRow(rownum++);
                  cellnum = 0;
                  for (String key : headerRow)
                  {
                     Cell cell = row.createCell(cellnum++);                     
                     cell.setCellValue(rowMap.get(key));                     
                  }
              }
              FileOutputStream out = new FileOutputStream(new File(fileName));
              workbook.write(out);
              out.close();    		      		  
    		  
    	  }catch(Exception e)
    	  {
    		  e.printStackTrace();
    	  }
      }
      private void writeToExcel(String fileName, ArrayList<String> rowList){
         
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet();
        Vector table=new Vector();
        Integer numOfColumns = 0;
        for(String row : rowList){
            numOfColumns = 0;
            for(String col : row.split("##")){
                table.add(new String(col));
                numOfColumns ++;
            }
            
        }
        
        Iterator rows=table.iterator();
        Enumeration rowsOfVector=table.elements();
        int totalNoOfRows=rowList.size();
        int currentRow=0;
        while (rows.hasNext () && currentRow<totalNoOfRows){
            XSSFRow row =  sheet.createRow(currentRow++);               

            for (int i = 0; i < numOfColumns ; i++) {
                XSSFCell cell=row.createCell(i);
                Object val=rows.next();
                if( val instanceof String){
                    cell.setCellValue(val.toString());
                }
                else if(val instanceof Date){
                    cell.setCellValue((java.sql.Date)val);
                }
                else if(val instanceof Double){
                    cell.setCellValue((Double)val);
                }
            }
        }

        FileOutputStream outPutStream = null;
        try {
            outPutStream = new FileOutputStream(fileName);
            workBook.write(outPutStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outPutStream != null) {
                try {
                    outPutStream.flush();
                    outPutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
      }
      
      public static void main(String[] args) 
      {
    	  ArrayList<String> a1 = new ArrayList<String>(); a1.add("1");a1.add("2");a1.add("3");
    	  ArrayList<String> a2 = new ArrayList<String>(); a2.add("4");a2.add("5");a2.add("6");
    	  
    	  a2.addAll(a1);
    	  
    	  for(String a: a2)
    	  {
    		  System.out.println(a);
    	  }
      }
}

package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FetchExcelData {

	public Object[][] readDataFromExcel() throws IOException {

		        File excel= new File("/RestAssured-A1/src/test/resources/config/ExcelData.xlsx");
		        FileInputStream file = new FileInputStream(excel);
		        XSSFWorkbook wbook = new XSSFWorkbook(file);
		        XSSFSheet sheet = wbook.getSheetAt(0);

		           int totalRows = sheet.getLastRowNum();       
		           int totalColumns = sheet.getRow(0).getLastCellNum();

		           System.out.println("Total Number of Columns in the excel is : "+totalColumns);     
		           System.out.println("Total Number of Rows in the excel is : "+totalRows);

		           Object[][] data= new Object[totalRows-1][totalColumns];
		           for(int i=0;i<totalRows-1;i++)
		            {

		               Row row = sheet.getRow(i+1);
		                for(int j=0;j<totalColumns;j++)
		                {             
		                    Cell cell= row.getCell(j);
		                    if (cell.getCellType().toString().equals("NUMERIC"))
		                    {
		                        data[i][j] = String.valueOf(cell.getNumericCellValue());  //type double is casted to integer type
		                        
		                    }
		                    else if (cell.getCellType().toString().equals("STRING"))
		                    {
		                        data[i][j]= cell.getStringCellValue();
		                    }
		                }
		            }

//		           for(Object[] str: data) {
//		               for(Object s: str) {
//		                   System.out.print(s + "\t");
//		               }
//		           }

		        wbook.close();
		        file.close();
		        return data;
	}
	
}

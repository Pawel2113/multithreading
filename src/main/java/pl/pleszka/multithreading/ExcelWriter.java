package pl.pleszka.multithreading;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Queue;

import static pl.pleszka.multithreading.Otodom.getSetSize;

public class ExcelWriter {

    private static String[] columns = {"Nazwa", "Adres", "Cena", "Powierzchnia", "Cena za metr", "Liczba pięter", "Liczba pokoi", "Rok budowy", "Data publikacji", "Standard", "Link"};
    public final Queue<House> queueOfHouses = new LinkedList<>();

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();

        // Create a Workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet("Mieszkania");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        //Create queue of houses
        Otodom otodom = new Otodom();
        Queue<House> queueOfHouses = otodom.pageReader();


        System.out.println("<<<<<<<<Test Maina>>>>>>>>>>>");
        // Create Other rows and cells with employees data
        int rowNum = 1;
        for (int i = 0; i < getSetSize()+1; i++) {
            System.out.println(i + " vs " + getSetSize());
            int breaker = 0;

            synchronized (queueOfHouses) {

                while (queueOfHouses.isEmpty()) {
                    try {
                        queueOfHouses.wait(1000);
                        breaker++;
                        if(breaker > 2)
                            break;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!queueOfHouses.isEmpty()) {
                    House house = queueOfHouses.poll();
                    System.out.println(house.getLink());

                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(house.getName());
                    row.createCell(1).setCellValue(house.getAddress());
                    row.createCell(2).setCellValue(house.getPrice());
                    row.createCell(3).setCellValue(house.getArea());
                    row.createCell(4).setCellValue(house.getPriceOfOneMeter());
                    row.createCell(5).setCellValue(house.getFloors());
                    row.createCell(6).setCellValue(house.getNumberOfRooms());
                    row.createCell(7).setCellValue(house.getYearOfBuilt());
                    row.createCell(8).setCellValue(house.getDateOfPublishing());
                    row.createCell(9).setCellValue(house.getCondition());
                    row.createCell(10).setCellValue(house.getLink());

                }
            }
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("nieruchomości_warszawa.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}

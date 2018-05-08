package com.xiafei.tools.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <P>Description: Excel工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/4/8</P>
 * <P>UPDATE DATE: 2018/4/8</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class ExcelUtil {
    public static final String XLSX = ".xlsx";
    public static final String XLS = ".xls";
    /**
     * Excel导出使用的字体名称.
     */
    private static final String FONT_NAME = "黑体";

    public static void main(String[] args) throws IOException {
        String sheetName = "用车统计表单";
        String titleName = "用车申请数据统计表";
        String fileName = "用车申请统计表单";
        int columnNumber = 3;
        int[] columnWidth = {10, 20, 30};
        String[][] dataList = {{"001", "2015-01-01", "IT"},
                {"002", "2015-01-02", "市场部"}, {"003", "2015-01-03", "测试"}};
        String[] columnName = {"单号", "申请时间", "申请部门"};

        export(sheetName, titleName, fileName, columnName, dataList,
                new FileOutputStream(new File("./temp/test.xlsx")), "123");
//        new ExcelUtil().read(new FileInputStream(new File("./temp/test.xlsx")), "test.xlsx", null, new ArrayList<>(),"1213");
    }

    /**
     * 导出数据到Excel.
     *
     * @param sheetName sheet页名字
     * @param title     标题
     * @param fileName  输出的Excel文件名
     * @param colsName  列名数组
     * @param data      数据二位数组，第一维代表一行，第二维是每列
     * @param response  http响应对象
     * @param uuid      业务流水号，记日志用，可以为空
     * @throws IOException 导出失败
     */
    public static void export(final String sheetName, final String title, final String fileName, final String[] colsName,
                              final String[][] data, final HttpServletResponse response, final String uuid)
            throws IOException {
        response.setContentType("application/ms-excel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename="
                .concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
        export(sheetName, title, fileName, colsName, data, response.getOutputStream(), uuid);
    }

    /**
     * 导出数据到Excel.
     *
     * @param sheetName sheet页名字
     * @param title     标题
     * @param fileName  输出的Excel文件名
     * @param colsName  列名数组
     * @param data      数据二位数组，第一维代表一行，第二维是每列
     * @param out       输出流
     * @param uuid      业务流水号，记日志用，可以为空
     * @throws IOException 导出失败
     */
    private static void export(final String sheetName, final String title, final String fileName, final String[] colsName,
                               final String[][] data, final OutputStream out, final String uuid) throws IOException {
        // 第一步，创建一个webbook
        Workbook wb;
        if (fileName.endsWith(".xls")) {
            wb = new HSSFWorkbook();
        } else {
            wb = new XSSFWorkbook();
        }
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = wb.createSheet(sheetName);
        for (int i = 0, len = colsName.length; i < len; i++) {
            sheet.setColumnWidth(i, colsName[i].length() << 10); // 单独设置每列的宽为字符数*256
        }
        // 第三步创建标题行
        final Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(50);// 设备标题的高度
        // 设计标题样式
        final CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置标题字体
        final Font titleFont = wb.createFont(); // 创建字体样式
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体加粗
        titleFont.setFontName(FONT_NAME); // 设置字体类型
        titleFont.setFontHeightInPoints((short) 15); // 设置字体大小
        titleStyle.setFont(titleFont); // 为标题样式设置字体样式
        // 真正创建标题单元格
        final Cell titleCell = titleRow.createCell(0);// 创建标题第一列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colsName.length - 1)); // 合并列标题
        titleCell.setCellValue(title); // 设置标题内容
        titleCell.setCellStyle(titleStyle); // 设置标题样式

        // 第四步 创建表头
        final Row headerRow = sheet.createRow(1);
        headerRow.setHeightInPoints(37);// 设置表头高度
        // 设置表头样式
        final CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setWrapText(true);// 设置自动换行
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个居中格式
        headerStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        headerStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 设备表头字体
        final Font headerFont = wb.createFont(); // 创建字体样式
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体加粗
        headerFont.setFontName(FONT_NAME); // 设置字体类型
        headerFont.setFontHeightInPoints((short) 10); // 设置字体大小
        headerStyle.setFont(headerFont); // 为标题样式设置字体样式
        // 创建表头每个单元格内容
        for (int i = 0, len = colsName.length; i < len; i++) {
            final Cell cell = headerRow.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(colsName[i]);
        }

        // 第五步 将数据导入workbook
        // 设置数据样式
        final CellStyle dataStyle = wb.createCellStyle();
        dataStyle.setWrapText(true);// 设置自动换行
        dataStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个上下居中格式
        dataStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
        // 设置边框
        dataStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        dataStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 创建单元格，并设置值
        for (int i = 0, rows = data.length; i < rows; i++) {
            final Row dataRow = sheet.createRow(i + 2);
            for (int j = 0, colNum = colsName.length; j < colNum; j++) {
                final Cell datacell = dataRow.createCell(j);
                datacell.setCellStyle(dataStyle);
                datacell.setCellValue(data[i][j]);
            }
        }

        // 最终 将文件写出到输出流
        try {
            wb.write(out);
            log.info("[{}]导出Excel成功", uuid);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 读取Excel数据.
     *
     * @param is        Excel文件输入流
     * @param fileName  Excel文件名
     * @param sheetName sheet页名字，可以为空，默认取第一个sheet页
     * @param dataList  要放读入数据的数据列表，使用时自己替换
     * @param uuid      业务流水号，记日志用
     * @throws IOException 读取失败
     */
    public void read(InputStream is, final String fileName, final String sheetName,
                     final List<Map<String, Object>> dataList, final String uuid)
            throws IOException {
        final Workbook wb;
        if (fileName.endsWith(XLS)) {
            wb = new HSSFWorkbook(is);
        } else if (fileName.endsWith(XLSX)) {
            wb = new XSSFWorkbook(is);
        } else {
            log.error("[{}]Excel格式错误，文件名[{}]", uuid, fileName);
            throw new RuntimeException("Excel格式错误");
        }
        try {
            final Sheet sheet;
            if (StringUtils.isNotBlank(sheetName)) {
                sheet = wb.getSheet(sheetName);
            } else {
                sheet = wb.getSheetAt(0);
            }
            for (Row row : sheet) {
                if (row.getRowNum() < 2) { // rowNum从0开始是第一行，这里假设标题有两行，跳过标题数据
                    continue;
                }
                System.out.println("rowNum:" + row.getRowNum());
                System.out.println(row.getCell(0).getStringCellValue());
                System.out.println(row.getCell(1).getStringCellValue());
                System.out.println(row.getCell(2).getStringCellValue());
            }
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                //
            }
        }

    }


    public static String readCell(Row row, int column) {
        final Cell cell = row.getCell(column);
        if (cell != null) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue();
        }
        return null;
    }
}

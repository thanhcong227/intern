package viettelsoftware.intern.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;
import org.jxls.common.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public final class FileUtil {

    public static <T> void writeToFileExcel(List<T> listData, Map<String, Object> params, String templatePath, String outputPath) {
        Context context = new Context();
        context.putVar("data", listData);
        if (params != null) {
            params.forEach(context::putVar);
        }
        File file = new File(outputPath);
        if (!file.getParentFile().exists() && (file.getParentFile().mkdirs())) {
            log.info("Create new new forder success: {}", file.getParentFile().getPath());
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    log.info("Create new file success: {}", file.getName());
                }
            } catch (IOException e) {
                log.error("Create new file error ", e);
            }
        }
        Paths.get(outputPath);
        try (
                InputStream is = new ClassPathResource(templatePath).getInputStream();
                OutputStream os = Files.newOutputStream(Paths.get(outputPath))) {
            JxlsHelper.getInstance().processTemplate(is, os, context);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<Object[]> readExcel(String excelFilePath, int sheetRead, int startRowRead)
            throws IOException {
        List<Object[]> listData = new ArrayList<>();

        InputStream inputStream = Files.newInputStream(new File(excelFilePath).toPath());

        Workbook workbook = getWorkbook(inputStream, excelFilePath);

        Sheet sheet = workbook.getSheetAt(sheetRead);

        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            if (nextRow.getRowNum() < startRowRead || isEmptyRow(nextRow)) {
                // Ignore header
                continue;
            }
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            Object[] item = new Object[nextRow.getLastCellNum()];
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                Object cellValue = getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                item[cell.getColumnIndex()] = cellValue;
            }
            listData.add(item);
        }

        workbook.close();
        inputStream.close();

        return listData;
    }

    private Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }
        return workbook;
    }

    private Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        Object cellValue = null;
        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case _NONE:
            case BLANK:
            case ERROR:
            default:
                break;
        }
        return cellValue;
    }

    private boolean isEmptyRow(Row row) {
        boolean isEmptyRow = true;
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                isEmptyRow = false;
            }
        }
        return isEmptyRow;
    }
}

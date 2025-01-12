package pro.sky.telegrambot.fileservice;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    /**
     * Экспортирует список категорий в формат Excel и возвращает данные в виде массива байтов.
     *
     * <p>Метод создает новый файл Excel, заполняет его данными из списка категорий
     * и возвращает представление этого файла в виде массива байтов.
     * Первая строка файла содержит заголовки столбцов: "id", "category_name" и "parent_id".
     *
     * @param categories список категорий {@link Category}, которые необходимо экспортировать в Excel
     * @return массив байтов, представляющий содержимое файла Excel с категориями
     * @throws IOException если происходит ошибка ввода-вывода при создании или записи файла Excel
     */

    public byte[] exportCategoriesToExcel(List<Category> categories) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("categories");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("category_name");
            headerRow.createCell(2).setCellValue("parent_id");
            int rowNum = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(String.valueOf(category.getParentId() != null ? category.getParentId().getId() : ""));
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }
}

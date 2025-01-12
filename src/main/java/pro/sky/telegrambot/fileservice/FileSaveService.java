package pro.sky.telegrambot.fileservice;

import java.io.*;
import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileSaveService {

    private final TreeManagerRepository treeManagerRepository;

    public File saveFile(byte[] data) throws IOException {
        File file = new File("categories.xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        return file;
    }

    @Transactional
    public void saveDataFromExcel(byte[] excelFile) throws Exception {
        log.info("Вызов метода saveDataFromExcel");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(excelFile)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Category category = new Category();
                category.setId((long) row.getCell(0).getNumericCellValue());
                category.setName(row.getCell(1).getStringCellValue());
                Long parentId = getLongCellNum(row.getCell(2));
                if (parentId != null) {
                    Category parent = treeManagerRepository.findById(parentId)
                            .orElse(null);
                    category.setParentId(parent);
                }
                treeManagerRepository.save(category);
            }
        }
    }

    private Long getLongCellNum(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                if (String.valueOf(cell).isEmpty()) {
                    return null;
                } else {
                    long integerPart = (long) cell.getNumericCellValue();
                    if (integerPart == cell.getNumericCellValue()) {
                        return integerPart;
                    } else {
                        throw new RuntimeException("Все числа в загружаемой таблице должны быть целыми: (ячейка)" + cell.getAddress());
                    }
                }
            case STRING:
                try {
                    String actualCell = String.valueOf(cell);
                    if (actualCell.isEmpty()) {
                        return null;
                    } else {
                        if (isNumeric(actualCell)) {
                            return Long.parseLong(cell.getStringCellValue());
                        } else {
                            throw new RuntimeException("Недопустимые символы в ячейке " + cell.getAddress());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            default:
                log.info("DEFAULT");
                return null;
        }
    }

    public boolean isNumeric(String cell) {
        return cell.matches("\\d+");
    }

}

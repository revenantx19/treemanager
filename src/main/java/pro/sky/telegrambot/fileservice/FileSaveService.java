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
import pro.sky.telegrambot.validator.CategoryValidator;

/**
 * Сервис для сохранения данных в файл и работы с категориями из Excel-файла.
 *
 * <p>Класс предоставляет методы для сохранения данных в файл формата Excel,
 * а также для извлечения данных из Excel-файлов и сохранения их в базу данных.
 * Он осуществляет валидацию данных и обрабатывает различные типы ячеек.
 *
 * <p>Включает в себя следующие основные методы:
 * <ul>
 *     <li>{@link #saveFile(byte[])} - сохраняет данные в файл Excel.</li>
 *     <li>{@link #saveDataFromExcel(byte[])} - обрабатывает файл Excel и сохраняет
 *     извлеченные данные в базу данных.</li>
 *     <li>{@link #getLongCellNum(Cell)} - извлекает целочисленное значение из ячейки.</li>
 *     <li>{@link #isNumeric(String)} - проверяет, является ли строка числовым значением.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileSaveService {
    private final TreeManagerRepository treeManagerRepository;
    private final CategoryValidator categoryValidator;
    /**
     * Сохраняет переданные данные в файл формата Excel с именем "categories.xlsx".
     *
     * <p>Метод создает новый файл и записывает в него содержимое из массива байтов,
     * переданного в качестве параметра. Если файл уже существует, он будет перезаписан.
     *
     * @param data массив байтов, содержащий данные для записи в файл
     * @return объект {@link File}, представляющий созданный или перезаписанный файл
     * @throws IOException если происходит ошибка ввода-вывода во время записи в файл
     */
    public File saveFile(byte[] data) throws IOException {
        File file = new File("categories.xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        return file;
    }
    /**
     * Сохраняет данные из переданного массива байтов, содержащего Excel-файл, в базу данных.
     *
     * <p>Метод обрабатывает первый лист Excel-файла и извлекает данные из строк,
     * начиная со второй (первая строка считается заголовком). Он ожидает, что
     * данные содержат информацию о категориях, включая идентификатор, имя и идентификатор родителя.
     *
     * <p>Если в ходе обработки возникают ошибки (например, неверные типы данных или
     * недопустимые символы), метод выбрасывает соответствующие исключения с сообщениями
     * об ошибках, указывая на конкретные ячейки, которые вызвали проблемы.
     *
     * @param excelFile массив байтов, представляющий содержимое Excel-файла
     * @throws Exception если происходит какая-либо ошибка во время обработки файла,
     *                   включая:
     *                   <ul>
     *                       <li>{@link IllegalStateException} если ячейка, ожидающая
     *                       числовое значение, содержит строку или пуста;</li>
     *                       <li>{@link RuntimeException} если содержимое ячейки не соответствует
     *                       ожидаемым форматам или ссылкам.</li>
     *                   </ul>
     */
    @Transactional
    public void saveDataFromExcel(byte[] excelFile) throws Exception {
        log.info("Вызов метода saveDataFromExcel");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(excelFile)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Category category = new Category();

                try {
                    if (row.getCell(0) != null) {
                        category.setId((long) row.getCell(0).getNumericCellValue());
                    } else {
                        throw new IllegalStateException("Имеются пустые ячейки в первом столбце");
                    }
                } catch (IllegalStateException e) {
                    if (e.getMessage().contains("Cannot get a NUMERIC value from")) {
                        throw new RuntimeException("Ячейка " + row.getCell(0).getAddress() +
                                " содержит строковое значение, ожидалось числовое значение.");
                    } else {
                        throw e;
                    }
                }

                if (!categoryValidator.checkInvalidChars(String.valueOf(row.getCell(1)))) {
                    category.setName(row.getCell(1).getStringCellValue());
                } else {
                    throw new RuntimeException("Имя каталога содержит недопустимые символы в ячейке " + row.getCell(1).getAddress());
                }

                Long parentId = getLongCellNum(row.getCell(2));
                if (parentId != null) {
                    Category parent = treeManagerRepository.findById(parentId)
                            .orElse(null);
                    if (parent != null) {
                        category.setParentId(parent);
                    } else {
                        throw new RuntimeException("Указана ссылка на несуществующий каталог " + row.getCell(2).getAddress());
                    }
                }
                treeManagerRepository.save(category);
            }
        }
    }

    /**
     * Извлекает целочисленное значение из указанной ячейки типа {@link Cell}.
     *
     * <p>Метод обрабатывает различные типы ячеек и возвращает
     * целочисленное значение, если это возможно. Он поддерживает следующие типы ячеек:
     * <ul>
     *     <li>{@link CellType#NUMERIC} - ячейка с числовым значением.</li>
     *     <li>{@link CellType#STRING} - ячейка с строковым значением,
     *     которое может быть преобразовано в целое число.</li>
     *     <li>{@link CellType#BLANK} - пустая ячейка, возвращает {@code null}.</li>
     * </ul>
     *
     * <p>Если ячейка содержит дробное число, недопустимые символы или
     * имеет неподдерживаемый тип, метод выбрасывает {@link RuntimeException}
     * с соответствующим сообщением.
     *
     * @param cell ячейка {@link Cell}, из которой необходимо извлечь целое число
     * @return целочисленное значение как {@link Long}, если ячейка корректно
     *         содержит целое число; {@code null}, если ячейка пуста или не содержит
     *         допустимое значение.
     *
     * @throws RuntimeException если ячейка содержит дробное число, недопустимые
     *         символы или если тип ячейки не поддерживается.
     */

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
            case BLANK:
                return null;
            default:
                throw new RuntimeException("Недопустимый тип ячейки " + cell.getAddress());
        }
    }

    public boolean isNumeric(String cell) {
        return cell.matches("\\d+");
    }

}



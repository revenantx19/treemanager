package pro.sky.telegrambot.commands.impl;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.fileservice.ExcelExportService;
import pro.sky.telegrambot.fileservice.FileSaveService;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class DownloadCommandTest {

    @Mock
    private TreeManagerRepository treeManagerRepository;

    @Mock
    private NewMessage newMessage;

    @Mock
    private MessageContext messageContext;

    @Mock
    private ExcelExportService excelExportService;

    @Mock
    private FileSaveService fileSaveService;

    @InjectMocks
    private DownloadCommand downloadCommand;

    public DownloadCommandTest() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест №1: Успешное выполнение команды загрузки.
     * Проверяет, что Excel-документ создается и отправляется пользователю.
     */
    @Disabled
    @Test
    void testExecute_Success() throws IOException {
        // Подготовка тестовых данных
        List<Category> categories = List.of(new Category(), new Category());

        when(treeManagerRepository.findAll()).thenReturn(categories);
        when(excelExportService.exportCategoriesToExcel(categories)).thenReturn(new byte[0]);
        when(fileSaveService.saveFile(any(byte[].class))).thenReturn(new File("test"));

        when(messageContext.getUpdate()).thenReturn(mock(Update.class));
        when(messageContext.getUpdate().message()).thenReturn(mock(Message.class));
        when(messageContext.getUpdate().message().chat()).thenReturn(mock(Chat.class));
        when(messageContext.getUpdate().message().chat().id()).thenReturn(1L);

        downloadCommand.execute(messageContext);

        verify(treeManagerRepository).findAll();
        verify(excelExportService).exportCategoriesToExcel(categories);
        verify(fileSaveService).saveFile(any());
        verify(newMessage).sendNewFile(1L, new File("test"));
    }

    /**
     * Тест №2: Обработка исключения при создании файла.
     * Проверяет, что выбрасывается RuntimeException, если возникает IOException при сохранении файла.
     */
    @Disabled
    @Test
    void testExecute_IOException() throws IOException {
        // Подготовка тестовых данных
        List<Category> categories = List.of(new Category(), new Category());
        MessageContext messageContext = mock(MessageContext.class);

        when(treeManagerRepository.findAll()).thenReturn(categories);
        when(excelExportService.exportCategoriesToExcel(categories)).thenReturn(new byte[0]);
        when(fileSaveService.saveFile(any())).thenThrow(new IOException("File error"));

        when(messageContext.getUpdate()).thenReturn(mock(Update.class));
        when(messageContext.getUpdate().message()).thenReturn(mock(Message.class));
        when(messageContext.getUpdate().message().chat()).thenReturn(mock(Chat.class));
        when(messageContext.getUpdate().message().chat().id()).thenReturn(1L);

        // Проверка выбрасываемого исключения
        assertThrows(RuntimeException.class, () -> downloadCommand.execute(messageContext));

        // Проверка, что метод не дошел до отправки сообщения
        verify(newMessage, never()).sendNewFile(anyLong(), any(File.class));
    }
}

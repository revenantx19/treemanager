import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pro.sky.telegrambot.commands.impl.UploadCommand;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.fileservice.FileSaveService;
import pro.sky.telegrambot.messagesender.NewMessage;

import java.io.IOException;

import static org.mockito.Mockito.*;

class UploadCommandTest {

    @Mock
    private TelegramBot bot;
    @Mock
    private FileSaveService fileSaveService;
    @Mock
    private NewMessage newMessage;
    @InjectMocks
    private UploadCommand uploadCommand;
    @Mock
    private MessageContext messageContext;
    @Mock
    private Update update;
    @Mock
    private Document document;
    @Mock
    private GetFileResponse getFileResponse;
    @Mock
    private File file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(messageContext.getUpdate().message().document().fileId()).thenReturn(anyString());
        when(messageContext.getUpdate()).thenReturn(update);
        when(update.message().document()).thenReturn(document);
        when(document.fileId()).thenReturn("file_id");
        when(bot.execute(any(GetFile.class))).thenReturn(getFileResponse);
        when(getFileResponse.file()).thenReturn(file);
    }

    /**
     * Проверяет корректную загрузку документа Excel.
     */
    @Test
    void execute_ShouldSaveDataFromExcel_WhenFileRetrievedSuccessfully() throws Exception {
        byte[] fileContent = new byte[]{1, 2, 3};
        when(bot.getFileContent(file)).thenReturn(fileContent);

        uploadCommand.execute(messageContext);

        verify(fileSaveService, times(1)).saveDataFromExcel(fileContent);
    }

    /**
     * Проверяет обработку исключения и отправку сообщения об ошибке.
     */
    @Test
    void execute_ShouldLogError_WhenExceptionThrown() throws IOException {
        when(bot.getFileContent(file)).thenThrow(new RuntimeException("Test error"));

        uploadCommand.execute(messageContext);

        verify(newMessage, times(1)).createNewMessage(anyLong(), eq("Test error"));
    }
}

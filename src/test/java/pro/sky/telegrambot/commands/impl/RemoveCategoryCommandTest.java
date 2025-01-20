package pro.sky.telegrambot.commands.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoveCategoryCommandTest {
    @Mock
    private TreeManagerRepository treeManagerRepository;
    @Mock
    private NewMessage newMessage;
    @Mock
    private MessageContext messageContext;
    @InjectMocks
    private RemoveCategoryCommand removeCategoryCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест №1 для метода {@link RemoveCategoryCommand#execute(MessageContext)}.
     * <p>
     * Проверяет, что категория удаляется по идентификатору,
     * когда передан числовой параметр.
     * В частности, подтверждается, что метод {@code removeCategoriesById}
     * из класса {@link TreeManagerRepository} вызывается с идентификатором {@code 10L}.
     * После выполнения операции флаг {@link RemoveCategoryCommand#flag} сбрасывается в {@code false}.
     * </p>
     */
    @Test
    void testExecuteDeletesCategoryById() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.firstParamIsNumeric()).thenReturn(true);
        when(messageContext.getP1()).thenReturn("10");
        RemoveCategoryCommand.flag = true;

        removeCategoryCommand.execute(messageContext);

        verify(treeManagerRepository).removeCategoriesById(10L);
        assertFalse(RemoveCategoryCommand.flag);
    }

    /**
     * Тест №2 для метода {@link RemoveCategoryCommand#execute(MessageContext)}.
     * <p>
     * Проверяет, что категории находятся по имени,
     * когда передан нечисловой параметр.
     * В частности, подтверждается, что метод {@code findPathByFolderName}
     * из класса {@link TreeManagerRepository} возвращает список путей
     * для категории с именем {@code "TestCategory"}.
     * После выполнения операции флаг {@link RemoveCategoryCommand#flag} устанавливается в {@code true}.
     * </p>
     */
    @Test
    void testExecuteFindsCategoriesByName() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.firstParamIsNumeric()).thenReturn(false);
        when(messageContext.getP1()).thenReturn("TestCategory");
        List<String> paths = List.of("path1", "path2");
        when(treeManagerRepository.findPathByFolderName("TestCategory")).thenReturn(paths);

        removeCategoryCommand.execute(messageContext);

        verify(newMessage).createNewMessage(anyLong(), contains("path1\npath2"));
        assertTrue(RemoveCategoryCommand.flag);
    }

    /**
     * Тест №3 для метода {@link RemoveCategoryCommand#execute(MessageContext)}.
     * <p>
     * Проверяет обработку случая, когда категория не найдена по имени.
     * В частности, подтверждается вызов метода {@code createNewMessage}
     * для класса {@link NewMessage} с сообщением о том, что категория не найдена.
     * Флаг {@link RemoveCategoryCommand#flag} остаётся {@code false}.
     * </p>
     */
    @Test
    void testExecuteHandlesCategoryNotFound() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.firstParamIsNumeric()).thenReturn(false);
        when(treeManagerRepository.findPathByFolderName("TestCategory")).thenReturn(List.of());

        removeCategoryCommand.execute(messageContext);

        verify(newMessage).createNewMessage(anyLong(), contains("Каталог с таким именем не найден"));
        assertFalse(RemoveCategoryCommand.flag);
    }

    /**
     * Тест №4 для метода {@link RemoveCategoryCommand#execute(MessageContext)}.
     * <p>
     * Проверяет, что выполняется правильное поведение при передаче нечислового
     * параметра. Убедимся, что категории находятся по имени, и
     * сообщение с путями возвращается корректно.
     * </p>
     */
    @Test
    void testExecuteWithNonNumericFirstParam() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.firstParamIsNumeric()).thenReturn(false);
        when(messageContext.getP1()).thenReturn("TestCategory");

        List<String> paths = List.of("path1");
        when(treeManagerRepository.findPathByFolderName("TestCategory")).thenReturn(paths);

        removeCategoryCommand.execute(messageContext);

        verify(newMessage).createNewMessage(anyLong(), contains("path1"));
        assertTrue(RemoveCategoryCommand.flag);
    }

    /**
     * Тест №5 для метода {@link RemoveCategoryCommand#removeFolderById(Long)}.
     * <p>
     * Проверяет, что метод вызывает репозиторий для удаления папки по идентификатору.
     * Убедимся, что метод {@code removeCategoriesById} из класса
     * {@link TreeManagerRepository} вызывается с идентификатором {@code 1L}.
     * </p>
     */
    @Test
    void testRemoveFolderByIdCallsRepository() {
        removeCategoryCommand.removeFolderById(1L);

        verify(treeManagerRepository).removeCategoriesById(1L);
    }
}
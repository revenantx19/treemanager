package pro.sky.telegrambot.commands.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    TreeManagerRepository treeManagerRepository;

    @Mock
    NewMessage newMessage;

    @Mock
    MessageContext messageContext;

    private RemoveCategoryCommand removeCategoryCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        removeCategoryCommand = new RemoveCategoryCommand(treeManagerRepository, newMessage);
    }

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

    @Test
    void testExecuteHandlesCategoryNotFound() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.firstParamIsNumeric()).thenReturn(false);
        when(treeManagerRepository.findPathByFolderName("TestCategory")).thenReturn(List.of());

        removeCategoryCommand.execute(messageContext);

        verify(newMessage).createNewMessage(anyLong(), contains("Каталог с таким именем не найден"));
        assertFalse(RemoveCategoryCommand.flag);
    }

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

    @Test
    void testRemoveFolderByIdCallsRepository() {
        removeCategoryCommand.removeFolderById(1L);

        verify(treeManagerRepository).removeCategoriesById(1L);
    }
}
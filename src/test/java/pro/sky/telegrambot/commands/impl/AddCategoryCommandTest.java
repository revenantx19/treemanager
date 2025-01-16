package pro.sky.telegrambot.commands.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pro.sky.telegrambot.context.MessageContext;
import pro.sky.telegrambot.messagesender.NewMessage;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.TreeManagerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pro.sky.telegrambot.commands.impl.AddCategoryCommand.saverChildFolderName;

@Slf4j
class AddCategoryCommandTest {
    @Mock
    TreeManagerRepository treeManagerRepository;

    @Mock
    NewMessage newMessage;

    @Mock
    MessageContext messageContext;

    private AddCategoryCommand addCategoryCommand;

    private Category rootCategory = new Category();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addCategoryCommand = new AddCategoryCommand(newMessage, treeManagerRepository);
        saverChildFolderName = "folder";
        rootCategory.setName("root");
        rootCategory.setId(1L);
    }

    @Test
    void testExecuteAddChildCategoryById() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.getP1()).thenReturn("10");
        when(messageContext.firstParamIsNumeric()).thenReturn(true);

        List<String> directoriesForAddedFolders = List.of("path1", "path2", "path3");

        when(treeManagerRepository.findPathByFolderName(messageContext.getP1())).thenReturn(directoriesForAddedFolders);

        when(treeManagerRepository.findById(any())).thenReturn(Optional.of(rootCategory));



        when(treeManagerRepository.existsByParentIdAndName(any(), any())).thenReturn(false);

        Long parentFolderId = Long.parseLong(messageContext.getP1());

        Optional<Category> parentCategory = Optional.of(new Category());
        parentCategory.get().setId(parentFolderId);
        when(treeManagerRepository.findById(10L)).thenReturn(parentCategory);

        addCategoryCommand.execute(messageContext);

        verify(treeManagerRepository).addElement(eq("folder"), eq(parentFolderId));
        assertTrue(saverChildFolderName == null);

    }

    @Test
    void testAddChildCategorySuccessfully() {
        // Arrange
        when(messageContext.firstParamIsNumeric()).thenReturn(true);
        when(messageContext.getP1()).thenReturn("10");

        Optional<Category> parentCategory = Optional.of(new Category());
        when(treeManagerRepository.findById(10L)).thenReturn(parentCategory);

        // Правильное значение для сравнения
        when(treeManagerRepository.existsByParentIdAndName(Optional.ofNullable(eq(parentCategory.get())), eq("folder"))).thenReturn(false);

        Long parentFolderId = Long.parseLong(messageContext.getP1());

        // Act
        addCategoryCommand.execute(messageContext);

        // Assert
        verify(treeManagerRepository).addElement(eq("folder"), eq(parentFolderId));
    }


}
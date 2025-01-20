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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
    /**
     * Тест №1 для метода {@link AddCategoryCommand#execute(MessageContext)}.
     * <p>
     * Проверяет, что выполняется правильное поведение, когда первый параметр
     * (полученный из {@link MessageContext}) является числовым.
     * В частности, подтверждается, что метод {@code findById} из класса
     * {@link TreeManagerRepository} вызывается ровно один раз с любым значением типа long.
     * </p>
     */
    @Test
    void execute_ShouldAddChildCategory_WhenFirstParamIsNumeric() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.getP1()).thenReturn("10");
        when(messageContext.firstParamIsNumeric()).thenReturn(true);

        addCategoryCommand.execute(messageContext);

        verify(treeManagerRepository, times(1)).findById(anyLong());
    }
    /**
     * Тест №2 для метода {@link AddCategoryCommand#execute(MessageContext)}.
     * <p>
     * Проверяет, что выполняется правильное поведение, когда первый параметр
     * (полученный из {@link MessageContext}) является нечисловым.
     * В частности, подтверждается, что метод {@code findPathByFolderName} из класса
     * {@link TreeManagerRepository} вызывается ровно один раз с любым значением типа {@code String}.
     * </p>
     */
    @Test
    void execute_ShouldAddRootCategory_WhenFirstParamIsNotNumeric() {
        when(messageContext.getP1()).thenReturn("folder");
        when(messageContext.firstParamIsNumeric()).thenReturn(false);

        addCategoryCommand.execute(messageContext);

        verify(treeManagerRepository, times(1)).findPathByFolderName(anyString());
    }
    /**
     * Тест №3 для метода {@link AddCategoryCommand#addChildCategory(MessageContext)}.
     * <p>
     * Проверяет, что происходит исключение {@link NoSuchElementException},
     * когда родительская категория не найдена в {@link TreeManagerRepository}.
     * Метод {@code findById} вызывается с любым значением типа long,
     * и при отсутствии категории выбрасывается исключение.
     * </p>
     */
    @Test
    void execute_ShouldThrowNoSuchElementException_WhenParentCategoryNotFound() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.getP1()).thenReturn("10");
        when(messageContext.firstParamIsNumeric()).thenReturn(true);

        when(treeManagerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> addCategoryCommand.addChildCategory(messageContext));
    }
    /**
     * Тест №4 для метода {@link AddCategoryCommand#addChildCategory(MessageContext)}.
     * <p>
     * Проверяет, что категория добавляется, когда родительская категория существует.
     * В частности, подтверждается вызов метода {@code addElement}
     * из класса {@link TreeManagerRepository} с аргументами {@code "folder"}
     * и значением {@code 1L}.
     * </p>
     */
    @Test
    void addChildCategory_ShouldAddCategory_WhenParentCategoryExists() {
        when(messageContext.getChatId()).thenReturn(1L);
        when(messageContext.getP1()).thenReturn("10");
        when(messageContext.firstParamIsNumeric()).thenReturn(false);

        when(treeManagerRepository.findById(10L)).thenReturn(Optional.of(rootCategory));
        when(treeManagerRepository.existsByParentIdAndName(any(), anyString())).thenReturn(false);

        addCategoryCommand.addChildCategory(messageContext);

        verify(treeManagerRepository, times(1)).addElement(eq("folder"), eq(1L));
        assertNull(saverChildFolderName);
    }
    /**
     * Тест №5 для метода {@link AddCategoryCommand#addRootOrSelectExistingCategory(MessageContext, List)}.
     * <p>
     * Проверяет, что категория сохраняется, если такая категория не существует.
     * В частности, подтверждается, что метод {@code save} из класса
     * {@link TreeManagerRepository} вызывается ровно один раз с любым объектом типа {@link Category}.
     * </p>
     */
    @Test
    void addRootOrSelectExistingCategory_ShouldSaveCategory_WhenCategoryNotExist() {
        when(messageContext.getMessage()).thenReturn(new String[]{"folder", "folder1"});
        when(treeManagerRepository.existsByNameAndParentIdIsNull(anyString())).thenReturn(false);

        addCategoryCommand.addRootOrSelectExistingCategory(messageContext, List.of());

        verify(treeManagerRepository, times(1)).save(any(Category.class));
    }

}
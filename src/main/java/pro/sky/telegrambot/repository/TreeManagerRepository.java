package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.Category;

import java.util.List;
import java.util.Optional;
/**
 * Репозиторий для управления категорией в базе данных.
 *
 * <p>Этот интерфейс предоставляет методы для выполнения операций
 * с сущностью {@link Category}, включая добавление, удаление
 * и извлечение данных иерархии категорий.
 */
@Repository
public interface TreeManagerRepository extends JpaRepository<Category, Long> {
    /**
     * Добавляет новый элемент категории с указанным именем и родительским ID.
     *
     * <p>Метод выполняет SQL-запрос для вставки новой категории
     * в таблицу "treemanager".
     *
     * @param name имя новой категории
     * @param parentId идентификатор родительской категории (может быть null)
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO treemanager (name, parent_id) VALUES (?, ?)")
    void addElement(String name, Long parentId);
    /**
     * Получает дерево категорий в виде строк, представляющих иерархию.
     *
     * <p>Метод использует рекурсивный запрос для построения полного дерева
     * категорий с отступами, указывающими уровень вложенности.
     *
     * @return список строк, представляющих категорию и ее уровень
     */
    @Query(value = "WITH RECURSIVE category_tree AS (" +
            "SELECT id, name, parent_id, 1 AS level, name AS path " +
            "FROM treemanager WHERE parent_id IS NULL " +
            "UNION ALL " +
            "SELECT t.id, t.name, t.parent_id, ct.level + 1 AS level, CAST((ct.path || ' > ' || t.name) AS character varying(255)) AS path " +
            "FROM treemanager AS t JOIN category_tree AS ct ON t.parent_id = ct.id) " +
            "SELECT repeat('----', category_tree.level - 1) || category_tree.name FROM category_tree ORDER BY path", nativeQuery = true)
    List<String> viewCategoryTree();
    /**
     * Находит путь к категории по имени.
     *
     * <p>Метод использует рекурсивный запрос для построения пути,
     * который ведет к категории с заданным именем.
     *
     * @param folderName имя папки, путь к которой необходимо найти
     * @return список строк с id и полным путем к папке
     */
    @Query(value = "WITH RECURSIVE catalog_path AS ( " +
            " SELECT id, name, parent_id, CAST(name AS character varying(255)) AS path " +
            " FROM treemanager " +
            " WHERE parent_id IS NULL " +
            " UNION ALL " +
            " SELECT d.id, d.name, d.parent_id, CAST(cp.path || '\\' || d.name AS character varying(255)) " +
            " FROM treemanager d " +
            " INNER JOIN catalog_path cp ON cp.id = d.parent_id)" +
            " SELECT CONCAT(id, ' - ', path) FROM catalog_path WHERE name = :folderName", nativeQuery = true)
    List<String> findPathByFolderName(String folderName);
    /**
     * Удаляет категорию по указанному идентификатору.
     *
     * <p>Метод выполняет удаление категории с заданным id.
     *
     * @param id идентификатор категории для удаления
     */
    void removeCategoriesById(Long id);
    /**
     * Проверяет, существует ли корневая категория с заданным именем.
     *
     * @param rootFolderName имя корневой категории
     * @return true, если категория существует, иначе false
     */
    boolean existsByNameAndParentIdIsNull(String rootFolderName);
    /**
     * Проверяет, существует ли подкатегория с заданным родительским id и именем.
     *
     * @param folderId опциональный идентификатор родительской категории
     * @param saverChildFolderName имя подкатегории
     * @return true, если подкатегория существует, иначе false
     */
    boolean existsByParentIdAndName(Optional<Category> folderId, String saverChildFolderName);
}

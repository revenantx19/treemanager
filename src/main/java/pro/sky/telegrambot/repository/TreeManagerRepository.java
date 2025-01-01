package pro.sky.telegrambot.repository;

import org.apache.el.util.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreeManagerRepository extends JpaRepository<Category, Long> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO treemanager (name, parent_id) VALUES (?, ?)")
    void addElement(String name, Long parentId);

    @Query(value = "SELECT id FROM treemanager WHERE name = :s", nativeQuery = true)
    Optional<Category> findParentId(String s);

    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Optional<Category> findByName(String name);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM treemanager WHERE name = :name AND parent_id = :id)", nativeQuery = true)
    boolean existsName(Long id, String name);

    @Query(value = "DELETE FROM treemanager WHERE name = :folderName", nativeQuery = true)
    void delete(String folderName);

    @Query(value = "WITH RECURSIVE tree AS (SELECT id, name, parent_id, 1 as level " +
            "FROM treemanager WHERE parent_id IS NULL UNION ALL " +
            "SELECT d.id, d.name, d.parent_id, tree.level + 1 " +
            "FROM treemanager d JOIN tree ON d.parent_id = tree.id) " +
            "SELECT /*repeat('--', tree.level - 1) || tree.name as*/ * FROM tree /*ORDER BY path*/", nativeQuery = true)
    List<String> viewTree();

    @Query(value = "WITH RECURSIVE category_tree AS (" +
            "SELECT id, name, parent_id, 1 AS level, name AS path " +
            "FROM treemanager WHERE parent_id IS NULL " +
            "UNION ALL " +
            "SELECT t.id, t.name, t.parent_id, ct.level + 1 AS level, CAST((ct.path || ' > ' || t.name) AS character varying(255)) AS path " +
            "FROM treemanager AS t JOIN category_tree AS ct ON t.parent_id = ct.id) " +
            "SELECT repeat('--', category_tree.level - 1) || category_tree.name FROM category_tree ORDER BY path", nativeQuery = true)
    List<String> viewCategoryTree();

    @Query(value = "SELECT parent_id, name FROM treemanager WHERE name = :folderName", nativeQuery = true)
    List<String> viewRemoveElements(String folderName);

    @Query(value = "WITH RECURSIVE folder_tree AS (" +
            " SELECT id, name, parent_id, name AS full_path " +
            " FROM treemanager WHERE name = :name " +
            " UNION ALL " +
            " SELECT d.id, d.name, d.parent_id, CAST((ft.full_path || ' > ' || d.name) AS character varying(255)) " +
            " FROM treemanager AS d " +
            " JOIN folder_tree ft ON d.id = ft.parent_id" +
            ") " +
            "SELECT full_path FROM folder_tree", nativeQuery = true)
    List<String> showAllRemoveFolders(String name);

    @Query(value = "WITH RECURSIVE TreePaths AS (" +
            "    SELECT name, parent_id, name AS full_path " +
            "    FROM treemanager " +
            "    WHERE name LIKE :name " +
            "    UNION ALL " +
            "    SELECT tm.name, tm.parent_id, CAST(CONCAT(tp.full_path, '/', tm.name) AS character varying(255)) AS full_path " +
            "    FROM treemanager tm " +
            "    JOIN TreePaths tp ON tp.parent_id = tm.id " +
            ") " +
            "SELECT DISTINCT CASE " +
            "    WHEN parent_id IS NULL THEN name " +
            "    ELSE full_path END " +
            "FROM TreePaths", nativeQuery = true)
    List<String> findDirectoriesByName(String name);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM treemanager WHERE name = :folderName AND parent_id IS NULL)", nativeQuery = true)
    boolean existsParentCategory(String folderName);
}

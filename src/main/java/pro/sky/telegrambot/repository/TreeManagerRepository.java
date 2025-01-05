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

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :childName AND c.parentId = :parentId")
    boolean existsChildCategory(String childName, Long parentId);

    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Optional<Category> findByName(String name);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM treemanager WHERE name = :name AND parent_id = :id)", nativeQuery = true)
    boolean existsName(Long id, String name);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM treemanager WHERE name = :folderName)", nativeQuery = true)
    boolean existsByFolderName(String folderName);

    @Query(value = "WITH RECURSIVE category_tree AS (" +
            "SELECT id, name, parent_id, 1 AS level, name AS path " +
            "FROM treemanager WHERE parent_id IS NULL " +
            "UNION ALL " +
            "SELECT t.id, t.name, t.parent_id, ct.level + 1 AS level, CAST((ct.path || ' > ' || t.name) AS character varying(255)) AS path " +
            "FROM treemanager AS t JOIN category_tree AS ct ON t.parent_id = ct.id) " +
            "SELECT repeat('--', category_tree.level - 1) || category_tree.name FROM category_tree ORDER BY path", nativeQuery = true)
    List<String> viewCategoryTree();

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

    void removeByName(String name);

    void removeCategoriesById(Long id);

    @Query(value = "SELECT COUNT(c) > 0 FROM treemanager c JOIN treemanager p ON c.parent_id = p.id " +
            " WHERE c.name = :childName AND p.name = :parentName", nativeQuery = true)
    boolean existsChildUnderParent(String childName, String parentName);
}

package pro.sky.telegrambot.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Модель категории для управления иерархией категорий.
 *
 * <p>Этот класс представляет категорию в базе данных и включает в себя
 * информацию о подкатегориях и родительской категории. Класс
 * аннотирован для использования с JPA для управления персистентностью.
 */
@Entity
@Table(name = "treemanager")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentId;

    @OneToMany(mappedBy = "parentId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subcategories = new ArrayList<>();
    /**
     * Конструктор для создания категории с указанным именем.
     *
     * @param name имя категории
     */
    public Category(String name) {
        this.name = name;
    }
    /**
     * Конструктор по умолчанию.
     */
    public Category() {}
}

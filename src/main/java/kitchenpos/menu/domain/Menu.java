package kitchenpos.menu.domain;

import kitchenpos.common.vo.Price;
import kitchenpos.exception.InvalidMenuPriceException;
import kitchenpos.menugroup.domain.MenuGroup;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Price price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_group_id", foreignKey = @ForeignKey(name = "fk_menu_menu_group"))
    private MenuGroup menuGroup;

    @Embedded
    private MenuProducts menuProducts;

    protected Menu() {}

    private Menu(
            final String name,
            final Price price,
            final MenuGroup menuGroup,
            final MenuProducts menuProducts
    ) {
        this.name = name;
        this.price = price;
        this.menuGroup = menuGroup;
        this.menuProducts = menuProducts;
    }

    public static Menu of(
            final String name,
            final Price price,
            final MenuGroup menuGroup,
            final MenuProducts menuProducts
    ) {
        final Menu menu = new Menu(name, price, menuGroup, menuProducts);

        validateMenuPrice(menuProducts, price);
        menuProducts.addMenuProducts(menu);

        return menu;
    }

    private static void validateMenuPrice(final MenuProducts menuProducts, final Price price) {
        final Price totalPrice = menuProducts.calculateTotalPrice();

        if (price.compareTo(totalPrice)) {
            throw new InvalidMenuPriceException("메뉴 가격이 상품들의 가격 합보다 클 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price.getValue();
    }

    public MenuGroup getMenuGroup() {
        return menuGroup;
    }

    public List<MenuProduct> getMenuProducts() {
        return menuProducts.getMenuProducts();
    }

    public void updateMenuProducts(final MenuProducts menuProducts) {
        this.menuProducts = menuProducts;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Menu menu = (Menu) o;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Menu{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", price=" + price +
               '}';
    }
}

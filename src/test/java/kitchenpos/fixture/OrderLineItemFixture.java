package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class OrderLineItemFixture {

    private static final long DEFAULT_QUANTITY = 1L;

    public static OrderLineItem 주문_상품_생성(final Order order, final Menu menu) {
        return new OrderLineItem(order, menu, DEFAULT_QUANTITY);
    }

    public static List<OrderLineItem> 주문_상품들_생성(final Order order, final List<Menu> menus) {
        final List<OrderLineItem> 주문_상품들 = new ArrayList<>();

        for (Menu menu : menus) {
            주문_상품들.add(주문_상품_생성(order, menu));
        }

        return 주문_상품들;
    }
}
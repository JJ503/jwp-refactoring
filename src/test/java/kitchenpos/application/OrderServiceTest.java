package kitchenpos.application;

import kitchenpos.application.fixture.OrderServiceFixture;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class OrderServiceTest extends OrderServiceFixture {

    @InjectMocks
    OrderService orderService;

    @Mock
    MenuDao menuDao;

    @Mock
    OrderDao orderDao;

    @Mock
    OrderLineItemDao orderLineItemDao;

    @Mock
    OrderTableDao orderTableDao;

    @Test
    void 주문을_등록한다() {
        // given
        given(menuDao.countByIdIn(anyList())).willReturn(Long.valueOf(주문_항목들.size()));
        given(orderTableDao.findById(anyLong())).willReturn(Optional.ofNullable(주문_테이블));
        given(orderDao.save(any(Order.class))).willReturn(저장된_주문);
        given(orderLineItemDao.save(any(OrderLineItem.class))).willReturn(주문_항목들.get(0))
                                                              .willReturn(주문_항목들.get(1));

        final Order order = new Order(주문_테이블.getId(), 주문_상태, LocalDateTime.now(), 주문_항목들);

        // when
        final Order actual = orderService.create(order);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getId()).isPositive();
            softAssertions.assertThat(actual).usingRecursiveComparison().isEqualTo(저장된_주문);
        });
    }

    @Test
    void 주문_등록시_저장된_메뉴의_개수가_다르다면_예외를_반환한다() {
        // given
        given(menuDao.countByIdIn(anyList())).willReturn(주문_항목_수와_다른_개수);

        final Order order = new Order(주문_테이블.getId(), 주문_상태, LocalDateTime.now(), 주문_항목들);

        // when & then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_등록시_저장되지_않은_주문_테이블을_갖는다면_예외를_반환한다() {
        // given
        given(menuDao.countByIdIn(anyList())).willReturn(Long.valueOf(주문_항목들.size()));
        given(orderTableDao.findById(anyLong())).willReturn(Optional.empty());

        final Order order = new Order(존재하지_않는_주문_테이블_아이디, 주문_상태, LocalDateTime.now(), 주문_항목들);

        // when & then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_목록을_조회한다() {
        // given
        given(orderDao.findAll()).willReturn(저장된_주문들);

        // when
        final List<Order> actual = orderService.list();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).hasSize(2);
            softAssertions.assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(저장된_주문1);
            softAssertions.assertThat(actual.get(1)).usingRecursiveComparison().isEqualTo(저장된_주문2);
        });
    }
}

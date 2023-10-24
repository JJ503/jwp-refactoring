package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.repository.ProductRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 상품을_등록한다() {
        // given
        final Product product = ProductFixture.상품_생성();

        // when
        final Product actual = productService.create(product);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getId()).isPositive();
            softAssertions.assertThat(actual).usingRecursiveComparison()
                          .isEqualTo(product);
        });
    }

    @ParameterizedTest
    @NullSource
    void 상품_등록시_가격이_null이라면_예외를_반환한다(BigDecimal price) {
        // given
        final Product product = new Product(ProductFixture.상품명, price);

        // when & then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_등록시_가격이_음수라면_예외를_반환한다() {
        // given
        final BigDecimal negative_price = BigDecimal.valueOf(-10_000);
        final Product product = new Product(ProductFixture.상품명, negative_price);

        // when & then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_목록을_조회한다() {
        // given
        final List<Product> products = productRepository.saveAll(ProductFixture.상품들_생성(3));

        // when
        final List<Product> actual = productService.list();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).hasSize(2);
            softAssertions.assertThat(actual.get(0)).usingRecursiveComparison()
                          .isEqualTo(products.get(0));
            softAssertions.assertThat(actual.get(1)).usingRecursiveComparison()
                          .isEqualTo(products.get(1));
            softAssertions.assertThat(actual.get(2)).usingRecursiveComparison()
                          .isEqualTo(products.get(2));
        });
    }
}

package tesoreria.compras.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedResponseTest {

    @Test
    void exposesAllFields() {
        var page = new PaginatedResponse<>(List.of("a", "b"), 12L, 2, 1, 10);

        assertThat(page.data()).containsExactly("a", "b");
        assertThat(page.totalElements()).isEqualTo(12L);
        assertThat(page.totalPages()).isEqualTo(2);
        assertThat(page.currentPage()).isEqualTo(1);
        assertThat(page.pageSize()).isEqualTo(10);
    }
}

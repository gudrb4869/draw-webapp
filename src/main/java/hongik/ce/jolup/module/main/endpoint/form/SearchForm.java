package hongik.ce.jolup.module.main.endpoint.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchForm {
    private String category;
    private String keyword;
}

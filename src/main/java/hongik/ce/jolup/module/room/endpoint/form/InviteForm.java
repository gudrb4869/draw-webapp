package hongik.ce.jolup.module.room.endpoint.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class InviteForm {
    @Min(1)
    @Max(100)
    private Integer count = 1;

    private List<@NotBlank String> names = new ArrayList<>(Arrays.asList(""));
}

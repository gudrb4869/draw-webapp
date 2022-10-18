package hongik.ce.jolup.module.room.endpoint.form;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class InviteForm {
    private Set<Long> members = new HashSet<>();
}

package hongik.ce.jolup.module.room.endpoint.form;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RoomForm {

    @NotBlank
    @Size(max = 50)
    private String title;

    @NotNull
    private Boolean access;

    @NotBlank
    @Size(max = 50)
    private String shortDescription;
}

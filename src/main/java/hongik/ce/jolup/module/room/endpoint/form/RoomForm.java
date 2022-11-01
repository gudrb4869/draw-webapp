package hongik.ce.jolup.module.room.endpoint.form;

import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class RoomForm {

    @NotBlank
    @Size(max = 50)
    private String title;

    private boolean revealed = true;

    @NotBlank
    @Size(max = 50)
    private String shortDescription;

    public static RoomForm from(Room room) {
        RoomForm roomForm = new RoomForm();
        roomForm.title = room.getTitle();
        roomForm.revealed = room.isRevealed();
        roomForm.shortDescription = room.getShortDescription();
        return roomForm;
    }
}

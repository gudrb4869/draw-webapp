package hongik.ce.jolup.module.room.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.UserMember;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "title", "access"})
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NamedEntityGraph(
        name ="Room.withJoinsAndMembers",
        attributeNodes = {
                @NamedAttributeNode(value = "joins", subgraph = "member")
        },
        subgraphs = @NamedSubgraph(name = "member", attributeNodes = @NamedAttributeNode("member"))
)
@NamedEntityGraph(
        name ="Room.withJoins",
        attributeNodes = @NamedAttributeNode("joins")
)
public class Room extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String shortDescription;

    private boolean revealed;

    private Integer count = 0;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Competition> competitions = new ArrayList<>();

    public static Room from(RoomForm roomForm) {
        Room room = new Room();
        room.title = roomForm.getTitle();
        room.revealed = roomForm.isRevealed();
        room.shortDescription = roomForm.getShortDescription();
        room.addCount();
        return room;
    }

    public boolean isMember(UserMember userMember) {
        Join join = this.joins.stream()
                .filter(j -> j.getMember().equals(userMember.getMember()))
                .findFirst().orElse(null);
        return join != null;
    }

    public boolean isAdmin(UserMember userMember) {
        Join join = this.joins.stream()
                .filter(j -> j.getMember().equals(userMember.getMember()))
                .findFirst().orElse(null);
        return join != null && join.getGrade().equals(Grade.ADMIN);
    }

    public boolean isPublic() {
        return this.revealed;
    }

    public void addCount() {
        this.count++;
    }

    public void subCount() {
        this.count--;
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateAccess(boolean access) {
        this.revealed = access;
    }

    public void reveal() {
        if (this.revealed) {
            throw new IllegalStateException("이미 공개된 방입니다.");
        }
        this.revealed = true;
    }

    public void conceal() {
        if (!this.revealed) {
            throw new IllegalStateException("이미 비공개된 방입니다.");
        }
        this.revealed = false;
    }

    public boolean isRemovable() {
        return true;
    }

    public void updateFrom(RoomForm roomForm) {
        this.title = roomForm.getTitle();
        this.revealed = roomForm.isRevealed();
        this.shortDescription = roomForm.getShortDescription();
    }
}

package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // todo : wrapped class로 만들어서 글자 수 제한
    @Column(nullable = false, length = 300)
    private String content;

    private LocalDate startDate;

    private LocalDate endDate;

    public GoalRoomToDo(final GoalRoomToDo goalRoomToDo) {
        this(goalRoomToDo.id, goalRoomToDo.content, goalRoomToDo.startDate, goalRoomToDo.endDate);
    }

    public GoalRoomToDo(final String content, final LocalDate startDate, final LocalDate endDate) {
        this(null, content, startDate, endDate);
    }

    private GoalRoomToDo(final Long id, final String content, final LocalDate startDate, final LocalDate endDate) {
        this.id = id;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

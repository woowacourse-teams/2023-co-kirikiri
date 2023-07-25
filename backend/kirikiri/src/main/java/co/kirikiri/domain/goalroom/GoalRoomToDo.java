package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.Period;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private GoalRoomTodoContent content;

    @Embedded
    private Period period;

    public GoalRoomToDo(final GoalRoomTodoContent content, final Period period) {
        this(null, content, period);
    }

    public GoalRoomToDo(final Long id, final GoalRoomTodoContent content, final Period period) {
        this.id = id;
        this.content = content;
        this.period = period;
    }
}

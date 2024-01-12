package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseUpdatedTimeEntity;
import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.Period;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDo extends BaseUpdatedTimeEntity {

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

    public boolean isSameId(final Long todoId) {
        return this.id.equals(todoId);
    }

    public String getContent() {
        return content.getValue();
    }

    public LocalDate getStartDate() {
        return period.getStartDate();
    }

    public LocalDate getEndDate() {
        return period.getEndDate();
    }
}

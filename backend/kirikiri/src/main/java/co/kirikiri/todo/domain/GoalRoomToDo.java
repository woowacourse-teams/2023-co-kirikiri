package co.kirikiri.todo.domain;

import co.kirikiri.common.entity.BaseUpdatedTimeEntity;
import co.kirikiri.todo.domain.vo.GoalRoomTodoContent;
import co.kirikiri.todo.domain.vo.ToDoPeriod;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDo extends BaseUpdatedTimeEntity {

    private Long goalRoomId;

    @Embedded
    private GoalRoomTodoContent content;

    @Embedded
    private ToDoPeriod period;

    public GoalRoomToDo(final Long goalRoomId, final GoalRoomTodoContent content, final ToDoPeriod period) {
        this(null, goalRoomId, content, period);
    }

    public GoalRoomToDo(final Long id, final Long goalRoomId, final GoalRoomTodoContent content,
                        final ToDoPeriod period) {
        this.id = id;
        this.goalRoomId = goalRoomId;
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

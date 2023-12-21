package co.kirikiri.todo.persistence;

import co.kirikiri.todo.domain.GoalRoomToDo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRoomToDoRepository extends JpaRepository<GoalRoomToDo, Long> {

    List<GoalRoomToDo> findGoalRoomToDosByGoalRoomId(final Long goalRoomId);
}

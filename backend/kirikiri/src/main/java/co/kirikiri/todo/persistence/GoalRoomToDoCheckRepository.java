package co.kirikiri.todo.persistence;

import co.kirikiri.todo.domain.GoalRoomToDo;
import co.kirikiri.todo.domain.GoalRoomToDoCheck;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRoomToDoCheckRepository extends JpaRepository<GoalRoomToDoCheck, Long> {

    @Query("select gc from GoalRoomToDoCheck gc "
            + "where gc.goalRoomToDo = :goalRoomToDo "
            + "and gc.goalRoomMemberId = :goalRoomMemberId ")
    Optional<GoalRoomToDoCheck> findByGoalRoomTodoAndGoalRoomMemberId(
            @Param("goalRoomToDo") final GoalRoomToDo goalRoomToDo,
            @Param("goalRoomMemberId") final Long goalRoomMemberId);

    @Query("select gc from GoalRoomToDoCheck gc "
            + "inner join fetch gc.goalRoomToDo gt "
            + "where gc.goalRoomMemberId = :goalRoomMemberId "
            + "and gt.goalRoomId = :goalRoomId ")
    List<GoalRoomToDoCheck> findByGoalRoomIdAndGoalRoomMemberId(
            @Param("goalRoomId") final Long goalRoomId,
            @Param("goalRoomMemberId") final Long goalRoomMemberId);

    @Modifying
    @Query("delete from GoalRoomToDoCheck gc "
            + "where gc.goalRoomMemberId = :goalRoomMemberId "
            + "and gc.goalRoomToDo.id = :todoId")
    void deleteByGoalRoomMemberIdAndToDoId(@Param("goalRoomMemberId") final Long goalRoomMemberId,
                                           @Param("todoId") final Long todoId);
}

package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomToDo;
import co.kirikiri.domain.goalroom.GoalRoomToDoCheck;
import co.kirikiri.domain.member.vo.Identifier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRoomToDoCheckRepository extends JpaRepository<GoalRoomToDoCheck, Long> {

    @Query("select gc from GoalRoomToDoCheck gc "
            + "inner join fetch gc.goalRoomMember gcm "
            + "inner join fetch gcm.member m "
            + "inner join fetch gcm.goalRoom g "
            + "where m.identifier = :identifier "
            + "and gc.goalRoomToDo = :goalRoomTodo "
            + "and g.id = :goalRoomId")
    Optional<GoalRoomToDoCheck> findByGoalRoomIdAndTodoAndMemberIdentifier(
            @Param("goalRoomId") final Long goalRoomId,
            @Param("goalRoomTodo") final GoalRoomToDo goalRoomToDo,
            @Param("identifier") final Identifier identifier);

    @Query("select gc from GoalRoomToDoCheck gc "
            + "inner join fetch gc.goalRoomMember gcm "
            + "inner join fetch gcm.member m "
            + "inner join fetch gcm.goalRoom g "
            + "where m.identifier = :identifier "
            + "and g.id = :goalRoomId ")
    List<GoalRoomToDoCheck> findByGoalRoomIdAndMemberIdentifier(
            @Param("goalRoomId") final Long goalRoomId,
            @Param("identifier") final Identifier identifier);

    @Modifying
    @Query("delete from GoalRoomToDoCheck gc "
            + "where gc.goalRoomMember = :goalRoomMember "
            + "and gc.goalRoomToDo.id = :todoId")
    void deleteByGoalRoomMemberAndToDoId(@Param("goalRoomMember") final GoalRoomMember goalRoomMember,
                                         @Param("todoId") final Long todoId);
}

package co.kirikiri.domain.goalroom;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDos {

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", updatable = false)
    private List<GoalRoomToDo> values = new ArrayList<>();

    public GoalRoomToDos(final List<GoalRoomToDo> values) {
        this.values.addAll(new ArrayList<>(values));
    }

    public void add(final GoalRoomToDo goalRoomToDo) {
        values.add(new GoalRoomToDo(goalRoomToDo));
    }
}

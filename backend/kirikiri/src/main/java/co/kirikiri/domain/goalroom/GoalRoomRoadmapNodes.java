package co.kirikiri.domain.goalroom;

import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomRoadmapNodes {

    private static final int DATE_OFFSET = 1;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JoinColumn(name = "goal_room_id", nullable = false, updatable = false)
    private final List<GoalRoomRoadmapNode> values = new ArrayList<>();

    public GoalRoomRoadmapNodes(final List<GoalRoomRoadmapNode> values) {
        final List<GoalRoomRoadmapNode> copiedValues = new ArrayList<>(values);
        validatePeriodNoOverlap(copiedValues);
        this.values.addAll(copiedValues);
    }

    public void validatePeriodNoOverlap(final List<GoalRoomRoadmapNode> nodes) {
        sortByStartDateAsc(nodes);

        IntStream.range(0, nodes.size() - 1)
                .filter(index -> nodes.get(index).isEndDateEqualOrAfterOtherStartDate(nodes.get(index + 1)))
                .findAny()
                .ifPresent(it -> {
                    throw new BadRequestException("골룸 노드의 기간이 겹칠 수 없습니다.");
                });
    }

    private void sortByStartDateAsc(final List<GoalRoomRoadmapNode> nodes) {
        nodes.sort(Comparator.comparing(GoalRoomRoadmapNode::getStartDate));
    }

    public void addAll(final GoalRoomRoadmapNodes goalRoomRoadmapNodes) {
        this.values.addAll(new ArrayList<>(goalRoomRoadmapNodes.values));
    }

    public LocalDate getGoalRoomStartDate() {
        return values.stream()
                .min(Comparator.comparing(GoalRoomRoadmapNode::getStartDate))
                .orElseThrow(() -> new NotFoundException("골룸에 노드가 존재하지 않습니다."))
                .getStartDate();
    }

    public LocalDate getGoalRoomEndDate() {
        return values.stream()
                .max(Comparator.comparing(GoalRoomRoadmapNode::getEndDate))
                .orElseThrow(() -> new NotFoundException("골룸에 노드가 존재하지 않습니다."))
                .getEndDate();
    }

    public int size() {
        return values.size();
    }

    public List<GoalRoomRoadmapNode> getValues() {
        return new ArrayList<>(values);
    }
}

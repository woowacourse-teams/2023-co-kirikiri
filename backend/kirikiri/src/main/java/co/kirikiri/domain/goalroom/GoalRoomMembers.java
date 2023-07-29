package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ServerException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomMembers {

    private static final int MIN_SIZE_TO_FIND_NEXT_LEADER = 1;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, mappedBy = "goalRoom")
    private List<GoalRoomMember> values = new ArrayList<>();

    public GoalRoomMembers(final List<GoalRoomMember> values) {
        this.values.addAll(new ArrayList<>(values));
    }

    public void add(final GoalRoomMember goalRoomMember) {
        this.values.add(goalRoomMember);
    }

    public GoalRoomMember findByMember(final Member member) {
        return values.stream()
                .filter(value -> value.isSameMemberWith(member))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("골룸에 참여한 사용자가 아닙니다. memberId = " + member.getId()));
    }

    public GoalRoomMember findNextLeader() {
        if (size() <= MIN_SIZE_TO_FIND_NEXT_LEADER) {
            throw new ServerException(
                    String.format("골룸 참여자가 %d명 이하이므로 다음 리더를 찾을 수 없습니다.", MIN_SIZE_TO_FIND_NEXT_LEADER));
        }
        values.sort(Comparator.comparing(GoalRoomMember::getJoinedAt));
        return values.get(1);
    }

    public int size() {
        return values.size();
    }

    public void remove(final GoalRoomMember goalRoomMember) {
        values.remove(goalRoomMember);
    }

    public List<GoalRoomMember> getValues() {
        return values;
    }
}

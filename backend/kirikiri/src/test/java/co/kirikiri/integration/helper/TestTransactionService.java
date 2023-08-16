package co.kirikiri.integration.helper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestTransactionService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    public TestTransactionService(final GoalRoomRepository goalRoomRepository,
                                  final GoalRoomMemberRepository goalRoomMemberRepository) {
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    public GoalRoom 골룸을_완료시킨다(final Long 골룸_아이디) {
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        return goalRoomRepository.save(골룸);
    }

    public void 골룸_멤버를_저장한다(final List<GoalRoomMember> 골룸_멤버_리스트) {
        goalRoomMemberRepository.saveAll(골룸_멤버_리스트);
    }
}

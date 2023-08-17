package co.kirikiri.integration.helper;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestTransactionService {

    @PersistenceContext
    private EntityManager em;

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

    public void 골룸의_상태와_종료날짜를_변경한다(final Long 골룸_아이디, final GoalRoomStatus 골룸_상태, final LocalDate 변경할_종료날짜) {
        em.createQuery("update GoalRoom g set endDate = :endDate, status = :status where id = :goalRoomId")
                .setParameter("status", 골룸_상태)
                .setParameter("endDate", 변경할_종료날짜)
                .setParameter("goalRoomId", 골룸_아이디)
                .executeUpdate();
    }

    public void 골룸의_종료날짜를_변경한다(final Long 골룸_아이디, final LocalDate 변경할_종료날짜) {
        em.createQuery("update GoalRoom g set endDate = :endDate where id = :goalRoomId")
                .setParameter("goalRoomId", 골룸_아이디)
                .setParameter("endDate", 변경할_종료날짜)
                .executeUpdate();
    }

    public void 골룸의_시작날짜를_변경한다(final Long 골룸_아이디, final LocalDate 변경할_시작날짜) {
        em.createQuery("update GoalRoom g set startDate = :startDate where id = :goalRoomId")
                .setParameter("goalRoomId", 골룸_아이디)
                .setParameter("startDate", 변경할_시작날짜)
                .executeUpdate();
    }
}

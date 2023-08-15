package co.kirikiri.integration.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IntegrationTestHelper {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void 골룸을_완료상태로_변경하고_종료날짜를_변경한다(final Long 골룸_아이디, final LocalDate 변경할_종료날짜) {
        em.createQuery("update GoalRoom g set endDate = :endDate, status = 'COMPLETED' where id = :goalRoomId")
                .setParameter("goalRoomId", 골룸_아이디)
                .setParameter("endDate", 변경할_종료날짜)
                .executeUpdate();
    }

    @Transactional
    public void 골룸의_종료날짜를_변경한다(final Long 골룸_아이디, final LocalDate 변경할_종료날짜) {
        em.createQuery("update GoalRoom g set endDate = :endDate where id = :goalRoomId")
                .setParameter("goalRoomId", 골룸_아이디)
                .setParameter("endDate", 변경할_종료날짜)
                .executeUpdate();
    }

    @Transactional
    public void 골룸의_시작날짜를_변경한다(final Long 골룸_아이디, final LocalDate 변경할_시작날짜) {
        em.createQuery("update GoalRoom g set startDate = :startDate where id = :goalRoomId")
                .setParameter("goalRoomId", 골룸_아이디)
                .setParameter("startDate", 변경할_시작날짜)
                .executeUpdate();
    }
}
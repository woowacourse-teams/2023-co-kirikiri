package co.kirikiri.integration.helper;

import co.kirikiri.domain.goalroom.GoalRoomStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestTransactionQuery {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void 골룸의_상태와_종료날짜를_변경한다(final Long 골룸_아이디, final GoalRoomStatus 골룸_상태, final LocalDate 변경할_종료날짜) {
        em.createQuery("update GoalRoom g set endDate = :endDate, status = :status where id = :goalRoomId")
                .setParameter("status", 골룸_상태)
                .setParameter("endDate", 변경할_종료날짜)
                .setParameter("goalRoomId", 골룸_아이디)
                .executeUpdate();
    }
}




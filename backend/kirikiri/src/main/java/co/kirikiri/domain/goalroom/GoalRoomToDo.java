package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.BaseCreatedTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalRoomToDo extends BaseCreatedTimeEntity {

    @Column(nullable = false, length = 300)
    private String content;

    private LocalDate startDate;

    private LocalDate endDate;
}

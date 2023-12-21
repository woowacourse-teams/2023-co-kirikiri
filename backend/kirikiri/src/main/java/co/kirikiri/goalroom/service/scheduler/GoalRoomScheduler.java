package co.kirikiri.goalroom.service.scheduler;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.entity.BaseEntity;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomScheduler {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void startGoalRooms() {
        final List<GoalRoom> goalRoomsToStart = goalRoomRepository.findAllRecruitingGoalRoomsByStartDateEarlierThan(
                LocalDate.now());
        for (final GoalRoom goalRoom : goalRoomsToStart) {
            final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoom.getGoalRoomPendingMembers().getValues();
            saveGoalRoomMemberFromPendingMembers(goalRoomPendingMembers);
            goalRoom.start();
        }
    }

    private void saveGoalRoomMemberFromPendingMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
        goalRoomMemberRepository.saveAllInBatch(goalRoomMembers);
        final List<Long> ids = makeGoalRoomPendingMemberIds(goalRoomPendingMembers);
        goalRoomPendingMemberRepository.deleteAllByIdIn(ids);
    }

    private List<GoalRoomMember> makeGoalRoomMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMember)
                .toList();
    }

    private GoalRoomMember makeGoalRoomMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return new GoalRoomMember(goalRoomPendingMember.getRole(), goalRoomPendingMember.getJoinedAt(),
                goalRoomPendingMember.getGoalRoom(), goalRoomPendingMember.getMemberId());
    }

    private List<Long> makeGoalRoomPendingMemberIds(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(BaseEntity::getId)
                .toList();
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void endGoalRooms() {
        final List<GoalRoom> goalRoomsToEnd = goalRoomRepository.findAllByEndDate(LocalDate.now().minusDays(1));
        goalRoomsToEnd.forEach(GoalRoom::complete);
    }
}

package co.kirikiri.service.scheduler;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.aop.ExceptionConvert;
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
                goalRoomPendingMember.getGoalRoom(), goalRoomPendingMember.getMember());
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

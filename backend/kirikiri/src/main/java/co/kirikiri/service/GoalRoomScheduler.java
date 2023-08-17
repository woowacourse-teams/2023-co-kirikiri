package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class GoalRoomScheduler {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void startGoalRooms() {
        final List<GoalRoom> goalRoomsToStart = goalRoomRepository.findAllByStartDate(LocalDate.now());
        for (final GoalRoom goalRoom : goalRoomsToStart) {
            final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findAllByGoalRoom(
                    goalRoom);
            final List<GoalRoomMember> goalRoomMembers = makeGoalRoomMembers(goalRoomPendingMembers);
            goalRoomMemberRepository.saveAll(goalRoomMembers);
            goalRoomPendingMemberRepository.deleteAll(goalRoomPendingMembers);
            goalRoom.start();
        }
    }

    private List<GoalRoomMember> makeGoalRoomMembers(final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMember)
                .toList();
    }

    private GoalRoomMember makeGoalRoomMember(final GoalRoomPendingMember goalRoomPendingMember) {
        return new GoalRoomMember(goalRoomPendingMember.getRole(),
                goalRoomPendingMember.getJoinedAt(), goalRoomPendingMember.getGoalRoom(),
                goalRoomPendingMember.getMember());
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void endGoalRooms() {
        final List<GoalRoom> goalRoomsToEnd = goalRoomRepository.findAllByEndDate(LocalDate.now().minusDays(1));
        goalRoomsToEnd.forEach(GoalRoom::complete);
    }
}

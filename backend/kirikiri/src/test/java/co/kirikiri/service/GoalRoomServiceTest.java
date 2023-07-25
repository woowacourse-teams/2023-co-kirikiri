package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class GoalRoomServiceTest {

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @InjectMocks
    private GoalRoomService goalRoomService;

    @Test
    void 골룸_목록을_조회한다() {
        // given
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(roadmapNode1, roadmapNode2));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);

        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 7, 10), roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 11),
                LocalDate.of(2023, 7, 20), roadmapNode2);
        final GoalRoomRoadmapNode goalRoomRoadmapNode3 = new GoalRoomRoadmapNode(LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 10), roadmapNode1);
        final GoalRoomRoadmapNode goalRoomRoadmapNode4 = new GoalRoomRoadmapNode(LocalDate.of(2023, 8, 11),
                LocalDate.of(2023, 8, 20), roadmapNode2);

        final GoalRoom goalRoom1 = new GoalRoom(1L, "goalroom1", 10, GoalRoomStatus.RECRUITING,
                roadmapContent);
        goalRoom1.addRoadmapNodesAll(new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2)));
        final Member member1 = 사용자를_생성한다(1L);
        goalRoom1.joinGoalRoom(GoalRoomRole.LEADER, member1);

        final GoalRoom goalRoom2 = new GoalRoom(2L, "goalroom2", 10, GoalRoomStatus.RECRUITING,
                roadmapContent);
        goalRoom2.addRoadmapNodesAll(new GoalRoomRoadmapNodes(List.of(goalRoomRoadmapNode3, goalRoomRoadmapNode4)));
        final Member member2 = 사용자를_생성한다(2L);
        goalRoom2.joinGoalRoom(GoalRoomRole.LEADER, member2);

        final PageImpl<GoalRoom> goalRoomsPage = new PageImpl<>(List.of(goalRoom2, goalRoom1), PageRequest.of(1, 10),
                2);
        given(goalRoomRepository.findGoalRoomsWithPendingMembersPageByCond(any(), any()))
                .willReturn(goalRoomsPage);

        // when
        final PageResponse<GoalRoomForListResponse> result = goalRoomService.findGoalRoomsByFilterType(
                GoalRoomFilterTypeDto.LATEST, new CustomPageRequest(1, 10));

        final PageResponse<GoalRoomForListResponse> expected = new PageResponse<>(1, 2,
                List.of(
                        new GoalRoomForListResponse(2L, "goalroom2", 1, 10, LocalDateTime.now(),
                                LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 20),
                                new MemberResponse(member2.getId(), member2.getNickname().getValue())),
                        new GoalRoomForListResponse(1L, "goalroom1", 1, 10, LocalDateTime.now(),
                                LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 20),
                                new MemberResponse(member1.getId(), member1.getNickname().getValue()))
                )
        );

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("data.createdAt")
                .isEqualTo(expected);
    }

    private Member 사용자를_생성한다(final Long id) {
        return new Member(id, new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2000, 7, 20), new Nickname("name1"),
                        "010-1111-1111"));
    }
}

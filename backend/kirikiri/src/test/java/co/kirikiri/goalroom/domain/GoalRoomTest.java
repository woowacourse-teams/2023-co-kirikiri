package co.kirikiri.goalroom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import co.kirikiri.goalroom.domain.vo.Period;
import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodeImages;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomTest {

    private static final GoalRoomName GOAL_ROOM_NAME = new GoalRoomName("골룸 이름");
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    @Test
    void 골룸의_총_기간을_계산한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmap.getId(), roadmapNodes);

        final GoalRoom goalRoom = 골룸을_생성한다(roadmapContent, creator);

        // when
        final int totalPeriod = goalRoom.calculateTotalPeriod();

        // then
        assertThat(totalPeriod).isEqualTo(31);
    }

    @Test
    void 골룸의_총_인증_횟수를_구한다() {
        //given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmap.getId(), roadmapNodes);

        final GoalRoom goalRoom = 골룸을_생성한다(roadmapContent, creator);

        //expect
        assertThat(goalRoom.getAllCheckCount()).isEqualTo(20);
    }

    @Test
    void 골룸이_종료된지_3개월_이상_지나지_않으면_false를_반환한다() {
        //given
        final Member creator = 크리에이터를_생성한다();
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmap.getId(), roadmapNodes);

        final GoalRoom goalRoom = 골룸을_생성한다(roadmapContent, creator);

        // when
        final boolean result = goalRoom.isCompletedAfterMonths(3);

        // then
        assertThat(result).isEqualTo(false);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri1@email.com");
        return new Member(1L, new Identifier("cokirikiri"), null,
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), null, memberProfile);
    }

    private Roadmap 로드맵을_생성한다(final Member creator) {
        final RoadmapCategory category = new RoadmapCategory("게임");
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator.getId(),
                category, null);
        return roadmap;
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode(1L, "로드맵 1주차", "로드맵 1주차 내용");
        roadmapNode1.addImages(new RoadmapNodeImages(Collections.emptyList()));
        final RoadmapNode roadmapNode2 = new RoadmapNode(2L, "로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final Long roadmapId, final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent(1L, "로드맵 본문", roadmapId,
                new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent, final Member creator) {
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TODAY, TEN_DAY_LATER), 10, firstRoadmapNode.getId());

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER), 10, secondRoadmapNode.getId());

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));

        return new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), roadmapContent.getId(),
                goalRoomRoadmapNodes);
    }
}

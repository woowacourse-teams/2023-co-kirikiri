package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapContents;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImages;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

class GoalRoomTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private static Member member;
    private static MemberProfile memberProfile;

    @BeforeAll
    static void setUp() {
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        memberProfile = new MemberProfile(Gender.MALE, LocalDate.now(), phoneNumber);
        member = new Member(identifier, encryptedPassword, nickname, memberProfile);
    }

    @Test
    void 정상적으로_골룸에_참여한다() {
        //given
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member);
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalRoomName"), new LimitedMemberCount(20), new RoadmapContent("content"));

        //when
        //then
        assertDoesNotThrow(() -> goalRoom.participate(goalRoomPendingMember));
    }

    @Test
    void 골룸_참여_시_정원이_다_찼을때_예외를_던진다() {
        //given
        final Identifier identifier = new Identifier("identifier2");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        final Member member2 = new Member(identifier, encryptedPassword, nickname, new MemberProfile(Gender.MALE, LocalDate.now(), phoneNumber));
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member2);

        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalRoomName"), new LimitedMemberCount(1), new RoadmapContent("content"));
        goalRoom.participate(goalRoomPendingMember2);

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member);

        //when
        //then
        assertThatThrownBy(() -> goalRoom.participate(goalRoomPendingMember))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("정원 초과입니다.");
    }

    @Test
    void 골룸_참여_시_이미_참여한_사람이_참여할_때_예외를_던진다() {
        //given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalRoomName"), new LimitedMemberCount(20), new RoadmapContent("content"));
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, member);
        goalRoom.participate(goalRoomPendingMember);

        //when
        //then
        assertThatThrownBy(() -> goalRoom.participate(goalRoomPendingMember))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("이미 참여 중인 상태입니다.");
    }

    @Test
    void 골룸의_총_기간을_계산한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final Roadmap roadmap = 로드맵을_생성한다(creator);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);
        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent);

        // when
        final int totalPeriod = goalRoom.calculateTotalPeriod();

        // then
        assertThat(totalPeriod)
                .isSameAs(31);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        return new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
    }

    private Roadmap 로드맵을_생성한다(final Member creator) {
        final RoadmapCategory category = new RoadmapCategory("게임");
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(roadmapContent);
        return roadmap;
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        roadmapNode1.addImages(new RoadmapNodeImages(Collections.emptyList()));
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10), roadmapContent);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TODAY, TEN_DAY_LATER), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER), 10, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoom;
    }
}

package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodeImage;
import co.kirikiri.roadmap.domain.RoadmapNodeImages;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import co.kirikiri.roadmap.domain.RoadmapTags;
import co.kirikiri.roadmap.persistence.RoadmapCategoryRepository;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class GoalRoomPendingMemberRepositoryTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private final MemberRepository memberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    public GoalRoomPendingMemberRepositoryTest(final MemberRepository memberRepository,
                                               final RoadmapCategoryRepository roadmapCategoryRepository,
                                               final RoadmapRepository roadmapRepository,
                                               final RoadmapContentRepository roadmapContentRepository,
                                               final GoalRoomRepository goalRoomRepository,
                                               final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository) {
        this.memberRepository = memberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapContentRepository = roadmapContentRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomPendingMemberRepository = goalRoomPendingMemberRepository;
    }

    @Test
    void 골룸과_사용자_아이디로_골룸_사용자_대기_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_생성한다("여행");
        final Roadmap roadmap = 로드맵을_저장한다("title", creator, category);
        final RoadmapContent roadmapContent = 로드맵_본문을_저장한다(roadmap.getId());
        final GoalRoom goalRoom = 골룸을_저장한다(roadmapContent, creator);

        final GoalRoomPendingMember expected = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), goalRoom, creator);

        // when
        final Optional<GoalRoomPendingMember> findGoalRoomPendingMember = goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(
                goalRoom, new Identifier("cokirikiri"));

        // then
        assertThat(findGoalRoomPendingMember.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "joinedAt")
                .isEqualTo(expected);
    }

    @Test
    void 골룸과_사용자_아이디로_골룸_사용자_대기_목록_조회시_없으면_빈값을_반환한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_생성한다("여행");
        final Roadmap roadmap = 로드맵을_저장한다("title", creator, category);
        final RoadmapContent roadmapContent = 로드맵_본문을_저장한다(roadmap.getId());
        final GoalRoom goalRoom = 골룸을_저장한다(roadmapContent, creator);

        // when
        final Optional<GoalRoomPendingMember> findGoalRoomPendingMember = goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(
                goalRoom, new Identifier("cokirikiri2"));

        // then
        assertThat(findGoalRoomPendingMember)
                .isEmpty();
    }

    @Test
    void 골룸으로_사용자_대기_목록과_멤버를_함께_조회한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_생성한다("여행");
        final Roadmap roadmap = 로드맵을_저장한다("title", creator, category);
        final RoadmapContent roadmapContent = 로드맵_본문을_저장한다(roadmap.getId());
        final GoalRoom goalRoom = 골룸을_저장한다(roadmapContent, creator);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "kirikiri1@email.com");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "kirikiri1@email.com");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "kirikiri1@email.com");

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), goalRoom, creator);
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member2);
        final GoalRoomPendingMember goalRoomPendingMember3 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member3);
        goalRoomPendingMemberRepository.saveAll(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2, goalRoomPendingMember3));

        final List<GoalRoomPendingMember> expected = List.of(goalRoomPendingMember, goalRoomPendingMember1,
                goalRoomPendingMember2, goalRoomPendingMember3);

        // when
        final List<GoalRoomPendingMember> findGoalRoomPendingMembers = goalRoomPendingMemberRepository.findAllByGoalRoom(
                goalRoom);

        // then
        assertThat(findGoalRoomPendingMembers)
                .usingRecursiveComparison()
                .ignoringFields("id", "joinedAt")
                .isEqualTo(expected);
    }

    @Test
    void 골룸에_참가한다() {
        //given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_생성한다("여행");
        final Roadmap roadmap = 로드맵을_저장한다("title", creator, category);
        final RoadmapContent roadmapContent = 로드맵_본문을_저장한다(roadmap.getId());
        final GoalRoom goalRoom = 골룸을_저장한다(roadmapContent, creator);

        final Member follower = 사용자를_생성한다("identifier2", "password!2", "name", "kirikiri1@email.com");

        //when
        goalRoom.join(follower);

        //then
        final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findByGoalRoom(
                goalRoom);

        final List<Member> members = goalRoomPendingMembers.stream()
                .map(GoalRoomPendingMember::getMember)
                .toList();

        Assertions.assertAll(
                () -> assertThat(goalRoomPendingMembers).hasSize(2),
                () -> assertThat(members).contains(follower)
        );
    }

    @Test
    void 골룸_아이디로_골룸_사용자를_조회하고_들어온지_오래된_순서대로_정렬한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_생성한다("여행");
        final Roadmap roadmap = 로드맵을_저장한다("title", creator, category);
        final RoadmapContent roadmapContent = 로드맵_본문을_저장한다(roadmap.getId());
        final GoalRoom goalRoom = 골룸을_저장한다(roadmapContent, creator);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "kirikiri1@email.com");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "kirikiri1@email.com");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "kirikiri1@email.com");

        final GoalRoomPendingMember goalRoomPendingMember0 = goalRoom.getGoalRoomPendingMembers().getValues().get(0);
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member2);
        final GoalRoomPendingMember goalRoomPendingMember3 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member3);
        goalRoomPendingMemberRepository.saveAll(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2, goalRoomPendingMember3));
        final List<GoalRoomPendingMember> expected = List.of(goalRoomPendingMember0, goalRoomPendingMember1,
                goalRoomPendingMember2, goalRoomPendingMember3);

        // when
        final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findByGoalRoomIdOrderedBySortType(
                goalRoom.getId(), GoalRoomMemberSortType.JOINED_ASC);

        // then
        assertThat(goalRoomPendingMembers)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디로_골룸_사용자를_조회하고_마지막으로_들어온_순서대로_정렬한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_생성한다("여행");
        final Roadmap roadmap = 로드맵을_저장한다("title", creator, category);
        final RoadmapContent roadmapContent = 로드맵_본문을_저장한다(roadmap.getId());
        final GoalRoom goalRoom = 골룸을_저장한다(roadmapContent, creator);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "kirikiri1@email.com");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "kirikiri1@email.com");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "kirikiri1@email.com");

        final GoalRoomPendingMember goalRoomPendingMember0 = goalRoom.getGoalRoomPendingMembers().getValues().get(0);
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), goalRoom, member1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member2);
        final GoalRoomPendingMember goalRoomPendingMember3 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member3);
        final GoalRoomPendingMember savedGoalRoomPendingMember1 = goalRoomPendingMemberRepository.save(
                goalRoomPendingMember1);
        final GoalRoomPendingMember savedGoalRoomPendingMember2 = goalRoomPendingMemberRepository.save(
                goalRoomPendingMember2);
        final GoalRoomPendingMember savedGoalRoomPendingMember3 = goalRoomPendingMemberRepository.save(
                goalRoomPendingMember3);
        final List<GoalRoomPendingMember> expected = List.of(savedGoalRoomPendingMember3, savedGoalRoomPendingMember2,
                savedGoalRoomPendingMember1, goalRoomPendingMember0);

        // when
        final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findByGoalRoomIdOrderedBySortType(
                goalRoom.getId(), GoalRoomMemberSortType.JOINED_DESC);

        // then
        assertThat(goalRoomPendingMembers)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디로_골룸_사용자를_조회하고_정렬조건을_달성률순_또는_입력하지_않은경우_참여한순으로_정렬한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_생성한다("여행");
        final Roadmap roadmap = 로드맵을_저장한다("title", creator, category);
        final RoadmapContent roadmapContent = 로드맵_본문을_저장한다(roadmap.getId());
        final GoalRoom goalRoom = 골룸을_저장한다(roadmapContent, creator);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "kirikiri1@email.com");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "kirikiri1@email.com");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "kirikiri1@email.com");

        final GoalRoomPendingMember goalRoomPendingMember0 = goalRoom.getGoalRoomPendingMembers().getValues().get(0);
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), goalRoom, member1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member2);
        final GoalRoomPendingMember goalRoomPendingMember3 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), goalRoom, member3);
        goalRoomPendingMemberRepository.saveAll(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2, goalRoomPendingMember3));
        final List<GoalRoomPendingMember> expected = List.of(goalRoomPendingMember0, goalRoomPendingMember1,
                goalRoomPendingMember2, goalRoomPendingMember3);

        // when
        final List<GoalRoomPendingMember> goalRoomPendingMembers1 = goalRoomPendingMemberRepository.findByGoalRoomIdOrderedBySortType(
                goalRoom.getId(), GoalRoomMemberSortType.ACCOMPLISHMENT_RATE);
        final List<GoalRoomPendingMember> goalRoomPendingMembers2 = goalRoomPendingMemberRepository.findByGoalRoomIdOrderedBySortType(
                goalRoom.getId(), null);

        // then
        assertThat(goalRoomPendingMembers1)
                .isEqualTo(expected);
        assertThat(goalRoomPendingMembers2)
                .isEqualTo(expected);
    }

    private Member 크리에이터를_저장한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri1@email.com");
        final Member creator = new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"),
                new MemberImage("originalFileName", "serverFilePath", ImageContentType.PNG), memberProfile);
        return memberRepository.save(creator);
    }

    private Member 사용자를_생성한다(final String identifier, final String password, final String nickname,
                             final String email) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);
        final Member member = new Member(new Identifier(identifier), new EncryptedPassword(new Password(password)),
                new Nickname(nickname), new MemberImage("originalFileName", "serverFilePath", ImageContentType.PNG),
                memberProfile);
        return memberRepository.save(member);
    }

    private RoadmapCategory 카테고리를_생성한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 로드맵을_저장한다(final String title, final Member creator, final RoadmapCategory category) {
        final Roadmap roadmap = new Roadmap(title, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator.getId(), category, new RoadmapTags(new ArrayList<>()));
        return roadmapRepository.save(roadmap);
    }

    private RoadmapContent 로드맵_본문을_저장한다(final Long roadmapId) {
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문", roadmapId, new RoadmapNodes(roadmapNodes));
        return roadmapContentRepository.save(roadmapContent);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        roadmapNode1.addImages(new RoadmapNodeImages(노드_이미지들을_생성한다()));
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private List<RoadmapNodeImage> 노드_이미지들을_생성한다() {
        return List.of(
                new RoadmapNodeImage("node-image1.png", "node-image1-save-path", ImageContentType.PNG),
                new RoadmapNodeImage("node-image2.png", "node-image2-save-path", ImageContentType.PNG)
        );
    }

    private GoalRoom 골룸을_저장한다(final RoadmapContent roadmapContent, final Member member) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10),
                roadmapContent, member);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TODAY, TEN_DAY_LATER),
                10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER),
                10, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoomRepository.save(goalRoom);
    }
}

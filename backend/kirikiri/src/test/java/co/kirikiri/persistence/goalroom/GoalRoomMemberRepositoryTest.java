package co.kirikiri.persistence.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
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
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapContents;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapNodeImages;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@RepositoryTest
class GoalRoomMemberRepositoryTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    public GoalRoomMemberRepositoryTest(final MemberRepository memberRepository,
                                        final RoadmapRepository roadmapRepository,
                                        final GoalRoomRepository goalRoomRepository,
                                        final RoadmapCategoryRepository roadmapCategoryRepository,
                                        final GoalRoomMemberRepository goalRoomMemberRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    @Test
    void 골룸과_사용자_아이디로_골룸_사용자_목록을_조회한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), savedGoalRoom, creator);
        final GoalRoomMember expected = goalRoomMemberRepository.save(goalRoomMember);

        // when
        final Optional<GoalRoomMember> findGoalRoomMember = goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(
                savedGoalRoom, new Identifier("cokirikiri"));

        // then
        assertThat(findGoalRoomMember.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    void 골룸과_사용자_아이디로_골룸_사용자_목록_조회시_없으면_빈값을_반환한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        // when
        final Optional<GoalRoomMember> findGoalRoomMember = goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(
                savedGoalRoom, new Identifier("cokirikiri2"));

        // then
        assertThat(findGoalRoomMember)
                .isEmpty();
    }

    @Test
    void 골룸으로_사용자_목록과_멤버를_함께_조회한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), savedGoalRoom, member1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 20, 12, 0, 0), savedGoalRoom, member2);
        final GoalRoomMember goalRoomMember3 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 21, 12, 0, 0), savedGoalRoom, member3);
        final List<GoalRoomMember> expected = goalRoomMemberRepository.saveAll(
                List.of(goalRoomMember1, goalRoomMember2, goalRoomMember3));

        // when
        final List<GoalRoomMember> findGoalRoomMembers = goalRoomMemberRepository.findAllByGoalRoom(
                goalRoom);

        // then
        assertThat(findGoalRoomMembers)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디로_골룸_사용자를_조회하고_들어온지_오래된_순서대로_정렬한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), savedGoalRoom, member1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 20, 12, 0, 0), savedGoalRoom, member2);
        final GoalRoomMember goalRoomMember3 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 21, 12, 0, 0), savedGoalRoom, member3);
        final List<GoalRoomMember> expected = goalRoomMemberRepository.saveAll(
                List.of(goalRoomMember1, goalRoomMember2, goalRoomMember3));

        // when
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(
                savedGoalRoom.getId(), GoalRoomMemberSortType.JOINED_ASC);

        // then
        assertThat(goalRoomMembers)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디로_골룸_사용자를_조회하고_마지막으로_들어온_순서대로_정렬한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), savedGoalRoom, member1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 20, 12, 0, 0), savedGoalRoom, member2);
        final GoalRoomMember goalRoomMember3 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 21, 12, 0, 0), savedGoalRoom, member3);
        final GoalRoomMember savedGoalRoomMember1 = goalRoomMemberRepository.save(goalRoomMember1);
        final GoalRoomMember savedGoalRoomMember2 = goalRoomMemberRepository.save(goalRoomMember2);
        final GoalRoomMember savedGoalRoomMember3 = goalRoomMemberRepository.save(goalRoomMember3);

        // when
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(
                savedGoalRoom.getId(), GoalRoomMemberSortType.JOINED_DESC);

        // then
        assertThat(goalRoomMembers)
                .isEqualTo(List.of(savedGoalRoomMember3, savedGoalRoomMember2, savedGoalRoomMember1));
    }

    @Test
    void 골룸_아이디로_골룸_사용자를_조회하고_달성률이_높은_순대로_정렬한다() {
        // given
        final Member creator = 크리에이터를_저장한다();
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        final GoalRoom savedGoalRoom = goalRoomRepository.save(goalRoom);

        final Member member1 = 사용자를_생성한다("identifier1", "password2!", "name1", "010-1111-1111");
        final Member member2 = 사용자를_생성한다("identifier2", "password3!", "name2", "010-1111-1112");
        final Member member3 = 사용자를_생성한다("identifier3", "password4!", "name3", "010-1111-1113");

        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 19, 12, 0, 0), savedGoalRoom, member1);
        goalRoomMember1.updateAccomplishmentRate(30.0);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 20, 12, 0, 0), savedGoalRoom, member2);
        goalRoomMember2.updateAccomplishmentRate(70.0);
        final GoalRoomMember goalRoomMember3 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.of(2023, 7, 21, 12, 0, 0), savedGoalRoom, member3);
        goalRoomMember3.updateAccomplishmentRate(10.0);
        final List<GoalRoomMember> expected = goalRoomMemberRepository.saveAll(
                List.of(goalRoomMember2, goalRoomMember1, goalRoomMember3));

        // when
        final List<GoalRoomMember> goalRoomMembers1 = goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(
                savedGoalRoom.getId(), GoalRoomMemberSortType.ACCOMPLISHMENT_RATE);
        final List<GoalRoomMember> goalRoomMembers2 = goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(
                savedGoalRoom.getId(), null);

        // then
        assertThat(goalRoomMembers1)
                .isEqualTo(expected);
        assertThat(goalRoomMembers2)
                .isEqualTo(expected);
    }

    @Test
    void 골룸_아이디와_사용자_아이디로_골룸_멤버를_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("identifier1", "password!1", "name1", "01011111111");
        final RoadmapCategory category = 카테고리를_저장한다("여가");
        final Roadmap roadmap = 로드맵을_저장한다(creator, category);

        final RoadmapContents roadmapContents = roadmap.getContents();
        final RoadmapContent targetRoadmapContent = roadmapContents.getValues().get(0);

        final GoalRoom goalRoom = 골룸을_생성한다(targetRoadmapContent, creator);
        goalRoomRepository.save(goalRoom);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(
                GoalRoomRole.LEADER, LocalDateTime.now(), goalRoom, creator
        );
        goalRoomMemberRepository.save(goalRoomMember);

        // when
        final GoalRoomMember findGoalRoomMember = goalRoomMemberRepository.findGoalRoomMember(goalRoom.getId(),
                creator.getIdentifier()).get();

        // then
        Assertions.assertThat(findGoalRoomMember)
                .usingRecursiveComparison()
                .ignoringFields("joinedAt")
                .isEqualTo(goalRoomMember);
    }

    private Member 크리에이터를_저장한다() {
        final MemberImage memberImage = new MemberImage("originalFileName", "serverFilePath", ImageContentType.JPG);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        final Member creator = new Member(1L, new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberImage, memberProfile);
        return memberRepository.save(creator);
    }

    private Member 사용자를_생성한다(final String identifier, final String password, final String nickname,
                             final String phoneNumber) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), phoneNumber);
        final Member member = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password(password)), new Nickname(nickname),
                new MemberImage("file-name", "file-path", ImageContentType.PNG), memberProfile);
        return memberRepository.save(member);
    }

    private RoadmapCategory 카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        roadmapNode1.addImages(new RoadmapNodeImages(노드_이미지들을_생성한다()));
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private List<RoadmapNodeImage> 노드_이미지들을_생성한다() {
        return List.of(
                new RoadmapNodeImage("node-image1.png", "node-image1-save-path", ImageContentType.PNG),
                new RoadmapNodeImage("node-image2.png", "node-image2-save-path", ImageContentType.PNG)
        );
    }

    private Roadmap 로드맵을_저장한다(final Member creator, final RoadmapCategory category) {
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(roadmapContent);
        return roadmapRepository.save(roadmap);
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent, final Member member) {
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(10),
                roadmapContent, member);
        final List<RoadmapNode> roadmapNodes = roadmapContent.getNodes().getValues();

        final RoadmapNode firstRoadmapNode = roadmapNodes.get(0);
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TODAY, TEN_DAY_LATER), 10, firstRoadmapNode);

        final RoadmapNode secondRoadmapNode = roadmapNodes.get(1);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER),
                10, secondRoadmapNode);

        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoom;
    }
}

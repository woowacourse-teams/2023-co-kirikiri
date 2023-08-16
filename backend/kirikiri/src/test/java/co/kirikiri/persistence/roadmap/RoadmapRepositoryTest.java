package co.kirikiri.persistence.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.domain.roadmap.RoadmapTag;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.domain.roadmap.vo.RoadmapTagName;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@RepositoryTest
class RoadmapRepositoryTest {

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapRepositoryTest(final MemberRepository memberRepository,
                                 final RoadmapRepository roadmapRepository,
                                 final GoalRoomRepository goalRoomRepository,
                                 final GoalRoomMemberRepository goalRoomMemberRepository,
                                 final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 로드맵을_저장한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory category = 카테고리를_생성한다("여가");
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);

        // when
        final Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        // then
        assertThat(savedRoadmap).usingRecursiveComparison()
                .isEqualTo(roadmap);
    }

    @Test
    void 단일_로드맵을_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory category = 카테고리를_생성한다("여가");
        final Roadmap savedRoadmap = 로드맵을_저장한다("로드맵 제목", creator, category);

        // when
        final Roadmap expectedRoadmap = roadmapRepository.findRoadmapById(savedRoadmap.getId()).get();

        assertThat(expectedRoadmap)
                .usingRecursiveComparison()
                .isEqualTo(savedRoadmap);
    }

    @Test
    void 카테고리_값이_null이라면_삭제되지_않은_전체_로드맵을_최신순으로_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_저장한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_저장한다("게임 로드맵2", creator, gameCategory);
        final Roadmap travelRoadmap = 로드맵을_저장한다("여행 로드맵", creator, travelCategory);
        삭제된_로드맵을_저장한다("여행 로드맵2", creator, travelCategory);

        final RoadmapCategory category = null;
        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                null, 2);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(3),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(travelRoadmap, gameRoadmap2, gameRoadmap))
        );
    }

    @Test
    void 카테고리_값으로_1이상의_유효한_값이_들어오면_해당_카테고리의_삭제되지_않은_로드맵을_최신순으로_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_저장한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_저장한다("게임 로드맵2", creator, gameCategory);
        삭제된_로드맵을_저장한다("게임 로드맵3", creator, gameCategory);
        삭제된_로드맵을_저장한다("게임 로드맵4", creator, travelCategory);

        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(gameCategory, orderType,
                null, 10);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(gameRoadmap2, gameRoadmap))
        );
    }

    @Test
    void 카테고리_조건_없이_주어진_로드맵_이전의_데이터를_최신순으로_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_저장한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_저장한다("게임 로드맵2", creator, gameCategory);
        final Roadmap travelRoadmap = 로드맵을_저장한다("여행 로드맵", creator, travelCategory);
        삭제된_로드맵을_저장한다("여행 로드맵2", creator, travelCategory);

        final RoadmapCategory category = null;
        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                null, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                gameRoadmap2.getId(), 10);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(3),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(travelRoadmap, gameRoadmap2, gameRoadmap)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(gameRoadmap))
        );
    }

    @Test
    void 카테고리_조건_없이_주어진_로드맵_이전의_데이터를_생성된_골룸이_많은순으로_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap1 = 노드_정보를_포함한_로드맵을_생성한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 노드_정보를_포함한_로드맵을_생성한다("게임 로드맵2", creator, gameCategory);
        final Roadmap travelRoadmap = 노드_정보를_포함한_로드맵을_생성한다("여행 로드맵", creator, travelCategory);
        노드_정보를_포함한_삭제된_로드맵을_저장한다("여행 로드맵2", creator, travelCategory);

        // gameRoadmap1 : 골룸 3개
        골룸을_생성한다(gameRoadmap1.getContents().getValues().get(0), creator);
        골룸을_생성한다(gameRoadmap1.getContents().getValues().get(0), creator);
        골룸을_생성한다(gameRoadmap1.getContents().getValues().get(0), creator);

        // gameRoadmap2 : 골룸 0개
        // travelRoadmap : 골룸 1개
        골룸을_생성한다(travelRoadmap.getContents().getValues().get(0), creator);

        final RoadmapCategory category = null;
        final RoadmapOrderType orderType = RoadmapOrderType.GOAL_ROOM_COUNT;

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                null, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                travelRoadmap.getId(), 10);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(3),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(gameRoadmap1, travelRoadmap, gameRoadmap2)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(gameRoadmap2))
        );
    }

    @Test
    void 카테고리_조건_없이_주어진_로드맵_이전의_데이터를_참가중인_인원이_많은순으로_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final Member follower = 사용자를_생성한다("cokirikiri2", "코끼리2");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap1 = 노드_정보를_포함한_로드맵을_생성한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 노드_정보를_포함한_로드맵을_생성한다("게임 로드맵2", creator, gameCategory);
        final Roadmap travelRoadmap = 노드_정보를_포함한_로드맵을_생성한다("여행 로드맵", creator, travelCategory);
        노드_정보를_포함한_삭제된_로드맵을_저장한다("여행 로드맵2", creator, travelCategory);

        골룸을_생성한다(gameRoadmap1.getContents().getValues().get(0), creator);
        final GoalRoom gameRoadmap2GoalRoom = 골룸을_생성한다(gameRoadmap2.getContents().getValues().get(0), creator);
        final GoalRoom travelRoadmapGoalRoom = 골룸을_생성한다(travelRoadmap.getContents().getValues().get(0), creator);

        // gameRoadmap1 : 참가인원 0명
        // gameRoadmap2 : 참가인원 1명
        final List<GoalRoomMember> gameRoadmap2GoalRoomMembers = List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), gameRoadmap2GoalRoom, creator));
        goalRoomMemberRepository.saveAll(gameRoadmap2GoalRoomMembers);

        // travelRoadmap : 참가인원 2명
        final List<GoalRoomMember> travelRoadmapGoalRoomMembers = List.of(
                new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), travelRoadmapGoalRoom, creator),
                new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), travelRoadmapGoalRoom, follower));
        goalRoomMemberRepository.saveAll(travelRoadmapGoalRoomMembers);

        final RoadmapCategory category = null;
        final RoadmapOrderType orderType = RoadmapOrderType.PARTICIPANT_COUNT;

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                null, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                gameRoadmap2.getId(), 10);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(3),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(travelRoadmap, gameRoadmap2, gameRoadmap1)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(gameRoadmap1))
        );
    }

    @Test
    void 카테고리_조건_없이_주어진_로드맵_이전의_데이터를_평점순으로_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final Member follower = 사용자를_생성한다("cokirikiri2", "코끼리2");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap1 = 노드_정보를_포함한_로드맵을_생성한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 노드_정보를_포함한_로드맵을_생성한다("게임 로드맵2", creator, gameCategory);
        final Roadmap travelRoadmap = 노드_정보를_포함한_로드맵을_생성한다("여행 로드맵", creator, travelCategory);
        노드_정보를_포함한_삭제된_로드맵을_저장한다("여행 로드맵2", creator, travelCategory);

        // gameRoadmap1 : 4.5
        final RoadmapReview gameRoadmap1Review = new RoadmapReview("리뷰1", 4.5, follower);
        gameRoadmap1.addReview(gameRoadmap1Review);
        roadmapRepository.save(gameRoadmap1);

        // gameRoadmap2 : 5.0
        final RoadmapReview gameRoadmap2Review = new RoadmapReview("리뷰2", 5.0, follower);
        gameRoadmap2.addReview(gameRoadmap2Review);
        roadmapRepository.save(gameRoadmap2);

        // travelRoadmap : 4.0
        final RoadmapReview travelRoadmapReview = new RoadmapReview("리뷰3", 4.0, follower);
        travelRoadmap.addReview(travelRoadmapReview);
        roadmapRepository.save(travelRoadmap);

        final RoadmapCategory category = null;
        final RoadmapOrderType orderType = RoadmapOrderType.REVIEW_RATE;

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                null, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                gameRoadmap1.getId(), 10);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(3),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(gameRoadmap2, gameRoadmap1, travelRoadmap)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(travelRoadmap))
        );
    }

    @Test
    void 로드맵을_제목으로_검색한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory category = 카테고리를_생성한다("여가");

        final Roadmap roadmap1 = 로드맵을_저장한다("로드맵", creator, category);
        final Roadmap roadmap2 = 로드맵을_저장한다("짱로드맵", creator, category);
        final Roadmap roadmap3 = 로드맵을_저장한다("로 드맵짱", creator, category);
        final Roadmap roadmap4 = 로드맵을_저장한다("짱로드 맵짱", creator, category);
        로드맵을_저장한다("로드", creator, category);
        삭제된_로드맵을_저장한다("로드맵", creator, category);

        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;
        final RoadmapSearchDto searchRequest = RoadmapSearchDto.create(null, " 로 드 맵 ", null);

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                null, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                roadmap3.getId(), 3);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(3),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap4, roadmap3, roadmap2)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap2, roadmap1))
        );
    }

    @Test
    void 로드맵을_크리에이터_닉네임으로_검색한다() {
        // given
        final Member creator1 = 사용자를_생성한다("cokirikiri", "코끼리");
        final Member creator2 = 사용자를_생성한다("cokirikiri2", "끼리코");
        final RoadmapCategory category = 카테고리를_생성한다("여가");

        final Roadmap roadmap1 = 로드맵을_저장한다("로드맵", creator1, category);
        final Roadmap roadmap2 = 로드맵을_저장한다("로드맵", creator1, category);
        로드맵을_저장한다("로드맵", creator2, category);
        final Roadmap roadmap4 = 로드맵을_저장한다("로드맵", creator1, category);
        로드맵을_저장한다("로드맵", creator2, category);
        삭제된_로드맵을_저장한다("로드맵", creator1, category);

        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;
        final RoadmapSearchDto searchRequest = RoadmapSearchDto.create(creator1.getNickname().getValue(), null, null);

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                null, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                roadmap2.getId(), 3);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(3),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap4, roadmap2, roadmap1)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap1))
        );
    }

    @Test
    void 로드맵을_태그_이름으로_검색한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory category = 카테고리를_생성한다("여가");

        final Roadmap roadmap1 = 로드맵을_태그와_저장한다("로드맵", creator, category,
                new RoadmapTags(List.of(
                        new RoadmapTag(new RoadmapTagName("자바")),
                        new RoadmapTag(new RoadmapTagName("스프링")))));

        로드맵을_저장한다("로드맵", creator, category);

        final Roadmap roadmap3 = 로드맵을_태그와_저장한다("로드맵", creator, category,
                new RoadmapTags(List.of(
                        new RoadmapTag(new RoadmapTagName("자바")))));

        로드맵을_태그와_저장한다("로드맵", creator, category, new RoadmapTags(List.of(
                new RoadmapTag(new RoadmapTagName("스프링")))));

        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;
        final RoadmapSearchDto searchRequest = RoadmapSearchDto.create(null, null, " 자 바 ");

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                null, 1);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                roadmap3.getId(), 1);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap3, roadmap1)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap1))
        );
    }

    @Test
    void 사용자가_생성한_로드맵을_조회한다() {
        // given
        final Member creator = 사용자를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");
        final RoadmapCategory itCategory = 카테고리를_생성한다("IT");

        final Roadmap gameRoadmap = 로드맵을_저장한다("로드맵1", creator, gameCategory);
        final Roadmap traveRoadmap = 로드맵을_저장한다("로드맵2", creator, travelCategory);
        final Roadmap deletedGameRoadmap = 삭제된_로드맵을_저장한다("로드맵3", creator, itCategory);

        roadmapRepository.saveAll(List.of(gameRoadmap, traveRoadmap, deletedGameRoadmap));

        // when
        final List<Roadmap> roadmapsFirstPage = roadmapRepository.findRoadmapsWithCategoryByMemberOrderByLatest(creator,
                null, 2);
        final List<Roadmap> roadmapsSecondPage = roadmapRepository.findRoadmapsWithCategoryByMemberOrderByLatest(
                creator, roadmapsFirstPage.get(1).getId(), 2);

        // then
        assertAll(
                () -> assertThat(roadmapsFirstPage)
                        .isEqualTo(List.of(deletedGameRoadmap, traveRoadmap, gameRoadmap)),
                () -> assertThat(roadmapsSecondPage)
                        .isEqualTo(List.of(gameRoadmap))
        );
    }

    private Member 사용자를_생성한다(final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        final MemberImage memberImage = new MemberImage("file-name", "file-path", ImageContentType.PNG);
        final Member creator = new Member(new Identifier(identifier), new EncryptedPassword(new Password("password1!")),
                new Nickname(nickname), memberImage, memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_생성한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 로드맵을_저장한다(final String title, final Member creator, final RoadmapCategory category) {
        final Roadmap roadmap = new Roadmap(title, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        return roadmapRepository.save(roadmap);
    }

    private Roadmap 삭제된_로드맵을_저장한다(final String title, final Member creator, final RoadmapCategory category) {
        final Roadmap roadmap = new Roadmap(title, "로드맵 소개글2", 7, RoadmapDifficulty.DIFFICULT, creator, category);
        roadmap.delete();
        return roadmapRepository.save(roadmap);
    }

    private Roadmap 로드맵을_태그와_저장한다(final String title, final Member creator, final RoadmapCategory category,
                                  final RoadmapTags roadmapTags) {
        final Roadmap roadmap = new Roadmap(title, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addTags(roadmapTags);
        return roadmapRepository.save(roadmap);
    }

    private Roadmap 노드_정보를_포함한_로드맵을_생성한다(final String title, final Member creator, final RoadmapCategory category) {
        final Roadmap roadmap = new Roadmap(title, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        roadmap.addContent(roadmapContent);
        return roadmapRepository.save(roadmap);
    }

    private Roadmap 노드_정보를_포함한_삭제된_로드맵을_저장한다(final String title, final Member creator, final RoadmapCategory category) {
        final Roadmap roadmap = new Roadmap(title, "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        final RoadmapNode roadmapNode1 = 로드맵_노드를_생성한다("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = 로드맵_노드를_생성한다("로드맵 2주차", "로드맵 2주차 내용");
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(List.of(roadmapNode1, roadmapNode2));
        roadmap.addContent(roadmapContent);
        roadmap.delete();
        return roadmapRepository.save(roadmap);
    }

    private RoadmapNode 로드맵_노드를_생성한다(final String title, final String content) {
        return new RoadmapNode(title, content);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private GoalRoom 골룸을_생성한다(final RoadmapContent roadmapContent, final Member member) {
        final LocalDate today = LocalDate.now();
        final GoalRoomRoadmapNode goalRoomRoadmapNode1 = 골룸_로드맵_노드를_생성한다(today, today.plusDays(10),
                roadmapContent.getNodes().getValues().get(0));
        final GoalRoomRoadmapNode goalRoomRoadmapNode2 = 골룸_로드맵_노드를_생성한다(today.plusDays(11), today.plusDays(20),
                roadmapContent.getNodes().getValues().get(1));
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(
                List.of(goalRoomRoadmapNode1, goalRoomRoadmapNode2));

        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("골룸"), new LimitedMemberCount(5), roadmapContent,
                member);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        return goalRoomRepository.save(goalRoom);
    }

    private GoalRoomRoadmapNode 골룸_로드맵_노드를_생성한다(final LocalDate startDate, final LocalDate endDate,
                                                final RoadmapNode roadmapNode) {
        return new GoalRoomRoadmapNode(new Period(startDate, endDate), 1, roadmapNode);
    }
}

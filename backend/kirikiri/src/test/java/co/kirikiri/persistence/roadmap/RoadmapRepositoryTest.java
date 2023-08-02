package co.kirikiri.persistence.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapTag;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.domain.roadmap.vo.RoadmapTagName;
import co.kirikiri.persistence.dto.RoadmapFilterType;
import co.kirikiri.persistence.dto.RoadmapLastValueDto;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.CustomScrollRequest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

@RepositoryTest
class RoadmapRepositoryTest {

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapRepositoryTest(final MemberRepository memberRepository, final RoadmapRepository roadmapRepository,
                                 final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 로드맵을_저장한다() {
        // given
        final Member creator = 크리에이터를_생성한다("cokirikiri", "코끼리");
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
        final Member creator = 크리에이터를_생성한다("cokirikiri", "코끼리");
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
        final Member creator = 크리에이터를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_저장한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_저장한다("게임 로드맵2", creator, gameCategory);
        final Roadmap travelRoadmap = 로드맵을_저장한다("여행 로드맵", creator, travelCategory);
        삭제된_로드맵을_저장한다("여행 로드맵2", creator, travelCategory);

        final RoadmapCategory category = null;
        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                null, 2);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(travelRoadmap, gameRoadmap2))
        );
    }

    @Test
    void 카테고리_값으로_1이상의_유효한_값이_들어오면_해당_카테고리의_삭제되지_않은_로드맵을_최신순으로_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_저장한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_저장한다("게임 로드맵2", creator, gameCategory);
        삭제된_로드맵을_저장한다("게임 로드맵3", creator, gameCategory);
        삭제된_로드맵을_저장한다("게임 로드맵4", creator, travelCategory);

        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;

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
    void 카테고리_조건_없이_주어진_마지막_날짜_이전의_데이터를_최신순으로_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_저장한다("게임 로드맵", creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_저장한다("게임 로드맵2", creator, gameCategory);
        final Roadmap travelRoadmap = 로드맵을_저장한다("여행 로드맵", creator, travelCategory);
        삭제된_로드맵을_저장한다("여행 로드맵2", creator, travelCategory);

        final RoadmapCategory category = null;
        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;
        final RoadmapLastValueDto firstRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(null, null, null, null, 1));
        final RoadmapLastValueDto secondRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(travelRoadmap.getCreatedAt(), null, null, null, 10));

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                firstRoadmapLastValueDto, 1);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCategory(category, orderType,
                secondRoadmapLastValueDto, 10);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(travelRoadmap)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(gameRoadmap2, gameRoadmap))
        );
    }

    @Test
    void 로드맵을_제목으로_검색한다() {
        // given
        final Member creator = 크리에이터를_생성한다("cokirikiri", "코끼리");
        final RoadmapCategory category = 카테고리를_생성한다("여가");

        final Roadmap roadmap1 = 로드맵을_저장한다("로드맵", creator, category);
        final Roadmap roadmap2 = 로드맵을_저장한다("짱로드맵", creator, category);
        final Roadmap roadmap3 = 로드맵을_저장한다("로 드맵짱", creator, category);
        final Roadmap roadmap4 = 로드맵을_저장한다("짱로드 맵짱", creator, category);
        로드맵을_저장한다("로드", creator, category);
        삭제된_로드맵을_저장한다("로드맵", creator, category);

        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;
        final RoadmapSearchDto searchRequest = RoadmapSearchDto.create(null, " 로 드 맵 ", null);
        final RoadmapLastValueDto firstRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(null, null, null, null, 2));
        final RoadmapLastValueDto secondRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(roadmap3.getCreatedAt(), null, null, null, 3));

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                firstRoadmapLastValueDto, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                secondRoadmapLastValueDto, 3);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap4, roadmap3)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap2, roadmap1))
        );
    }

    @Test
    void 로드맵을_크리에이터_아이디로_검색한다() {
        // given
        final Member creator1 = 크리에이터를_생성한다("cokirikiri", "코끼리");
        final Member creator2 = 크리에이터를_생성한다("cokirikiri2", "끼리코");
        final RoadmapCategory category = 카테고리를_생성한다("여가");

        final Roadmap roadmap1 = 로드맵을_저장한다("로드맵", creator1, category);
        final Roadmap roadmap2 = 로드맵을_저장한다("로드맵", creator1, category);
        로드맵을_저장한다("로드맵", creator2, category);
        final Roadmap roadmap4 = 로드맵을_저장한다("로드맵", creator1, category);
        로드맵을_저장한다("로드맵", creator2, category);
        삭제된_로드맵을_저장한다("로드맵", creator1, category);

        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;
        final RoadmapSearchDto searchRequest = RoadmapSearchDto.create(creator1.getId(), null, null);
        final RoadmapLastValueDto firstRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(null, null, null, null, 2));
        final RoadmapLastValueDto secondRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(roadmap2.getCreatedAt(), null, null, null, 3));

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                firstRoadmapLastValueDto, 2);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                secondRoadmapLastValueDto, 3);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(2),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap4, roadmap2)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap1))
        );
    }

    @Test
    void 로드맵을_태그_이름으로_검색한다() {
        // given
        final Member creator = 크리에이터를_생성한다("cokirikiri", "코끼리");
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

        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;
        final RoadmapSearchDto searchRequest = RoadmapSearchDto.create(null, null, " 자 바 ");
        final RoadmapLastValueDto firstRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(null, null, null, null, 1));
        final RoadmapLastValueDto secondRoadmapLastValueDto = RoadmapLastValueDto.create(
                new CustomScrollRequest(roadmap3.getCreatedAt(), null, null, null, 1));

        // when
        final List<Roadmap> firstRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                firstRoadmapLastValueDto,
                1);
        final List<Roadmap> secondRoadmapRequest = roadmapRepository.findRoadmapsByCond(searchRequest, orderType,
                secondRoadmapLastValueDto, 1);

        // then
        assertAll(
                () -> assertThat(firstRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(firstRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap3)),

                () -> assertThat(secondRoadmapRequest.size()).isEqualTo(1),
                () -> assertThat(secondRoadmapRequest).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(List.of(roadmap1))
        );
    }

    private Member 크리에이터를_생성한다(final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1),
                new Nickname(nickname), "010-1234-5678");
        final Member creator = new Member(new Identifier(identifier),
                new EncryptedPassword(new Password("password1!")), memberProfile);
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
}

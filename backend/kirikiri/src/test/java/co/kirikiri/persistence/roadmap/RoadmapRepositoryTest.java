package co.kirikiri.persistence.roadmap;

import static java.util.List.of;
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
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.dto.RoadmapFilterType;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory category = 카테고리를_생성한다("여가");
        final Roadmap roadmap = 로드맵을_생성한다(creator, category);

        final Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        // then
        assertThat(savedRoadmap).usingRecursiveComparison()
                .isEqualTo(roadmap);
    }

    @Test
    void 카테고리_값이_null이라면_삭제되지_않은_전체_로드맵을_최신순으로_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap travelRoadmap = 로드맵을_생성한다(creator, travelCategory);
        final Roadmap deletedTravelRoadmap = 삭제된_로드맵을_생성한다(creator, travelCategory);

        roadmapRepository.saveAll(of(gameRoadmap, deletedTravelRoadmap, gameRoadmap2, travelRoadmap));

        final RoadmapCategory category = null;
        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;
        final PageRequest firstPage = PageRequest.of(0, 2);
        final PageRequest secondPage = PageRequest.of(1, 2);

        // when
        final Page<Roadmap> firstPageRoadmaps = roadmapRepository.findRoadmapPagesByCond(category, orderType,
                firstPage);
        final Page<Roadmap> secondPageRoadmaps = roadmapRepository.findRoadmapPagesByCond(category, orderType,
                secondPage);

        // then
        assertAll(
                () -> assertThat(firstPageRoadmaps.getTotalPages()).isEqualTo(2),
                () -> assertThat(firstPageRoadmaps.getTotalElements()).isEqualTo(3),
                () -> assertThat(firstPageRoadmaps.getContent().size()).isEqualTo(2),

                () -> assertThat(secondPageRoadmaps.getTotalPages()).isEqualTo(2),
                () -> assertThat(secondPageRoadmaps.getTotalElements()).isEqualTo(3),
                () -> assertThat(secondPageRoadmaps.getContent().size()).isEqualTo(1),

                () -> assertThat(firstPageRoadmaps.getContent()).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(of(travelRoadmap, gameRoadmap2)),

                () -> assertThat(secondPageRoadmaps.getContent()).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(of(gameRoadmap))
        );
    }

    @Test
    void 카테고리_값으로_1이상의_유효한_값이_들어오면_해당_카테고리의_삭제되지_않은_로드맵을_최신순으로_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap deletedGameRoadmap = 삭제된_로드맵을_생성한다(creator, gameCategory);
        final Roadmap deletedTravelRoadmap = 삭제된_로드맵을_생성한다(creator, travelCategory);

        roadmapRepository.saveAll(of(gameRoadmap, deletedTravelRoadmap, gameRoadmap2, deletedGameRoadmap));

        final RoadmapFilterType orderType = RoadmapFilterType.LATEST;
        final PageRequest firstPage = PageRequest.of(0, 10);

        // when
        final Page<Roadmap> firstPageRoadmaps = roadmapRepository.findRoadmapPagesByCond(gameCategory, orderType,
                firstPage);

        // then
        assertAll(
                () -> assertThat(firstPageRoadmaps.getTotalPages()).isEqualTo(1),
                () -> assertThat(firstPageRoadmaps.getTotalElements()).isEqualTo(2),
                () -> assertThat(firstPageRoadmaps.getContent().size()).isEqualTo(2),
                () -> assertThat(firstPageRoadmaps.getContent()).usingRecursiveComparison()
                        .ignoringFields("id", "createdAt", "updatedAt")
                        .isEqualTo(of(gameRoadmap2, gameRoadmap))
        );
    }

    @Test
    void 단일_로드맵을_조회한다() {
        final Roadmap savedRoadmap = roadmapRepository.save(로드맵을_생성한다());
        final Roadmap expectedRoadmap = roadmapRepository.findById(savedRoadmap.getId()).get();

        assertThat(expectedRoadmap).usingRecursiveComparison()
                .isEqualTo(savedRoadmap);
    }

    @Test
    void 사용자가_생성한_로드맵을_조회한다() {
        // given
        final Member creator = 크리에이터를_생성한다();
        final RoadmapCategory gameCategory = 카테고리를_생성한다("게임");
        final RoadmapCategory travelCategory = 카테고리를_생성한다("여행");

        final Roadmap gameRoadmap = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap gameRoadmap2 = 로드맵을_생성한다(creator, gameCategory);
        final Roadmap deletedGameRoadmap = 삭제된_로드맵을_생성한다(creator, gameCategory);

        roadmapRepository.saveAll(of(gameRoadmap, gameRoadmap2, deletedGameRoadmap));

        // when
        final List<Roadmap> roadmapsFirstPage = roadmapRepository.findRoadmapsWithCategoryByMemberOrderByIdDesc(creator,
                null, 2);
        final List<Roadmap> roadmapsSecondPage = roadmapRepository.findRoadmapsWithCategoryByMemberOrderByIdDesc(
                creator, 2L, 2);

        // then
        assertAll(
                () -> assertThat(roadmapsFirstPage).isEqualTo(of(deletedGameRoadmap, gameRoadmap2)),
                () -> assertThat(roadmapsSecondPage).isEqualTo(of(gameRoadmap))
        );
    }

    private Roadmap 로드맵을_생성한다() {
        final Member creator = 사용자를_생성한다();
        final RoadmapCategory category = 로드맵_카테고리를_생성한다();
        final RoadmapContent content = new RoadmapContent("로드맵 제목");

        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(content);

        return roadmap;
    }

    private Member 사용자를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30), "010-0000-0000");
        final Member member = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("썬샷"), memberProfile);

        return memberRepository.save(member);
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다() {
        final RoadmapCategory category = new RoadmapCategory("운동");
        return roadmapCategoryRepository.save(category);
    }

    private Member 크리에이터를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        final Member creator = new Member(new Identifier("cokirikiri"),
                new EncryptedPassword(new Password("password1!")), new Nickname("코끼리"), memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_생성한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        final RoadmapNodes roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes.getValues());

        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, RoadmapDifficulty.DIFFICULT,
                creator, category);
        roadmap.addContent(roadmapContent);
        return roadmap;
    }

    private RoadmapNodes 로드맵_노드들을_생성한다() {
        return new RoadmapNodes(of(
                new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용"),
                new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용")));

    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }

    private Roadmap 삭제된_로드맵을_생성한다(final Member creator, final RoadmapCategory category) {
        final Roadmap roadmap = new Roadmap("로드맵 제목2", "로드맵 소개글2", 7, RoadmapDifficulty.DIFFICULT, creator, category);
        roadmap.delete();
        return roadmap;
    }
}

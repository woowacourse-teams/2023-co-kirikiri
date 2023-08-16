package co.kirikiri.persistence.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.domain.ImageContentType;
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
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@RepositoryTest
class RoadmapReviewRepositoryTest {

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapReviewRepository roadmapReviewRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapReviewRepositoryTest(final MemberRepository memberRepository,
                                       final RoadmapRepository roadmapRepository,
                                       final RoadmapReviewRepository roadmapReviewRepository,
                                       final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.memberRepository = memberRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapReviewRepository = roadmapReviewRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 로드맵과_사용자로_로드맵_리뷰_정보가_존재하면_반환한다() {
        // given
        final Member member = 사용자를_저장한다("코끼리", "cokirikiri");
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(member, category);

        final RoadmapReview roadmapReview = new RoadmapReview("리뷰", 1.5, member);
        roadmapReview.updateRoadmap(roadmap);
        roadmapReviewRepository.save(roadmapReview);

        // when
        final RoadmapReview findRoadmapReview = roadmapReviewRepository.findByRoadmapAndMember(roadmap, member).get();

        // then
        assertThat(findRoadmapReview)
                .isEqualTo(roadmapReview);
    }

    @Test
    void 로드맵과_사용자로_로드맵_리뷰_정보가_존재하지_않으면_빈값을_반환한다() {
        // given
        final Member member = 사용자를_저장한다("코끼리", "cokirikiri");
        final Member member2 = 사용자를_저장한다("끼리코", "kirikirico");
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(member, category);

        final RoadmapReview roadmapReview = new RoadmapReview("리뷰", 2.5, member);
        roadmapReview.updateRoadmap(roadmap);
        roadmapReviewRepository.save(roadmapReview);

        // when
        final Optional<RoadmapReview> findRoadmapReview = roadmapReviewRepository.findByRoadmapAndMember(roadmap,
                member2);

        // then
        assertThat(findRoadmapReview)
                .isEmpty();
    }

    @Test
    void 로드맵에_대한_리뷰_정보를_최신순으로_조회한다() {
        // given
        final Member member = 사용자를_저장한다("코끼리", "cokirikiri");
        final Member member2 = 사용자를_저장한다("끼리코", "kirikirico");
        final Member member3 = 사용자를_저장한다("리끼코", "rikirikico");
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap = 로드맵을_저장한다(member, category);

        final RoadmapReview roadmapReview1 = new RoadmapReview("리뷰1", 2.5, member);
        final RoadmapReview roadmapReview2 = new RoadmapReview("리뷰2", 4.0, member2);
        final RoadmapReview roadmapReview3 = new RoadmapReview("리뷰3", 5.0, member3);
        roadmapReview1.updateRoadmap(roadmap);
        roadmapReview2.updateRoadmap(roadmap);
        roadmapReview3.updateRoadmap(roadmap);
        roadmapReviewRepository.save(roadmapReview1);
        roadmapReviewRepository.save(roadmapReview2);
        roadmapReviewRepository.save(roadmapReview3);

        // when
        final List<RoadmapReview> roadmapReviewsFirstPage = roadmapReviewRepository.findRoadmapReviewWithMemberByRoadmapOrderByLatest(
                roadmap, null, 2);

        final List<RoadmapReview> roadmapReviewsSecondPage = roadmapReviewRepository.findRoadmapReviewWithMemberByRoadmapOrderByLatest(
                roadmap, roadmapReviewsFirstPage.get(1).getId(), 2);

        // then
        assertAll(
                () -> assertThat(roadmapReviewsFirstPage)
                        .isEqualTo(List.of(roadmapReview3, roadmapReview2)),
                () -> assertThat(roadmapReviewsSecondPage)
                        .isEqualTo(List.of(roadmapReview1))
        );
    }

    @Test
    void 로드맵에_대한_리뷰_정보가_없으면_빈_값을_반환한다() {
        // given
        final Member member = 사용자를_저장한다("코끼리", "cokirikiri");
        final Member member2 = 사용자를_저장한다("끼리코", "kirikirico");
        final RoadmapCategory category = 카테고리를_저장한다("게임");
        final Roadmap roadmap1 = 로드맵을_저장한다(member, category);
        final Roadmap roadmap2 = 로드맵을_저장한다(member2, category);

        final RoadmapReview roadmapReview = new RoadmapReview("리뷰", 2.5, member);
        roadmapReview.updateRoadmap(roadmap1);
        roadmapReviewRepository.save(roadmapReview);

        // when
        final List<RoadmapReview> roadmapReviewsFirstPage = roadmapReviewRepository.findRoadmapReviewWithMemberByRoadmapOrderByLatest(
                roadmap2, null, 1);

        // then
        assertThat(roadmapReviewsFirstPage).isEmpty();
    }

    private Member 사용자를_저장한다(final String name, final String identifier) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1990, 1, 1), "010-1234-5678");
        final MemberImage memberImage = new MemberImage("test-name", "test-path", ImageContentType.PNG);
        final Member creator = new Member(new Identifier(identifier), new EncryptedPassword(new Password("password1!")),
                new Nickname(name), memberImage, memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }

    private Roadmap 로드맵을_저장한다(final Member creator, final RoadmapCategory category) {
        final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 10, RoadmapDifficulty.NORMAL, creator, category);
        roadmap.addContent(roadmapContent);
        return roadmapRepository.save(roadmap);
    }

    private List<RoadmapNode> 로드맵_노드들을_생성한다() {
        final RoadmapNode roadmapNode1 = new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용");
        final RoadmapNode roadmapNode2 = new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용");
        return List.of(roadmapNode1, roadmapNode2);
    }

    private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
        return roadmapContent;
    }
}

package co.kirikiri.roadmap.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.common.type.ImageContentType;
import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberImage;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import co.kirikiri.member.persistence.MemberRepository;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapReview;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

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
        final Long roadmapId = 1L;

        final RoadmapReview roadmapReview = new RoadmapReview("리뷰", 1.5, member.getId(), roadmapId);
        roadmapReviewRepository.save(roadmapReview);

        // when
        final RoadmapReview findRoadmapReview = roadmapReviewRepository.findByRoadmapIdAndMemberId(roadmapId, member.getId())
                .get();

        // then
        assertThat(findRoadmapReview).isEqualTo(roadmapReview);
    }

    @Test
    void 로드맵과_사용자로_로드맵_리뷰_정보가_존재하지_않으면_빈값을_반환한다() {
        // given
        final Member member = 사용자를_저장한다("코끼리", "cokirikiri");
        final Member member2 = 사용자를_저장한다("끼리코", "kirikirico");
        final Long roadmapId = 1L;

        final RoadmapReview roadmapReview = new RoadmapReview("리뷰", 2.5, member.getId(), roadmapId);
        roadmapReviewRepository.save(roadmapReview);

        // when
        final Optional<RoadmapReview> findRoadmapReview = roadmapReviewRepository.findByRoadmapIdAndMemberId(roadmapId,
                member2.getId());

        // then
        assertThat(findRoadmapReview).isEmpty();
    }

    @Test
    void 로드맵에_대한_리뷰_정보를_최신순으로_조회한다() {
        // given
        final Member member = 사용자를_저장한다("코끼리", "cokirikiri");
        final Member member2 = 사용자를_저장한다("끼리코", "kirikirico");
        final Member member3 = 사용자를_저장한다("리끼코", "rikirikico");
        final Long roadmapId = 1L;

        final RoadmapReview roadmapReview1 = new RoadmapReview("리뷰1", 2.5, member.getId(), roadmapId);
        final RoadmapReview roadmapReview2 = new RoadmapReview("리뷰2", 4.0, member2.getId(), roadmapId);
        final RoadmapReview roadmapReview3 = new RoadmapReview("리뷰3", 5.0, member3.getId(), roadmapId);
        roadmapReviewRepository.save(roadmapReview1);
        roadmapReviewRepository.save(roadmapReview2);
        roadmapReviewRepository.save(roadmapReview3);

        // when
        final List<RoadmapReview> roadmapReviewsFirstPage = roadmapReviewRepository.findRoadmapReviewByRoadmapIdOrderByLatest(
                roadmapId, null, 2);

        final List<RoadmapReview> roadmapReviewsSecondPage = roadmapReviewRepository.findRoadmapReviewByRoadmapIdOrderByLatest(
                roadmapId, roadmapReviewsFirstPage.get(1).getId(), 2);

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
        final Long roadmapId = 1L;
        final Long roadmapId2 = 2L;

        final RoadmapReview roadmapReview = new RoadmapReview("리뷰", 2.5, member.getId(), roadmapId2);
        roadmapReviewRepository.save(roadmapReview);

        // when
        final List<RoadmapReview> roadmapReviewsFirstPage = roadmapReviewRepository.findRoadmapReviewByRoadmapIdOrderByLatest(
                roadmapId, null, 1);

        // then
        assertThat(roadmapReviewsFirstPage).isEmpty();
    }

    private Member 사용자를_저장한다(final String name, final String identifier) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri1@email.com");
        final MemberImage memberImage = new MemberImage("test-name", "test-path", ImageContentType.PNG);
        final Member creator = new Member(new Identifier(identifier), new EncryptedPassword(new Password("password1!")),
                new Nickname(name), memberImage, memberProfile);
        return memberRepository.save(creator);
    }

    private RoadmapCategory 카테고리를_저장한다(final String name) {
        final RoadmapCategory roadmapCategory = new RoadmapCategory(name);
        return roadmapCategoryRepository.save(roadmapCategory);
    }
}

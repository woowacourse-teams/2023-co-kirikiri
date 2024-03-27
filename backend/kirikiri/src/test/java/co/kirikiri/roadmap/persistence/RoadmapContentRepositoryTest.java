package co.kirikiri.roadmap.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.member.domain.EncryptedPassword;
import co.kirikiri.member.domain.Gender;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.MemberProfile;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import co.kirikiri.member.domain.vo.Password;
import co.kirikiri.member.persistence.MemberRepository;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapTags;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

@RepositoryTest
class RoadmapContentRepositoryTest {

    private final MemberRepository memberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapContentRepository roadmapContentRepository;

    public RoadmapContentRepositoryTest(final MemberRepository memberRepository,
                                        final RoadmapCategoryRepository roadmapCategoryRepository,
                                        final RoadmapRepository roadmapRepository,
                                        final RoadmapContentRepository roadmapContentRepository) {
        this.memberRepository = memberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapContentRepository = roadmapContentRepository;
    }

    @Test
    void 로드맵_아이디로_로드맵의_가장_최근_컨텐츠를_조회한다() {
        // given
        final Roadmap roadmap = 로드맵을_저장한다();
        로드맵_컨텐츠를_저장한다(roadmap.getId());
        final RoadmapContent oldRoadmapContent = roadmapContentRepository.findFirstByRoadmapIdOrderByCreatedAtDesc(
                roadmap.getId()).get();

        final RoadmapContent newRoadmapContent = 로드맵_컨텐츠를_저장한다(roadmap.getId());

        // when
        final RoadmapContent expectedRoadmapContent = roadmapContentRepository.findFirstByRoadmapIdOrderByCreatedAtDesc(
                roadmap.getId()).get();

        // then
        assertAll(
                () -> assertThat(oldRoadmapContent).isNotEqualTo(expectedRoadmapContent),
                () -> assertThat(newRoadmapContent).isEqualTo(expectedRoadmapContent)
        );
    }

    @Test
    void 로드맵에_생성된_모든_컨텐츠를_삭제한다() {
        // given
        final Roadmap roadmap = 로드맵을_저장한다();
        로드맵_컨텐츠를_저장한다(roadmap.getId());
        로드맵_컨텐츠를_저장한다(roadmap.getId());

        // when
        final List<RoadmapContent> roadmapContents = roadmapContentRepository.findAllByRoadmapId(roadmap.getId());
        roadmapContentRepository.deleteAllByRoadmapId(roadmap.getId());

        // then
        assertAll(
                () -> assertThat(roadmapContents).hasSize(2),
                () -> assertThat(roadmapContentRepository.findAllByRoadmapId(roadmap.getId()))
                        .isEmpty()
        );
    }

    private Roadmap 로드맵을_저장한다() {
        final Member creator = 사용자를_생성한다();
        final RoadmapCategory category = 로드맵_카테고리를_생성한다();
        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100, RoadmapDifficulty.NORMAL, creator.getId(), category, new RoadmapTags(new ArrayList<>()));

        return roadmapRepository.save(roadmap);
    }

    private Member 사용자를_생성한다() {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri1@email.com");
        final Member member = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("썬샷"), null, memberProfile);

        return memberRepository.save(member);
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다() {
        final RoadmapCategory category = new RoadmapCategory("운동");
        return roadmapCategoryRepository.save(category);
    }

    private RoadmapContent 로드맵_컨텐츠를_저장한다(final Long roadmapId) {
        return roadmapContentRepository.save(new RoadmapContent("content", roadmapId, null));
    }
}

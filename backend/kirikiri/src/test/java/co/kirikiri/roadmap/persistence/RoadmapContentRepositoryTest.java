package co.kirikiri.roadmap.persistence;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    void 로드맵_컨텐츠를_로드맵과_함께_조회한다() {
        // given
        final Roadmap roadmap = 로드맵을_생성한다();
        final Roadmap savedRoadmap = roadmapRepository.save(roadmap);
        final Long roadmapContentId = savedRoadmap.getContents().getValues().get(0).getId();

        // when
        final RoadmapContent roadmapContent = roadmapContentRepository.findByIdWithRoadmap(roadmapContentId).get();

        // then
        assertAll(
                () -> assertThat(roadmapContent).isEqualTo(savedRoadmap.getContents().getValues().get(0)),
                () -> assertThat(roadmapContent.getRoadmap()).isEqualTo(savedRoadmap)
        );
    }

    @Test
    void 로드맵의_가장_최근_컨텐츠를_조회한다() {
        // given
        final Roadmap savedRoadmap = roadmapRepository.save(로드맵을_생성한다());
        final RoadmapContent oldRoadmapContent = roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(
                savedRoadmap).get();

        final RoadmapContent newRoadmapContent = new RoadmapContent("로드맵 제목");
        savedRoadmap.addContent(newRoadmapContent);

        // when
        final RoadmapContent expectedRoadmapContent = roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(
                savedRoadmap).get();

        // then
        assertAll(
                () -> assertThat(oldRoadmapContent).isNotEqualTo(expectedRoadmapContent),
                () -> assertThat(expectedRoadmapContent).isEqualTo(newRoadmapContent)
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
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, "kirikiri1@email.com");
        final Member member = new Member(new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")), new Nickname("썬샷"), null, memberProfile);

        return memberRepository.save(member);
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다() {
        final RoadmapCategory category = new RoadmapCategory("운동");
        return roadmapCategoryRepository.save(category);
    }
}

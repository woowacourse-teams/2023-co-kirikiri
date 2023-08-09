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
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

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
    void 로드맵의_가장_최근_컨텐츠를_조회한다() {
        final Roadmap savedRoadmap = roadmapRepository.save(로드맵을_생성한다());
        final RoadmapContent oldRoadmapContent = roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(
                savedRoadmap).get();

        final RoadmapContent newRoadmapContent = new RoadmapContent("로드맵 제목");
        savedRoadmap.addContent(newRoadmapContent);
        final RoadmapContent expectedRoadmapContent = roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(
                savedRoadmap).get();

        assertAll(
                () -> assertThat(oldRoadmapContent).isNotEqualTo(expectedRoadmapContent),
                () -> assertThat(expectedRoadmapContent).usingRecursiveComparison()
                        .isEqualTo(newRoadmapContent)
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
}

package co.kirikiri.persistence.roadmap;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.persistence.helper.RepositoryTest;
import co.kirikiri.persistence.member.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoadmapRepositoryTest extends RepositoryTest {

    private final RoadmapRepository roadmapRepository;
    private final MemberRepository memberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapRepositoryTest(final RoadmapRepository roadmapRepository, final MemberRepository memberRepository,
                                 final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapRepository = roadmapRepository;
        this.memberRepository = memberRepository;
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 단일_로드맵을_조회한다() {
        final Roadmap roadmap = 로드맵을_생성한다();
        final Roadmap savedRoadmap = roadmapRepository.save(roadmap);
        final Roadmap expectedRoadmap = roadmapRepository.findById(savedRoadmap.getId()).get();

        assertThat(expectedRoadmap).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(roadmap);
    }

    private Roadmap 로드맵을_생성한다() {
        final Member creator = 사용자를_생성한다();
        final RoadmapCategory category = 로드맵_카테고리를_생성한다();

        final List<RoadmapNodeImage> nodeImages = List.of(
                new RoadmapNodeImage("node-image1.png", "node-image1-save-path",
                        ImageContentType.PNG)
        );

        final List<RoadmapNode> nodes = List.of(
                new RoadmapNode("1단계", "준비운동", nodeImages),
                new RoadmapNode("2단계", "턱걸이", nodeImages)
        );

        final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 설명", 100,
                RoadmapDifficulty.NORMAL, RoadmapStatus.CREATED, creator, category);
        final RoadmapContent content = new RoadmapContent(nodes);
        roadmap.addContent(content);

        return roadmap;
    }

    private Member 사용자를_생성한다() {
        final MemberProfileImage profileImage = new MemberProfileImage("sunshot_image.webp",
                "sunshot-profile-save-path", ImageContentType.WEBP);
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30),
                "썬샷", "01083004367", profileImage);
        final Member member = new Member("아이디", "패스워드", memberProfile);

        return memberRepository.save(member);
    }

    private RoadmapCategory 로드맵_카테고리를_생성한다() {
        final RoadmapCategory category = new RoadmapCategory("운동");
        return roadmapCategoryRepository.save(category);
    }
}
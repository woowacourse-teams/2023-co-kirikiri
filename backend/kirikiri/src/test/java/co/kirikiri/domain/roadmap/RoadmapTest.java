package co.kirikiri.domain.roadmap;

import static co.kirikiri.domain.roadmap.RoadmapDifficulty.DIFFICULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.ImageContentType;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.MemberProfileImage;
import co.kirikiri.exception.BadRequestException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RoadmapTest {

    @Nested
    class 로드맵을_생성한다 {

        private final Member creator = 크리에이터를_생성한다();
        private final RoadmapCategory category = 카테고리를_생성한다();
        private final List<RoadmapNode> roadmapNodes = 로드맵_노드들을_생성한다();
        private final RoadmapContent roadmapContent = 로드맵_본문을_생성한다(roadmapNodes);

        @Test
        void 로드맵이_성공적으로_생성된다() {
            // expect
            assertDoesNotThrow(() -> new Roadmap("로드맵 제목", "로드맵 소개글", 30, DIFFICULT,
                    creator, category));
        }

        @Test
        void 로드맵에_본문을_추가한다() {
            // given
            final Roadmap roadmap = new Roadmap("로드맵 제목", "로드맵 소개글", 30, DIFFICULT, creator, category);

            // when
            roadmap.addContent(roadmapContent);

            // then
            final RoadmapContents contents = roadmap.getContents();
            assertThat(contents.getContents()).hasSize(1);
        }

        @Test
        void 로드맵_제목의_길이가_1보다_작으면_예외가_발생한다() {
            // given
            final String title = "";

            // expect
            assertThatThrownBy(() -> new Roadmap(title, "로드맵 소개글", 30, DIFFICULT, creator, category))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        void 로드맵_제목의_길이가_40보다_크면_예외가_발생한다() {
            // given
            final String title = "a".repeat(41);

            // expect
            assertThatThrownBy(() -> new Roadmap(title, "로드맵 소개글", 30, DIFFICULT, creator, category))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        void 로드맵_소개글의_길이가_1보다_작으면_예외가_발생한다() {
            // given
            final String introduction = "";

            // expect
            assertThatThrownBy(() -> new Roadmap("로드맵 제목", introduction, 30, DIFFICULT, creator, category))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        void 로드맵_소개글의_길이가_150보다_크면_예외가_발생한다() {
            // given
            final String introduction = "a".repeat(151);

            // expect
            assertThatThrownBy(() -> new Roadmap("로드맵 제목", introduction, 30, DIFFICULT, creator, category))
                    .isInstanceOf(BadRequestException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 1001})
        void 로드맵_추천_소요_기간이_0보다_작고_1000보다_크면_예외가_발생한다(final int requiredPeriod) {
            // expect
            assertThatThrownBy(() -> new Roadmap("로드맵 제목", "로드맵 소개글", requiredPeriod, DIFFICULT, creator, category))
                    .isInstanceOf(BadRequestException.class);
        }

        private Member 크리에이터를_생성한다() {
            final MemberProfileImage profileImage = new MemberProfileImage(1L, "originalFileName", "serverFilePath",
                    ImageContentType.JPEG);
            final MemberProfile profile = new MemberProfile(1L, Gender.FEMALE, LocalDate.of(1999, 6, 8), "nickname",
                    "01011112222", profileImage);

            return new Member(1L, "creator", "password", profile);
        }

        private RoadmapCategory 카테고리를_생성한다() {
            return new RoadmapCategory(1L, "여가");
        }

        private List<RoadmapNode> 로드맵_노드들을_생성한다() {
            return List.of(new RoadmapNode("로드맵 1주차", "로드맵 1주차 내용"),
                    new RoadmapNode("로드맵 2주차", "로드맵 2주차 내용"));
        }

        private RoadmapContent 로드맵_본문을_생성한다(final List<RoadmapNode> roadmapNodes) {
            final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
            roadmapContent.addNodes(new RoadmapNodes(roadmapNodes));
            return roadmapContent;
        }
    }
}

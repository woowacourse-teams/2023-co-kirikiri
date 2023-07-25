package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseTimeEntity;
import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapReview extends BaseTimeEntity {

    private static final int MIN_RATE = 0;
    private static final int MAX_RATE = 5;
    private static final int RATE_UNIT = 5;
    private static final String RATE_FORMAT = String.format("(%d(\\.%d)?|[%d-%d](\\.0|\\.%d)?)", MAX_RATE, MIN_RATE,
            MIN_RATE, MAX_RATE - 1, RATE_UNIT);
    private static final int CONTENT_MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1200)
    private String content;

    private Double rate = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    public RoadmapReview(final String content, final Double rate, final Member member) {
        validate(content, rate);
        if (content != null) {
            this.content = content;
        }
        this.rate = rate;
        this.member = member;
    }

    private void validate(final String content, final Double rate) {
        validateContentLength(content);
        validateRate(rate);
    }

    private void validateContentLength(final String content) {
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            throw new BadRequestException(String.format("리뷰는 최대 %d글자까지 입력할 수 있습니다.", CONTENT_MAX_LENGTH));
        }
    }

    private void validateRate(final Double rate) {
        if (!Pattern.matches(RATE_FORMAT, String.valueOf(rate))) {
            throw new BadRequestException(String.format("별점은 %d부터 %d까지 0.%d 단위로 설정할 수 있습니다.",
                    MIN_RATE, MAX_RATE, RATE_UNIT));
        }
    }

    public void updateRoadmap(final Roadmap roadmap) {
        if (this.roadmap == null) {
            this.roadmap = roadmap;
        }
    }

    public boolean isNotSameRoadmap(final Roadmap roadmap) {
        return this.roadmap == null || !this.roadmap.equals(roadmap);
    }
}

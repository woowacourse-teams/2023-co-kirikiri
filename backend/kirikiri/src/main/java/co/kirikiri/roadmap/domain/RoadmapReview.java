package co.kirikiri.roadmap.domain;

import co.kirikiri.common.entity.BaseUpdatedTimeEntity;
import co.kirikiri.roadmap.domain.exception.RoadmapException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.regex.Pattern;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapReview extends BaseUpdatedTimeEntity {

    private static final int MIN_RATE = 0;
    private static final int MAX_RATE = 5;
    private static final int RATE_UNIT = 5;
    private static final String RATE_FORMAT = String.format("(%d(\\.%d)?|[%d-%d](\\.0|\\.%d)?)", MAX_RATE, MIN_RATE,
            MIN_RATE, MAX_RATE - 1, RATE_UNIT);
    private static final int CONTENT_MAX_LENGTH = 1000;

    @Column(length = 1200)
    private String content;

    @Column(nullable = false)
    private Double rate = 0.0;

    private Long memberId;

    private Long roadmapId;

    public RoadmapReview(final String content, final Double rate, final Long memberId, final Long roadmapId) {
        if (content != null) {
            validate(content, rate);
        }
        this.content = content;
        this.rate = rate;
        this.memberId = memberId;
        this.roadmapId = roadmapId;
    }

    private void validate(final String content, final Double rate) {
        validateContentLength(content);
        validateRate(rate);
    }

    private void validateContentLength(final String content) {
        if (content.length() > CONTENT_MAX_LENGTH) {
            throw new RoadmapException(String.format("리뷰는 최대 %d글자까지 입력할 수 있습니다.", CONTENT_MAX_LENGTH));
        }
    }

    private void validateRate(final Double rate) {
        if (!Pattern.matches(RATE_FORMAT, String.valueOf(rate))) {
            throw new RoadmapException(String.format("별점은 %d부터 %d까지 0.%d 단위로 설정할 수 있습니다.",
                    MIN_RATE, MAX_RATE, RATE_UNIT));
        }
    }

    public String getContent() {
        return content;
    }

    public Double getRate() {
        return rate;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }
}

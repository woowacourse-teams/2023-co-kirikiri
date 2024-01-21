package co.kirikiri.roadmap.domain;

import co.kirikiri.domain.BaseCreatedTimeEntity;
import co.kirikiri.roadmap.domain.exception.RoadmapException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Roadmap extends BaseCreatedTimeEntity {

    private static final int TITLE_MIN_LENGTH = 1;
    private static final int TITLE_MAX_LENGTH = 40;
    private static final int INTRODUCTION_MIN_LENGTH = 1;
    private static final int INTRODUCTION_MAX_LENGTH = 150;
    private static final int REQUIRED_MIN_PERIOD = 0;
    private static final int REQUIRED_MAX_PERIOD = 1000;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 200, nullable = false)
    private String introduction;

    @Column(nullable = false)
    private Integer requiredPeriod;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RoadmapDifficulty difficulty;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 10, nullable = false)
    private RoadmapStatus status = RoadmapStatus.CREATED;

    private Long creatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private RoadmapCategory category;

    @Embedded
    private RoadmapTags tags = new RoadmapTags();

    public Roadmap(final String title, final String introduction, final int requiredPeriod, final RoadmapDifficulty difficulty,
                   final Long creatorId, final RoadmapCategory category, final RoadmapTags tags) {
        this(null, title, introduction, requiredPeriod, difficulty, RoadmapStatus.CREATED, creatorId, category, tags);
    }

    public Roadmap(final Long id, final String title, final String introduction, final Integer requiredPeriod,
                   final RoadmapDifficulty difficulty, final RoadmapStatus status, final Long creatorId,
                   final RoadmapCategory category, final RoadmapTags tags) {
        validate(title, introduction, requiredPeriod);
        this.id = id;
        this.title = title;
        this.introduction = introduction;
        this.requiredPeriod = requiredPeriod;
        this.difficulty = difficulty;
        this.status = status;
        this.creatorId = creatorId;
        this.category = category;
        this.tags = tags;
    }

    private void validate(final String title, final String introduction, final int requiredPeriod) {
        validateTitleLength(title);
        validateIntroductionLength(introduction);
        validateRequiredPeriod(requiredPeriod);
    }

    private void validateTitleLength(final String title) {
        if (title.length() < TITLE_MIN_LENGTH || title.length() > TITLE_MAX_LENGTH) {
            throw new RoadmapException(
                    String.format("로드맵 제목의 길이는 최소 %d글자, 최대 %d글자입니다.", TITLE_MIN_LENGTH, TITLE_MAX_LENGTH)
            );
        }
    }

    private void validateIntroductionLength(final String introduction) {
        if (introduction.length() < INTRODUCTION_MIN_LENGTH || introduction.length() > INTRODUCTION_MAX_LENGTH) {
            throw new RoadmapException(
                    String.format("로드맵 소개글의 길이는 최소 %d글자, 최대 %d글자입니다.",
                            INTRODUCTION_MIN_LENGTH, INTRODUCTION_MAX_LENGTH
                    )
            );
        }
    }

    private void validateRequiredPeriod(final int requiredPeriod) {
        if (requiredPeriod < REQUIRED_MIN_PERIOD || requiredPeriod > REQUIRED_MAX_PERIOD) {
            throw new RoadmapException(
                    String.format("로드맵 추천 소요 기간은 최소 %d일, 최대 %d일입니다.", REQUIRED_MIN_PERIOD, REQUIRED_MAX_PERIOD)
            );
        }
    }

    public boolean isCreator(final Long memberId) {
        return Objects.equals(creatorId, memberId);
    }

    public void delete() {
        this.status = RoadmapStatus.DELETED;
    }

    public boolean isDeleted() {
        return status == RoadmapStatus.DELETED;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public String getTitle() {
        return title;
    }

    public RoadmapCategory getCategory() {
        return category;
    }

    public String getIntroduction() {
        return introduction;
    }

    public Integer getRequiredPeriod() {
        return requiredPeriod;
    }

    public RoadmapDifficulty getDifficulty() {
        return difficulty;
    }

    public RoadmapTags getTags() {
        return tags;
    }
}

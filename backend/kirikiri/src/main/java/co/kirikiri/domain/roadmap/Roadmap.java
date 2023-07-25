package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.BaseEntity;
import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Roadmap extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private RoadmapCategory category;

    @Embedded
    private RoadmapContents contents = new RoadmapContents();

    @Embedded
    private RoadmapTags tags = new RoadmapTags();

    public Roadmap(final String title, final String introduction, final int requiredPeriod,
                   final RoadmapDifficulty difficulty, final Member creator, final RoadmapCategory category) {
        validate(title, introduction, requiredPeriod);
        this.title = title;
        this.introduction = introduction;
        this.requiredPeriod = requiredPeriod;
        this.difficulty = difficulty;
        this.creator = creator;
        this.category = category;
    }

    public Roadmap(final Long id, final String title, final String introduction, final Integer requiredPeriod,
                   final RoadmapDifficulty difficulty, final Member creator, final RoadmapCategory category) {
        this.id = id;
        this.title = title;
        this.introduction = introduction;
        this.requiredPeriod = requiredPeriod;
        this.difficulty = difficulty;
        this.creator = creator;
        this.category = category;
    }

    private void validate(final String title, final String introduction, final int requiredPeriod) {
        validateTitleLength(title);
        validateIntroductionLength(introduction);
        validateRequiredPeriod(requiredPeriod);
    }

    private void validateTitleLength(final String title) {
        if (title.length() < TITLE_MIN_LENGTH || title.length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException(
                    String.format("로드맵 제목의 길이는 최소 %d글자, 최대 %d글자입니다.", TITLE_MIN_LENGTH, TITLE_MAX_LENGTH)
            );
        }
    }

    private void validateIntroductionLength(final String introduction) {
        if (introduction.length() < INTRODUCTION_MIN_LENGTH || introduction.length() > INTRODUCTION_MAX_LENGTH) {
            throw new BadRequestException(
                    String.format("로드맵 소개글의 길이는 최소 %d글자, 최대 %d글자입니다.",
                            INTRODUCTION_MIN_LENGTH, INTRODUCTION_MAX_LENGTH
                    )
            );
        }
    }

    private void validateRequiredPeriod(final int requiredPeriod) {
        if (requiredPeriod < REQUIRED_MIN_PERIOD || requiredPeriod > REQUIRED_MAX_PERIOD) {
            throw new BadRequestException(
                    String.format("로드맵 추천 소요 기간은 최소 %d일, 최대 %d일입니다.", REQUIRED_MIN_PERIOD, REQUIRED_MAX_PERIOD)
            );
        }
    }

    public void addContent(final RoadmapContent content) {
        contents.add(content);
        if (content.isNotSameRoadmap(this)) {
            content.updateRoadmap(this);
        }
    }

    public void addTags(final RoadmapTags tags) {
        this.tags = tags;
    }

    public void delete() {
        this.status = RoadmapStatus.DELETED;
    }

    public Member getCreator() {
        return creator;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public RoadmapCategory getCategory() {
        return category;
    }

    public RoadmapContents getContents() {
        return contents;
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
}

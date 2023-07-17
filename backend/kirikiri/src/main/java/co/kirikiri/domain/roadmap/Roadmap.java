package co.kirikiri.domain.roadmap;

import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Roadmap {

    private static final int TITLE_MIN_LENGTH = 1;
    private static final int TITLE_MAX_LENGTH = 40;
    private static final int INTRODUCTION_MIN_LENGTH = 1;
    private static final int INTRODUCTION_MAX_LENGTH = 150;
    private static final int REQUIRED_MIN_PERIOD = 0;
    private static final int REQUIRED_MAX_PERIOD = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @JoinColumn(name = "member_id")
    private Member creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private RoadmapCategory category;

    @Embedded
    private RoadmapContents contents = new RoadmapContents();

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

    private void validate(final String title, final String introduction, final int requiredPeriod) {
        if (title.length() < TITLE_MIN_LENGTH || title.length() > TITLE_MAX_LENGTH) {
            throw new BadRequestException(
                    "로드맵 제목의 길이는 최소 " + TITLE_MIN_LENGTH + "글자, 최대 " + TITLE_MAX_LENGTH + "글자입니다.");
        }
        if (introduction.length() < INTRODUCTION_MIN_LENGTH || introduction.length() > INTRODUCTION_MAX_LENGTH) {
            throw new BadRequestException(
                    "로드맵 소개글의 길이는 최소 " + INTRODUCTION_MIN_LENGTH + "글자, 최대 " + INTRODUCTION_MAX_LENGTH + "글자입니다.");
        }
        if (requiredPeriod < REQUIRED_MIN_PERIOD || requiredPeriod > REQUIRED_MAX_PERIOD) {
            throw new BadRequestException(
                    "로드맵 추천 소요 기간은 최소 " + REQUIRED_MIN_PERIOD + "일, 최대 " + REQUIRED_MAX_PERIOD + "일입니다.");
        }
    }

    public void addContent(final RoadmapContent content) {
        contents.add(content);
        if (content.isNotSameRoadmap(this)) {
            content.updateRoadmap(this);
        }
    }

    public Long getId() {
        return id;
    }

    public RoadmapContents getContents() {
        return contents;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Roadmap roadmap = (Roadmap) o;
        return Objects.equals(id, roadmap.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

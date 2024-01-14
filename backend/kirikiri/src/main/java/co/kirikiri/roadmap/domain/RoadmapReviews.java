package co.kirikiri.roadmap.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapReviews {

    //    @OneToMany(fetch = FetchType.LAZY,
//            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
//            orphanRemoval = true, mappedBy = "roadmap")
    private List<RoadmapReview> values = new ArrayList<>();

    public void add(final RoadmapReview review) {
        this.values.add(review);
    }
}

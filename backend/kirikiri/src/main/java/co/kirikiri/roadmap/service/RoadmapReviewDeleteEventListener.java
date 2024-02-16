package co.kirikiri.roadmap.service;

import co.kirikiri.roadmap.persistence.RoadmapReviewRepository;
import co.kirikiri.roadmap.service.event.RoadmapDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadmapReviewDeleteEventListener {

    private final RoadmapReviewRepository roadmapReviewRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @EventListener(condition = "#roadmapDeleteEvent.target == 'Review'")
    public void handleRoadmapReviewDelete(final RoadmapDeleteEvent roadmapDeleteEvent) {
        deleteRoadmapReviews(roadmapDeleteEvent.roadmapId());
    }

    private void deleteRoadmapReviews(final Long roadmapId) {
        roadmapReviewRepository.deleteAllByRoadmapId(roadmapId);
        applicationEventPublisher.publishEvent(new RoadmapDeleteEvent(roadmapId, "Roadmap"));
    }
}

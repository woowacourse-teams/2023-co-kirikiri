package co.kirikiri.integration.helper;

import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.service.FileService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    public TestConfig(final RoadmapCategoryRepository roadmapCategoryRepository,
                      final GoalRoomRepository goalRoomRepository,
                      final GoalRoomMemberRepository goalRoomMemberRepository) {
        this.roadmapCategoryRepository = roadmapCategoryRepository;
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    @Bean
    public FileService fileService() {
        return new TestFileService();
    }

    @Bean
    public TestTransactionService testTransactionService() {
        return new TestTransactionService(roadmapCategoryRepository, goalRoomRepository, goalRoomMemberRepository);
    }
}

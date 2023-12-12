package co.kirikiri.integration.helper;

import co.kirikiri.auth.persistence.RefreshTokenRepository;
import co.kirikiri.common.service.FileService;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    public TestConfig(final GoalRoomRepository goalRoomRepository,
                      final GoalRoomMemberRepository goalRoomMemberRepository) {
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    @Bean
    public FileService fileService() {
        return new TestFileService();
    }

    @Bean
    public TestTransactionService testTransactionService() {
        return new TestTransactionService(goalRoomRepository, goalRoomMemberRepository);
    }

    @Bean
    public RefreshTokenRepository refreshTokenRepository() {
        return new TestRefreshTokenRepository();
    }
}

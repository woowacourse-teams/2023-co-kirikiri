package co.kirikiri.integration.helper;

import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.FileService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;

    public TestConfig(final RoadmapContentRepository roadmapContentRepository, final MemberRepository memberRepository) {
        this.roadmapContentRepository = roadmapContentRepository;
        this.memberRepository = memberRepository;
    }

    @Bean
    public FileService fileService() {
        return new TestFileService();
    }

    @Bean
    public TestTransactionService testTransactionService() {
        return new TestTransactionService(roadmapContentRepository, memberRepository);
    }
}

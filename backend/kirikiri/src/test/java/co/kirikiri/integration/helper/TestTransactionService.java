package co.kirikiri.integration.helper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import org.springframework.transaction.annotation.Transactional;

public class TestTransactionService {

    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;

    public TestTransactionService(final RoadmapContentRepository roadmapContentRepository, final MemberRepository memberRepository) {
        this.roadmapContentRepository = roadmapContentRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public RoadmapContent findRoadmapById(final Long id) {
        final RoadmapContent roadmapContent = roadmapContentRepository.findById(id).orElseThrow();
        roadmapContent.getNodes().getValues();
        return roadmapContent;
    }

    @Transactional
    public Member findMemberById(final Long id) {
        final Member member = memberRepository.findById(id).orElseThrow();
        member.getImage().getServerFilePath();
        return member;
    }
}

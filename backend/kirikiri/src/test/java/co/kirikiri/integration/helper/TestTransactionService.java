package co.kirikiri.integration.helper;

import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.골룸을_생성하고_아이디를_반환한다;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.십일_후;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.오늘;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_노드_인증_횟수;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_이름;
import static co.kirikiri.integration.fixture.GoalRoomAPIFixture.정상적인_골룸_제한_인원;
import static co.kirikiri.integration.fixture.MemberAPIFixture.DEFAULT_PASSWORD;
import static co.kirikiri.integration.helper.InitIntegrationTest.기본_로그인_토큰;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomRoadmapNodeRequest;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestTransactionService {

    @PersistenceContext
    private EntityManager em;

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    public TestTransactionService(final GoalRoomRepository goalRoomRepository,
                                  final GoalRoomMemberRepository goalRoomMemberRepository) {
        this.goalRoomRepository = goalRoomRepository;
        this.goalRoomMemberRepository = goalRoomMemberRepository;
    }

    public GoalRoom 골룸을_완료시킨다(final Long 골룸_아이디) {
        final GoalRoom 골룸 = goalRoomRepository.findById(골룸_아이디).get();
        골룸.complete();
        return goalRoomRepository.save(골룸);
    }

    public GoalRoom 완료한_골룸을_생성한다(final RoadmapResponse 로드맵_응답) {
        final List<GoalRoomRoadmapNodeRequest> 골룸_노드_별_기간_요청 = List.of(
                new GoalRoomRoadmapNodeRequest(로드맵_응답.content().nodes().get(0).id(), 정상적인_골룸_노드_인증_횟수, 오늘, 십일_후));
        final GoalRoomCreateRequest 골룸_생성_요청 = new GoalRoomCreateRequest(로드맵_응답.roadmapId(), 정상적인_골룸_이름, 정상적인_골룸_제한_인원,
                골룸_노드_별_기간_요청);
        final Long 골룸_아이디 = 골룸을_생성하고_아이디를_반환한다(골룸_생성_요청, 기본_로그인_토큰);
        return 골룸을_완료시킨다(골룸_아이디);
    }

    public void 골룸에_대한_참여자_리스트를_생성한다(final MemberInformationResponse 리더_정보, final GoalRoom 골룸,
                                     final MemberInformationResponse... 팔로워들_정보) {
        final Member 리더 = 사용자_정보에서_사용자를_생성한다(리더_정보);
        final GoalRoomMember 골룸_멤버_리더 = new GoalRoomMember(GoalRoomRole.LEADER,
                LocalDateTime.of(2023, 7, 1, 12, 0), 골룸, 리더);
        final List<GoalRoomMember> 골룸_멤버_리스트 = new ArrayList<>();
        골룸_멤버_리스트.add(골룸_멤버_리더);

        for (final MemberInformationResponse 팔로워_정보 : 팔로워들_정보) {
            final Member 팔로워 = 사용자_정보에서_사용자를_생성한다(팔로워_정보);
            final GoalRoomMember 골룸_멤버_팔로워 = new GoalRoomMember(GoalRoomRole.FOLLOWER,
                    LocalDateTime.of(2023, 7, 5, 18, 0), 골룸, 팔로워);
            골룸_멤버_리스트.add(골룸_멤버_팔로워);
        }
        골룸_멤버를_저장한다(골룸_멤버_리스트);
    }

    private Member 사용자_정보에서_사용자를_생성한다(final MemberInformationResponse 사용자_정보) {
        final MemberProfile memberProfile = new MemberProfile(Gender.valueOf(사용자_정보.gender()), 사용자_정보.email());
        return new Member(사용자_정보.id(), new Identifier(사용자_정보.identifier()), null, new EncryptedPassword(new Password(
                DEFAULT_PASSWORD)), new Nickname(사용자_정보.nickname()), null, memberProfile);
    }

    public void 골룸_멤버를_저장한다(final List<GoalRoomMember> 골룸_멤버_리스트) {
        goalRoomMemberRepository.saveAllInBatch(골룸_멤버_리스트);
    }

    public void 골룸의_상태와_종료날짜를_변경한다(final Long 골룸_아이디, final GoalRoomStatus 골룸_상태, final LocalDate 변경할_종료날짜) {
        em.createQuery("update GoalRoom g set endDate = :endDate, status = :status where id = :goalRoomId")
                .setParameter("status", 골룸_상태)
                .setParameter("endDate", 변경할_종료날짜)
                .setParameter("goalRoomId", 골룸_아이디)
                .executeUpdate();
    }

    public void 골룸의_종료날짜를_변경한다(final Long 골룸_아이디, final LocalDate 변경할_종료날짜) {
        em.createQuery("update GoalRoom g set endDate = :endDate where id = :goalRoomId")
                .setParameter("goalRoomId", 골룸_아이디)
                .setParameter("endDate", 변경할_종료날짜)
                .executeUpdate();
    }

    public void 골룸의_시작날짜를_변경한다(final Long 골룸_아이디, final LocalDate 변경할_시작날짜) {
        em.createQuery("update GoalRoom g set startDate = :startDate where id = :goalRoomId")
                .setParameter("goalRoomId", 골룸_아이디)
                .setParameter("startDate", 변경할_시작날짜)
                .executeUpdate();
    }
}

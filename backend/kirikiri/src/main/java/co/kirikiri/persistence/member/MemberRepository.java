package co.kirikiri.persistence.member;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    Optional<Member> findByIdentifier(final Identifier identifier);

    Optional<Member> findByNickname(final Nickname nickname);

    Optional<Member> findByOauthId(final String oauthId);
}

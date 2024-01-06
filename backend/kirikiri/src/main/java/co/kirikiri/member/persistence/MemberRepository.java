package co.kirikiri.member.persistence;

import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

    Optional<Member> findByIdentifier(final Identifier identifier);

    Optional<Member> findByNickname(final Nickname nickname);

    Optional<Member> findByOauthId(final String oauthId);
}

package co.kirikiri.persistence.member;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdentifier(final Identifier identifier);
}

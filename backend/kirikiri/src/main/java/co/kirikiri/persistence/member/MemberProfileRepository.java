package co.kirikiri.persistence.member;

import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByNickname(final Nickname nickname);
}

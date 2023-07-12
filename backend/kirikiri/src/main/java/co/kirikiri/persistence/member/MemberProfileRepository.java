package co.kirikiri.persistence.member;

import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Nickname;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByNickname(Nickname nickname);
}

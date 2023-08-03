package co.kirikiri.persistence.member;

import co.kirikiri.domain.member.Member;
import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findWithMemberProfileAndImageByIdentifier(final String identifier);

    Optional<Member> findWithMemberProfileAndImageById(final Long memberId);
}

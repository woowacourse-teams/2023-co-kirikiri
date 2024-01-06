package co.kirikiri.member.persistence;

import co.kirikiri.member.domain.Member;
import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findWithMemberProfileAndImageByIdentifier(final String identifier);

    Optional<Member> findWithMemberProfileAndImageById(final Long memberId);
}

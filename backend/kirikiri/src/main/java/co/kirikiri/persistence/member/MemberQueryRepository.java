package co.kirikiri.persistence.member;

import co.kirikiri.domain.member.Member;

public interface MemberQueryRepository {

    Member findWithMemberProfileAndImageById(final Long memberId);
}

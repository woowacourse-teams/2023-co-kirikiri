package co.kirikiri.persistence.member;

import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberImage.memberImage;
import static co.kirikiri.domain.member.QMemberProfile.memberProfile;

import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.QuerydslRepositorySupporter;

public class MemberQueryRepositoryImpl extends QuerydslRepositorySupporter implements MemberQueryRepository {

    public MemberQueryRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public Member findWithMemberProfileAndImageById(final Long memberId) {
        return selectFrom(member)
                .innerJoin(member.memberProfile, memberProfile)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(member.id.eq(memberId))
                .fetchOne();
    }
}

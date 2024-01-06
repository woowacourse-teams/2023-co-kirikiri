package co.kirikiri.member.persistence;

import static co.kirikiri.member.domain.QMember.member;
import static co.kirikiri.member.domain.QMemberImage.memberImage;
import static co.kirikiri.member.domain.QMemberProfile.memberProfile;

import co.kirikiri.member.domain.Member;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import java.util.Optional;

public class MemberQueryRepositoryImpl extends QuerydslRepositorySupporter implements MemberQueryRepository {

    public MemberQueryRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public Optional<Member> findWithMemberProfileAndImageByIdentifier(final String identifier) {
        return Optional.ofNullable(selectFrom(member)
                .innerJoin(member.memberProfile, memberProfile)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(member.identifier.value.eq(identifier))
                .fetchOne());
    }

    @Override
    public Optional<Member> findWithMemberProfileAndImageById(final Long memberId) {
        return Optional.ofNullable(selectFrom(member)
                .innerJoin(member.memberProfile, memberProfile)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(member.id.eq(memberId))
                .fetchOne());
    }
}

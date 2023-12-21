package co.kirikiri.persistence.member;

import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberImage.memberImage;
import static co.kirikiri.domain.member.QMemberProfile.memberProfile;

import co.kirikiri.common.persistence.QuerydslRepositorySupporter;
import co.kirikiri.domain.member.Member;
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

    @Override
    public Optional<Member> findWithMemberImageById(final Long memberId) {
        return Optional.ofNullable(selectFrom(member)
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(member.id.eq(memberId))
                .fetchOne());
    }
}

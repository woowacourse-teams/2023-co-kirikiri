import { PropsWithChildren } from 'react';
import * as S from './PageSection.styles';

type CreateGoalRoomPageSectionProps = {
  title: string;
  isRequired: boolean;
  description: string;
} & PropsWithChildren;

const PageSection = (props: Partial<CreateGoalRoomPageSectionProps>) => {
  const { title, isRequired, description, children } = props;

  return (
    <S.PageSection>
      {title && <S.SectionTitle isRequired={Boolean(isRequired)}>{title}</S.SectionTitle>}
      {description && <S.Description>{description}</S.Description>}
      <S.ChildrenWrapper>{children}</S.ChildrenWrapper>
    </S.PageSection>
  );
};

export default PageSection;

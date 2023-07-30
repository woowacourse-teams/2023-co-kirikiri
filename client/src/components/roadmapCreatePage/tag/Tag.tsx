import * as S from './tag.styles';
import InputDescription from '../input/inputDescription/InputDescription';
import InputLabel from '../input/inputLabel/InputLebel';
import TagItem from './TagItem';
import { useCreateTag } from '@/hooks/roadmap/useCreateTag';

const Tag = () => {
  const { tags, ref, getTagText, checkTagCountUnderFive } = useCreateTag();

  return (
    <S.Container>
      <InputLabel text='태그' />
      <InputDescription text='컨텐츠와 어울리는 태그를 작성해주세요' />
      <S.TagWrapper>
        {tags.map((item) => (
          <TagItem key={item} value={item} readOnly />
        ))}
        <TagItem ref={ref} />
        {checkTagCountUnderFive() && <S.AddButton onClick={getTagText}>+</S.AddButton>}
      </S.TagWrapper>
    </S.Container>
  );
};

export default Tag;

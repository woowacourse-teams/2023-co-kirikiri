import * as S from './tag.styles';
import InputDescription from '../input/inputDescription/InputDescription';
import InputLabel from '../input/inputLabel/InputLebel';
import TagItem from './TagItem';
import { useCreateTag } from '@/hooks/roadmap/useCreateTag';
import { useEffect } from 'react';

type TagProps = {
  getTags: (tags: string[]) => void;
};
const Tag = ({ getTags }: TagProps) => {
  const { tags, ref, getAddedTagText, checkIsTagCountMax } = useCreateTag();

  useEffect(() => {
    getTags(tags);
  }, [tags]);

  return (
    <S.Container>
      <InputLabel text='태그' />
      <InputDescription text='컨텐츠와 어울리는 태그를 작성해주세요' />
      <S.TagWrapper>
        {tags.map((item) => (
          <S.AddedTagItem key={item}>{item}</S.AddedTagItem>
        ))}
        <TagItem ref={ref} />
        {checkIsTagCountMax() && <S.AddButton onClick={getAddedTagText}>+</S.AddButton>}
      </S.TagWrapper>
    </S.Container>
  );
};

export default Tag;

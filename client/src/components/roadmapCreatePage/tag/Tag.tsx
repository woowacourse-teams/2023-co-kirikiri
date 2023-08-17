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
  const { tags, ref, addTagByButton, addTagByEnter, checkIsTagCountMax, deleteTag } =
    useCreateTag();

  useEffect(() => {
    getTags(tags);
  }, [tags]);

  return (
    <S.Container>
      <InputLabel text='태그' />
      <InputDescription text='컨텐츠와 어울리는 태그를 작성해주세요' />
      <S.TagWrapper>
        {tags.map((item) => (
          <>
            <S.AddedTagItem key={item}># {item}</S.AddedTagItem>
            <S.DeleteButton value={item} onClick={deleteTag}>
              X
            </S.DeleteButton>
          </>
        ))}
        <TagItem ref={ref} addTagByEnter={addTagByEnter} placeholder='# 태그명' />
        {checkIsTagCountMax() && <S.AddButton onClick={addTagByButton}>+</S.AddButton>}
      </S.TagWrapper>
    </S.Container>
  );
};

export default Tag;

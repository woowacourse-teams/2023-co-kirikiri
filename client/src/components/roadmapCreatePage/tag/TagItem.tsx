import { useValidateInput } from '@/hooks/_common/useValidateInput';
import { forwardRef, InputHTMLAttributes } from 'react';
import * as S from './tag.styles';

type TagItemProps = InputHTMLAttributes<HTMLInputElement>;

const TAG_MAX_LENGTH = { rule: /^.{1,10}$/, message: '1글자부터 5글자까지 작성해주세요' };
const TagItem = forwardRef<HTMLInputElement, TagItemProps>((props, ref) => {
  const { handleInputChange, value } = useValidateInput(TAG_MAX_LENGTH);

  return (
    <S.TagItem width={props.readOnly ? String(props.value).length : value.length}>
      <S.TagInputField
        onChange={handleInputChange}
        maxLength={10}
        value={props.value}
        ref={ref}
        readOnly={props.readOnly}
      />
    </S.TagItem>
  );
});

export default TagItem;

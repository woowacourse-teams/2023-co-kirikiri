import { TAG_MAX_LENGTH } from '@/constants/roadmap/regex';
import { TAG_ITEM_MAX_LENGTH } from '@/constants/roadmap/tag';
import { useValidateInput } from '@/hooks/_common/useValidateInput';
import { forwardRef, InputHTMLAttributes } from 'react';
import * as S from './tag.styles';

type TagItemProps = InputHTMLAttributes<HTMLInputElement>;

const TagItem = forwardRef<HTMLInputElement, TagItemProps>((props, ref) => {
  const { handleInputChange, value } = useValidateInput(TAG_MAX_LENGTH);

  return (
    <S.TagItem width={props.readOnly ? String(props.value).length : value.length}>
      <S.TagInputField
        onChange={handleInputChange}
        maxLength={TAG_ITEM_MAX_LENGTH}
        value={props.value}
        ref={ref}
        readOnly={props.readOnly}
      />
    </S.TagItem>
  );
});

export default TagItem;

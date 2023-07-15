import { useSelect } from '@/hooks/_common/useSelect';
import {
  Description,
  Label,
  Option,
  OptionGroup,
  SelectBox,
} from '../selector/selectBox';
import { S } from './category.style';

// 임시 더미데이터
type DummyCategoryType = {
  [key: string]: string;
};

const DummyCategory: DummyCategoryType = {
  1: '어학',
  2: 'IT',
  3: '시험',
  4: '운동',
  5: '게임',
  6: '음악',
  7: '라이프',
  8: '여가',
  9: '기타',
} as const;

const Category = () => {
  const { selectOption } = useSelect<number>();

  return (
    <SelectBox defaultOpen externalSelectState={selectOption}>
      <Label asChild>
        <S.CategoryLabel>
          카테고리<p>*</p>
        </S.CategoryLabel>
      </Label>
      <Description asChild>
        <S.CategoryDescription>
          컨텐츠에 어울리는 카테고리를 선택해주세요.
        </S.CategoryDescription>
      </Description>
      <OptionGroup asChild>
        <S.Wrapper>
          {Object.keys(DummyCategory).map((categoryId) => {
            return (
              <Option id={Number(categoryId)} asChild>
                <S.CategoryBox>{DummyCategory[Number(categoryId)]}</S.CategoryBox>
              </Option>
            );
          })}
        </S.Wrapper>
      </OptionGroup>
    </SelectBox>
  );
};

export default Category;

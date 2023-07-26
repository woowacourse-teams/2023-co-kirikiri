import {
  CategoriesInfo,
  getKeysOfCategoryInfo,
  invariantOf,
} from '@/constants/roadmap/category';
import { useSelect } from '@/hooks/_common/useSelect';
import { useEffect } from 'react';
import { Select, SelectBox } from '../selector/selectBox';
import { S } from './category.styles';

// 임시 더미데이터
export type DummyCategoryType = {
  [key: number]: string;
};

type CategoryProps = {
  getSelectedCategoryId: (category: keyof DummyCategoryType | null) => void;
};

const Category = ({ getSelectedCategoryId }: CategoryProps) => {
  const { selectOption, selectedOption } = useSelect<number>();

  useEffect(() => {
    getSelectedCategoryId(selectedOption);
  }, [selectedOption]);

  return (
    <SelectBox defaultOpen externalSelectState={selectOption}>
      <Select.Label asChild>
        <S.CategoryLabel>
          카테고리<p>*</p>
        </S.CategoryLabel>
      </Select.Label>
      <Select.Description asChild>
        <S.CategoryDescription>
          컨텐츠에 어울리는 카테고리를 선택해주세요.
        </S.CategoryDescription>
      </Select.Description>
      <Select.OptionGroup asChild>
        <S.Wrapper>
          {getKeysOfCategoryInfo(invariantOf(CategoriesInfo)).map((categoryId) => {
            return (
              <Select.Option id={Number(categoryId)} asChild>
                <S.CategoryBox>{CategoriesInfo[categoryId].name}</S.CategoryBox>
              </Select.Option>
            );
          })}
        </S.Wrapper>
      </Select.OptionGroup>
    </SelectBox>
  );
};

export default Category;

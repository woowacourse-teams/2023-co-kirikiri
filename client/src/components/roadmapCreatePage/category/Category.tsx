import { CategoriesInfo } from '@/constants/roadmap/category';
import { useSelect } from '@/hooks/_common/useSelect';
import { getInvariantObjectKeys, invariantOf } from '@/utils/_common/invariantType';
import { useEffect } from 'react';
import { Select } from 'ck-util-components';
import { S } from './category.styles';

type CategoryProps = {
  getSelectedCategoryId: (category: keyof typeof CategoriesInfo) => void;
};

const Category = ({ getSelectedCategoryId }: CategoryProps) => {
  const { selectOption, selectedOption } = useSelect<keyof typeof CategoriesInfo>();

  useEffect(() => {
    if (selectedOption !== null) {
      getSelectedCategoryId(selectedOption);
    }
  }, [selectedOption]);

  return (
    <Select
      defaultOpen
      externalSelectedOption={selectedOption}
      onSelectChange={selectOption}
    >
      <S.CategoryLabel>
        카테고리<p>*</p>
      </S.CategoryLabel>
      <S.CategoryDescription>
        컨텐츠에 어울리는 카테고리를 선택해주세요.
      </S.CategoryDescription>
      <Select.OptionGroup asChild>
        <S.Wrapper>
          {getInvariantObjectKeys(invariantOf(CategoriesInfo)).map((categoryId) => {
            return (
              <Select.Option id={Number(categoryId)} asChild>
                <S.CategoryBox>{CategoriesInfo[categoryId].name}</S.CategoryBox>
              </Select.Option>
            );
          })}
        </S.Wrapper>
      </Select.OptionGroup>
    </Select>
  );
};

export default Category;

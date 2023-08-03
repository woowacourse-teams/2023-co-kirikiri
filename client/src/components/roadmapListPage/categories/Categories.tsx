import { MouseEvent } from 'react';
import type { CategoryType, SelectedCategoryId } from '@myTypes/roadmap/internal';
import { CategoriesInfo } from '@constants/roadmap/category';
import SVGIcon from '@components/icons/SVGIcon';
import * as S from './Categories.styles';

type CategoriesProps = {
  selectedCategoryId: SelectedCategoryId;
  selectCategory: ({ currentTarget }: MouseEvent<HTMLDivElement>) => void;
};

const Categories = ({ selectedCategoryId, selectCategory }: CategoriesProps) => {
  const categories = Object.values(CategoriesInfo);
  const upCategories = categories.slice(0, 5);
  const downCategories = categories.slice(5);

  const renderCategory = ({ name, id }: CategoryType) => {
    return (
      <S.Category
        key={id}
        id={String(id)}
        onClick={selectCategory}
        selected={selectedCategoryId === id}
      >
        <SVGIcon name={CategoriesInfo[id].iconName} />
        <S.CategoryName>{name}</S.CategoryName>
      </S.Category>
    );
  };

  return (
    <S.Categories>
      <S.CategoriesRow>{upCategories.map(renderCategory)}</S.CategoriesRow>
      <S.CategoriesRow>{downCategories.map(renderCategory)}</S.CategoriesRow>
    </S.Categories>
  );
};

export default Categories;

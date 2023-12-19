import { MouseEvent } from 'react';
import type { CategoryType, SelectedCategoryId } from '@myTypes/roadmap/internal';
import { CategoriesInfo } from '@constants/roadmap/category';
import SVGIcon from '@components/icons/SVGIcon';
import * as S from './Categories.styles';
import { useNavigate } from 'react-router-dom';

type CategoriesProps = {
  selectedCategoryId: SelectedCategoryId;
  selectCategory: ({ currentTarget }: MouseEvent<HTMLButtonElement>) => void;
};

const Categories = ({ selectedCategoryId, selectCategory }: CategoriesProps) => {
  const categories = Object.values(CategoriesInfo);
  const upCategories = categories.slice(0, 5);
  const downCategories = categories.slice(5);

  const navigate = useNavigate();

  const handleClickCategory = (categoryId: CategoryType['id']) => {
    const queryParams = new URLSearchParams();
    queryParams.set('category', String(categoryId));
    navigate(`?${queryParams}`);
  };

  const renderCategory = ({ name, id }: CategoryType) => {
    return (
      <S.Category
        key={id}
        id={`${name}-${id}`}
        onClick={() => handleClickCategory(id)}
        selected={selectedCategoryId === id}
        aria-label={name}
        role='button'
      >
        <SVGIcon name={CategoriesInfo[id].iconName} />
        <S.CategoryName>{name}</S.CategoryName>
      </S.Category>
    );
  };

  return (
    <S.Categories aria-label='카테고리 목록'>
      <S.CategoriesRow>{upCategories.map(renderCategory)}</S.CategoriesRow>
      <S.CategoriesRow>{downCategories.map(renderCategory)}</S.CategoriesRow>
    </S.Categories>
  );
};

export default Categories;

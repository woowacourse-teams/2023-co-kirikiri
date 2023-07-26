import type { CategoryType } from '@myTypes/roadmap';
import { CategoriesInfo } from '@constants/roadmap/category';
import SVGIcon from '@components/icons/SVGIcon';
import * as S from './Categories.styles';

const Categories = () => {
  const categories = Object.values(CategoriesInfo);
  const upCategories = categories.slice(0, 5);
  const downCategories = categories.slice(5);

  const renderCategory = ({ name, id, iconName }: CategoryType) => {
    return (
      <S.Category key={id}>
        <SVGIcon name={iconName} />
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

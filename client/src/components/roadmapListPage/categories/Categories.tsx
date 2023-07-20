import { CategoriesInfo } from '@constants/roadmap/Category';
import SVGIcon from '@components/icons/SVGIcon';
import * as S from './Categories.styles';

const Categories = ({ setDummyCategoryId }: any) => {
  const categories = Object.values(CategoriesInfo);
  const upCategories = categories.slice(0, 5);
  const downCategories = categories.slice(5);

  return (
    <S.Categories>
      <S.CategoriesRow>
        {upCategories.map(({ name, id, iconName }) => {
          return (
            <S.Category key={id} onClick={() => setDummyCategoryId(id)}>
              <SVGIcon name={iconName} />
              <S.CategoryName>{name}</S.CategoryName>
            </S.Category>
          );
        })}
      </S.CategoriesRow>
      <S.CategoriesRow>
        {downCategories.map(({ name, id, iconName }) => {
          return (
            <S.Category key={id} onClick={() => setDummyCategoryId(id)}>
              <SVGIcon name={iconName} />
              <S.CategoryName>{name}</S.CategoryName>
            </S.Category>
          );
        })}
      </S.CategoriesRow>
    </S.Categories>
  );
};

export default Categories;

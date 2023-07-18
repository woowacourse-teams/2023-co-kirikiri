import * as S from './Categories.styles';

const CategoriesInfo = [
  {
    name: '어학',
  },
  {
    name: 'IT',
  },
  {
    name: '시험',
  },
  {
    name: '운동',
  },
  {
    name: '게임',
  },
  {
    name: '음악',
  },
  {
    name: '라이프',
  },
  {
    name: '여가',
  },
  {
    name: '기타',
  },
] as const;

const Categories = () => {
  return (
    <S.Categories>
      <S.CategoriesRow>
        {CategoriesInfo.slice(4).map(({ name }) => {
          return (
            <S.Category key={name}>
              <div>
                <svg
                  xmlns='http://www.w3.org/2000/svg'
                  width='24'
                  height='24'
                  viewBox='0 0 24 24'
                >
                  <path
                    fill='currentColor'
                    d='M18.06 23h1.66c.84 0 1.53-.65 1.63-1.47L23 5.05h-5V1h-1.97v4.05h-4.97l.3 2.34c1.71.47 3.31 1.32 4.27 2.26c1.44 1.42 2.43 2.89 2.43 5.29V23M1 22v-1h15.03v1c0 .54-.45 1-1.03 1H2c-.55 0-1-.46-1-1m15.03-7C16.03 7 1 7 1 15h15.03M1 17h15v2H1v-2Z'
                  />
                </svg>
              </div>
              <S.CategoryName>{name}</S.CategoryName>
            </S.Category>
          );
        })}
      </S.CategoriesRow>
      <S.CategoriesRow>
        {CategoriesInfo.slice(5).map(({ name }) => {
          return (
            <S.Category key={name}>
              <div>
                <svg
                  xmlns='http://www.w3.org/2000/svg'
                  width='24'
                  height='24'
                  viewBox='0 0 24 24'
                >
                  <path
                    fill='currentColor'
                    d='M18.06 23h1.66c.84 0 1.53-.65 1.63-1.47L23 5.05h-5V1h-1.97v4.05h-4.97l.3 2.34c1.71.47 3.31 1.32 4.27 2.26c1.44 1.42 2.43 2.89 2.43 5.29V23M1 22v-1h15.03v1c0 .54-.45 1-1.03 1H2c-.55 0-1-.46-1-1m15.03-7C16.03 7 1 7 1 15h15.03M1 17h15v2H1v-2Z'
                  />
                </svg>
              </div>
              <S.CategoryName>{name}</S.CategoryName>
            </S.Category>
          );
        })}
      </S.CategoriesRow>
    </S.Categories>
  );
};

export default Categories;

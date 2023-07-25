import Category from '@/components/roadmapCreatePage/category/category';
import Description from '@/components/roadmapCreatePage/description/description';
import Difficulty from '@/components/roadmapCreatePage/difficulty/difficulty';
import MainText from '@/components/roadmapCreatePage/mainText/mainText';
import Period from '@/components/roadmapCreatePage/period/period';
import Title from '@/components/roadmapCreatePage/title/title';
import PageLayout from '@/components/_common/pageLayout/PageLayout';

const RoadmapCreatePage = () => {
  return (
    <PageLayout>
      <Category />
      <Title />
      <Description />
      <Difficulty />
      <MainText />
      <Period />
    </PageLayout>
  );
};

export default RoadmapCreatePage;

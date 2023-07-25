import Category from '@/components/roadmapCreatePage/category/category';
import Description from '@/components/roadmapCreatePage/description/description';
import Difficulty from '@/components/roadmapCreatePage/difficulty/difficulty';
import MainText from '@/components/roadmapCreatePage/mainText/mainText';
import Period from '@/components/roadmapCreatePage/period/period';
import Roadmap from '@/components/roadmapCreatePage/roadmap/roadmap';
import Title from '@/components/roadmapCreatePage/title/title';

const RoadmapCreatePage = () => {
  return (
    <>
      <Category />
      <Title />
      <Description />
      <Difficulty />
      <MainText />
      <Period />
      <Roadmap />
    </>
  );
};

export default RoadmapCreatePage;

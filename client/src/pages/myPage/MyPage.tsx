import Spinner from '@components/_common/spinner/Spinner';

import { Suspense } from 'react';
import MyPageContent from '@components/myPage/myPageContent/MyPageContent';

const MyPage = () => {
  return (
    <Suspense fallback={<Spinner />}>
      <MyPageContent />
    </Suspense>
  );
};

export default MyPage;

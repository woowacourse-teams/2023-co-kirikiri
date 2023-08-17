import React from 'react';
import * as S from './WavyLoading.styles';

type WavyLoadingProps = {
  loadMoreRef: React.MutableRefObject<null>;
};

const WavyLoading = ({ loadMoreRef }: WavyLoadingProps) => {
  return (
    <S.WavyLoading ref={loadMoreRef}>
      <div />
      <div />
      <div />
    </S.WavyLoading>
  );
};

export default WavyLoading;

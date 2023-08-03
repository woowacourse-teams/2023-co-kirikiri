import { PropsWithChildren } from 'react';
import InputLabel from '../input/inputLabel/InputLebel';

const Roadmap = ({ children }: PropsWithChildren) => {
  return (
    <>
      <InputLabel text='로드맵' />
      {children}
    </>
  );
};

export default Roadmap;

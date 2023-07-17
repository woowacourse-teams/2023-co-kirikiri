import { ChangeEvent, FormEvent, useState } from 'react';
import { MemberJoinRequest } from '@myTypes/user/remote';
import { useSignUp } from '@hooks/queries/user';

const SignUpForm = () => {
  const [signUpFormData, setSignUpFormData] = useState<MemberJoinRequest>({
    identifier: '',
    password: '',
    nickname: '',
    phoneNumber: '',
    genderType: '',
    birthDate: '',
  });

  const { signUp } = useSignUp(signUpFormData);

  const handleInputChange = (
    event: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setSignUpFormData({
      ...signUpFormData,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await signUp();
  };

  return (
    <div>
      <h1>SignUpPage</h1>
      <form onSubmit={handleSubmit}>
        <input name='identifier' onChange={handleInputChange} placeholder='Identifier' />
        <input
          name='password'
          onChange={handleInputChange}
          placeholder='Password'
          type='password'
        />
        <input name='nickname' onChange={handleInputChange} placeholder='Nickname' />
        <input
          name='phoneNumber'
          onChange={handleInputChange}
          placeholder='Phone Number'
        />
        <select name='genderType' onChange={handleInputChange}>
          <option value=''>Select Gender</option>
          <option value='male'>Male</option>
          <option value='female'>Female</option>
        </select>
        <input
          name='birthDate'
          onChange={handleInputChange}
          placeholder='Birth Date'
          type='date'
        />
        <button type='submit'>Sign Up</button>
      </form>
    </div>
  );
};

export default SignUpForm;

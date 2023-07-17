import { useState } from 'react';
import { UserLoginRequest } from '@myTypes/user/remote';
import { useLogin } from '@hooks/queries/user';

const LoginForm = () => {
  const [loginData, setLoginData] = useState<UserLoginRequest>({
    identifier: '',
    password: '',
  });

  const loginMutation = useLogin(loginData);

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({
      ...loginData,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    loginMutation.mutate();
  };

  return (
    <div>
      <h1>LoginPage</h1>
      <form onSubmit={handleSubmit}>
        <input name='identifier' onChange={handleInputChange} placeholder='Identifier' />
        <input
          name='password'
          onChange={handleInputChange}
          placeholder='Password'
          type='password'
        />
        <button type='submit'>Log In</button>
      </form>
    </div>
  );
};

export default LoginForm;

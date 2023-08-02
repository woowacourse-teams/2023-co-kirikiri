import { userHandler } from '@mocks/handlers/userHandler';
import goalRoomHandler from '@mocks/handlers/goalRoomHander';

export const handlers = [...userHandler, ...goalRoomHandler];

import { userHandler } from '@mocks/handlers/userHandler';
import { roadmapsHandler } from '@mocks/handlers/roadmapHandler';
import goalRoomHandler from '@mocks/handlers/goalRoomHander';

export const handlers = [...userHandler, ...goalRoomHandler, ...roadmapsHandler];

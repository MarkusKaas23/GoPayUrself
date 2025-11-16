import { PrismaClient } from '../generated/prisma/index.js';

export const prisma = new PrismaClient();

export async function disconnectPrisma() {
  await prisma.$disconnect();
  console.log('Prisma client disconnected');
}
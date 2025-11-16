import express from 'express';
import authRouter from './routes/auth.js';
import carsRouter from './routes/cars.js';
import statusRouter from './routes/status.js';
import usersRouter from './routes/users.js';
import { disconnectPrisma } from './utils/prisma.js';

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(express.json());

// Routes
app.use('/', statusRouter);
app.use('/', carsRouter);
app.use('/', authRouter);
app.use('/', usersRouter);

// Start the server
app.listen(PORT, async () => {
  console.log(`Server listening on port ${PORT}`);
});

// Graceful shutdown
process.on('SIGINT', async () => {
  console.log('Shutting down...');
  await disconnectPrisma();
  process.exit(0);
});

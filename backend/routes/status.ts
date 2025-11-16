import { Router, type Request, type Response } from 'express';

const router = Router();

// simple hello world status check
router.get('/status', (_req: Request, res: Response) => {
  res.status(200).send({ message: 'API is running!' });
});

export default router;
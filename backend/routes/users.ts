

import { Router, type Request, type Response } from 'express';
import { requireAuth, type AuthRequest } from '../utils/auth.js';
import { prisma } from '../utils/prisma.js';

const router = Router();

// GET user information
router.get('/users/me', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const user = await prisma.user.findUnique({
      where: { id: req.user!.id },
      select: { id: true, firstName: true, lastName: true, email: true },
    });

    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    res.status(200).json(user);
  } catch (error) {
    console.error('Error fetching user:', error);
    res.status(500).json({ error: 'Failed to fetch user' });
  }
});

export default router;

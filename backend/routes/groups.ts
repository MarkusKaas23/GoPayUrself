import { Router, type Request, type Response } from 'express';
import { z } from 'zod';
import { requireAuth, type AuthRequest } from '../utils/auth.js';
import { prisma } from '../utils/prisma.js';
import { createGroupSchema } from '../schemas/groups.js';

const router = Router();

// GET all groups for a specified user
router.get('/groups', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const { userId } = req.query;

    if (!userId || typeof userId !== 'string') {
      return res.status(400).json({ error: 'userId query parameter is required' });
    }

    const groups = await prisma.group.findMany({
      where: {
        OR: [
          { ownerId: userId },
          { members: { some: { userId } } }
        ]
      },
      include: {
        owner: { select: { id: true, firstName: true, lastName: true } },
        members: {
          include: {
            user: { select: { id: true, firstName: true, lastName: true } }
          }
        }
      }
    });

    res.status(200).json({ groups });
  } catch (error) {
    console.error('Error fetching groups:', error);
    res.status(500).json({ error: 'Failed to fetch groups' });
  }
});

// POST create group
router.post('/groups', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const { name, memberPhoneNumbers } = createGroupSchema.parse(req.body);

    // Create group
    const group = await prisma.group.create({
      data: {
        name,
        ownerId: req.user!.id,
      },
    });

    // Add members if provided
    if (memberPhoneNumbers && memberPhoneNumbers.length > 0) {
      const users = await prisma.user.findMany({
        where: {
          phoneNumber: { in: memberPhoneNumbers },
        },
        select: { id: true },
      });

      const memberData = users.map(user => ({
        userId: user.id,
        groupId: group.id,
      }));

      await prisma.groupMember.createMany({
        data: memberData,
      });
    }

    res.status(201).json({ group });
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: 'Validation failed', details: error.issues });
    }
    console.error('Error creating group:', error);
    res.status(500).json({ error: 'Failed to create group' });
  }
});

// DELETE group
router.delete('/groups/:id', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const id = req.params.id as string;

    // Find group and check ownership
   const group = await prisma.group.findUnique({
     where: { id },
     select: { ownerId: true }
   });

   if (!group) {
     return res.status(404).json({ error: 'Group not found' });
   }

   if (group.ownerId !== req.user!.id) {
     return res.status(403).json({ error: 'Only the owner can delete the group' });
   }

   // Delete related records
   await prisma.groupMember.deleteMany({ where: { groupId: id } });
   await prisma.expense.deleteMany({ where: { groupId: id } });

   // Delete group
   await prisma.group.delete({ where: { id } });

   res.status(200).json({ message: 'Group deleted successfully' });
 } catch (error) {
   console.error('Error deleting group:', error);
   res.status(500).json({ error: 'Failed to delete group' });
 }
});

export default router;
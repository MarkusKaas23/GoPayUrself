import { Router, type Request, type Response } from 'express';
import { z } from 'zod';
import { requireAuth, type AuthRequest } from '../utils/auth.js';
import { prisma } from '../utils/prisma.js';
import { createExpenseSchema } from '../schemas/expenses.js';

const router = Router();

// GET expenses for a group
router.get('/expenses', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const { groupId } = req.query;

    if (!groupId || typeof groupId !== 'string') {
      return res.status(400).json({ error: 'groupId query parameter is required' });
    }

    // Check if user is member of the group
    const memberstatus = await prisma.groupMember.findFirst({
      where: {
        groupId,
        userId: req.user!.id,
      },
    });

    if (!memberstatus) {
      return res.status(403).json({ error: 'You are not a member of this group' });
    }

    const expenses = await prisma.expense.findMany({
      where: { groupId },
      include: {
        payer: { select: { id: true, firstName: true, lastName: true, email: true } },
        group: { select: { id: true, name: true } },
        splits: {
          include: {
            user: { select: { id: true, firstName: true, lastName: true, email: true } },
          },
        },
      },
      orderBy: { date: 'desc' },
    });

    res.status(200).json({ expenses });
  } catch (error) {
    console.error('Error fetching expenses:', error);
    res.status(500).json({ error: 'Failed to fetch expenses' });
  }
});

// POST create expense
router.post('/expenses', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const { amount, groupId, payerId, splits } = createExpenseSchema.parse(req.body);

    // Check if user is member of the group
    const membership = await prisma.groupMember.findFirst({
      where: {
        groupId,
        userId: req.user!.id,
      },
    });

    if (!membership) {
      return res.status(403).json({ error: 'You are not a member of this group' });
    }

    // Check if payer is a member of the group (including owner)
    const group = await prisma.group.findUnique({
      where: { id: groupId },
      select: { ownerId: true },
    });

    if (!group) {
      return res.status(404).json({ error: 'Group not found' });
    }

    const groupMembers = await prisma.groupMember.findMany({
      where: { groupId },
      select: { userId: true },
    });

    const memberIds = groupMembers.map(m => m.userId);
    const allUserIds = [group.ownerId, ...memberIds];

    if (!allUserIds.includes(payerId)) {
      return res.status(400).json({ error: 'Payer is not a member of the group' });
    }

    // Check that all splits userIds are members
    const splitUserIds = splits.map(s => s.userId);
    const invalidUsers = splitUserIds.filter(id => !allUserIds.includes(id));

    if (invalidUsers.length > 0) {
      return res.status(400).json({ error: 'Some users in splits are not members of the group' });
    }

    const expense = await prisma.expense.create({
      data: {
        amount,
        date: new Date(),
        groupId,
        payerId,
        splits: {
          create: splits.map(split => ({
            userId: split.userId,
            amount: split.amount,
          })),
        },
      },
      include: {
        payer: { select: { id: true, firstName: true, lastName: true, email: true } },
        group: { select: { id: true, name: true } },
        splits: {
          include: {
            user: { select: { id: true, firstName: true, lastName: true, email: true } },
          },
        },
      },
    });

    res.status(201).json({ expense });
  } catch (error) {
    if (error instanceof z.ZodError) {
      return res.status(400).json({ error: 'Validation failed', details: error.issues });
    }
    console.error('Error creating expense:', error);
    res.status(500).json({ error: 'Failed to create expense' });
  }
});

// DELETE expense
router.delete('/expenses/:id', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const id = req.params.id as string;

    // Find expense and check ownership
    const expense = await prisma.expense.findUnique({
      where: { id },
      select: { payerId: true, groupId: true }
    });

    if (!expense) {
      return res.status(404).json({ error: 'Expense not found' });
    }

    if (expense.payerId !== req.user!.id) {
      return res.status(403).json({ error: 'Only the payer can delete the expense' });
    }

    await prisma.expense.delete({ where: { id } });

    res.status(200).json({ message: 'Expense deleted successfully' });
  } catch (error) {
    console.error('Error deleting expense:', error);
    res.status(500).json({ error: 'Failed to delete expense' });
  }
});

export default router;
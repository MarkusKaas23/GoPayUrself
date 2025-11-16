import { Router, type Request, type Response } from 'express';
import { z } from 'zod';
import { Car } from '../schemas/car.js';
import type { AuthRequest } from '../utils/auth.js';
import { requireAuth } from '../utils/auth.js';
import { prisma } from '../utils/prisma.js';

const router = Router();

// GET all cars
router.get('/cars', requireAuth, async (_req: Request, res: Response) => {
  try {
    const allCars = await prisma.car.findMany();
    res.status(200).json(allCars);
  } catch (error) {
    console.error('Error fetching cars:', error);
    res.status(500).json({ error: 'Failed to fetch cars' });
  }
});

// GET a specific car by ID
router.get('/cars/:id', requireAuth, async (req: Request, res: Response) => {
  try {
    const { id } = req.params;
    if (!id) {
      return res.status(400).json({ error: 'ID is required' });
    }
    const car = await prisma.car.findUnique({
      where: { id }
    });
    if (!car) {
      return res.status(404).json({ error: 'Car not found' });
    }
    res.status(200).json(car);
  } catch (error) {
    console.error('Error fetching car:', error);
    res.status(500).json({ error: 'Failed to fetch car' });
  }
});

// POST a new car
router.post('/cars', requireAuth, async (req: Request, res: Response) => {
  try {
    const validatedData = Car.parse(req.body);
    const { id, ...createData } = validatedData;
    const newCar = await prisma.car.create({
      data: createData
    });
    res.status(201).json(newCar);
  } catch (error) {
    if (error instanceof z.ZodError) {
      console.error('Validation error:', error.issues);
      return res.status(400).json({ error: 'Validation failed', details: error.issues });
    }
    console.error('Error creating car:', error);
    res.status(500).json({ error: 'Failed to create car' });
  }
});

// Get cars posted by user
router.get('/my-cars', requireAuth, async (req: AuthRequest, res: Response) => {
  try {
    const userCars = await prisma.car.findMany({
      where: { userId: req.user!.id }
    });
    res.status(200).json(userCars);
  } catch (error) {
    console.error('Error fetching user cars:', error);
    res.status(500).json({ error: 'Failed to fetch user cars' });
  }
});

export default router;
import { z } from 'zod';

export const Car = z.object({
  id: z.string().optional(),
  name: z.string(),
  location: z.string(),
  price: z.number(),
  image: z.string(),
  availableFrom: z.coerce.date(),
  availableTo: z.coerce.date(),
  description: z.string(),
  features: z.array(z.string()),
  mileage: z.number().int(),
  fuelType: z.string(),
  transmission: z.string(),
  userId: z.string(),
});
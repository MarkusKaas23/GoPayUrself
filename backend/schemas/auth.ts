import { z } from "zod";

export const signupSchema = z.object({
  firstName: z.string(),
  lastName: z.string(),
  email: z.email(),
  phoneNumber: z.string().regex(/^[+]{1}(?:[0-9\$$/. -]\s?){6,15}[0-9]{1}$/),
  password: z.string().min(6),
});

export const loginSchema = z.object({
  email: z.email(),
  password: z.string().min(6),
});
import { z } from "zod";

export const createExpenseSchema = z.object({
  amount: z.number().positive(),
  groupId: z.string().min(1),
  payerId: z.string().min(1),
  splits: z.array(z.object({
    userId: z.string().min(1),
    amount: z.number().positive(),
  })).min(1),
}).refine((data) => {
  const totalSplit = data.splits.reduce((sum, split) => sum + split.amount, 0);
  return Math.abs(totalSplit - data.amount) < 0.01; // allow small floating point differences
}, {
  message: "The sum of split amounts must equal the total expense amount",
  path: ["splits"],
});
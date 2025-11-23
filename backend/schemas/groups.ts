import { z } from "zod";

export const createGroupSchema = z.object({
  name: z.string().min(1),
  memberPhoneNumbers: z.array(z.string()),
});
ALTER TABLE "ROUTES" ADD COLUMN "UPDATED_AT" timestamp;
UPDATE "ROUTES" SET "UPDATED_AT" = "CREATED_AT" WHERE "UPDATED_AT" IS NULL;
ALTER TABLE "ROUTES" ALTER COLUMN "UPDATED_AT" SET NOT NULL;
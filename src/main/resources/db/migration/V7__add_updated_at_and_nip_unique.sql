ALTER TABLE asn ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM pg_constraint 
        WHERE conname = 'asn_nip_unique'
    ) THEN
        ALTER TABLE asn ADD CONSTRAINT asn_nip_unique UNIQUE (nip);
    END IF;
END $$;

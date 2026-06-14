ALTER TABLE asn ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

-- Function to update updated_at column automatically
CREATE OR REPLACE FUNCTION update_asn_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to execute the function before update
DROP TRIGGER IF EXISTS trg_asn_updated_at ON asn;
CREATE TRIGGER trg_asn_updated_at
BEFORE UPDATE ON asn
FOR EACH ROW
EXECUTE FUNCTION update_asn_updated_at_column();

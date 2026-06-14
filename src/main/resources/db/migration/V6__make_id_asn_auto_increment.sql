CREATE SEQUENCE IF NOT EXISTS asn_id_asn_seq;
ALTER TABLE asn ALTER COLUMN id_asn SET DEFAULT nextval('asn_id_asn_seq');
ALTER SEQUENCE asn_id_asn_seq OWNED BY asn.id_asn;
DO $$
BEGIN
    PERFORM setval('asn_id_asn_seq', COALESCE((SELECT MAX(id_asn) + 1 FROM asn), 1), false);
END $$;

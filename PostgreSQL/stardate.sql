CREATE OR REPLACE
FUNCTION to_stardate(d TIMESTAMP WITH TIME ZONE) RETURNS DOUBLE PRECISION AS $$
DECLARE
  y INTEGER;
  du TIMESTAMP WITH TIME ZONE;
  da TIMESTAMP WITH TIME ZONE;
  db TIMESTAMP WITH TIME ZONE;
BEGIN
  du = d AT TIME ZONE 'UTC';
  y = extract(YEAR FROM du);
  da = to_timestamp(y || '-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS');
  db = to_timestamp((y + 1) || '-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS');
  RETURN y + extract(epoch FROM (du - da)) / extract(epoch FROM (db - da));
END;
$$ LANGUAGE plpgsql STRICT IMMUTABLE SET TIMEZONE = 'UTC';

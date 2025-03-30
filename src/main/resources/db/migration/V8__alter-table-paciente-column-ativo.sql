ALTER TABLE paciente ADD ativo tinyint;
UPDATE paciente SET ativo = 1;
ALTER TABLE paciente MODIFY COLUMN ativo tinyint NOT NULL;

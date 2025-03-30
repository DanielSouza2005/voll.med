ALTER TABLE medico ADD ativo tinyint;
UPDATE medico SET ativo = 1;
ALTER TABLE medico MODIFY COLUMN ativo tinyint NOT NULL;

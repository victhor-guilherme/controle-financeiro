ALTER TABLE conta ADD COLUMN IF NOT EXISTS usuario_id BIGINT REFERENCES usuarios(id);
ALTER TABLE categories ADD COLUMN IF NOT EXISTS usuario_id BIGINT REFERENCES usuarios(id);


ALTER TABLE usuarios ALTER COLUMN email TYPE VARCHAR(255) COLLATE "pg_unicode_fast";
ALTER TABLE conta ALTER COLUMN nome TYPE VARCHAR(255) COLLATE "pg_unicode_fast";
ALTER TABLE categories ALTER COLUMN nome TYPE VARCHAR(255) COLLATE "pg_unicode_fast";

CREATE UNIQUE INDEX IF NOT EXISTS ux_usuarios_email_ci ON usuarios (LOWER(email));
CREATE UNIQUE INDEX IF NOT EXISTS ux_conta_usuario_nome_ci ON conta (usuario_id, LOWER(nome));
CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_usuario_nome_ci ON categories (usuario_id, LOWER(nome));
CREATE INDEX IF NOT EXISTS ix_transacao_conta_data ON transacao (conta_id, data, id);
CREATE INDEX IF NOT EXISTS ix_transacao_categoria ON transacao (categoria_id);

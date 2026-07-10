ALTER TABLE categories
    ADD user_id UUID;

ALTER TABLE categories
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE categories
    ADD CONSTRAINT FK_CATEGORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE categories
    ALTER COLUMN description DROP NOT NULL;
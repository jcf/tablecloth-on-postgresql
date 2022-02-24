CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE people (
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  username TEXT NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

  PRIMARY KEY (id)
);

CREATE TYPE activity_type AS ENUM ('like', 'subscribe');

CREATE TABLE activities (
  src_id UUID NOT NULL,
  dest_id UUID NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  type activity_type NOT NULL,

  PRIMARY KEY (src_id, dest_id, type)
);

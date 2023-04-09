#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER bbq_user WITH PASSWORD 'qwerty';
	ALTER USER bbq_user WITH SUPERUSER;
	CREATE DATABASE db_dining_room_orders;
	GRANT ALL PRIVILEGES ON DATABASE db_dining_room_orders TO bbq_user;
EOSQL
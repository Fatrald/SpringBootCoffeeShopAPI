#!/usr/bin/env bash
# =====================================================================
# Inisialisasi database CoffeeShop.
#
# PENTING: script ini dijalankan otomatis oleh image postgres HANYA SEKALI,
# yaitu saat volume data masih kosong. Jika Anda mengubah script ini,
# reset volume dengan:  docker compose down -v   (SEMUA DATA HILANG)
#
# Hasil akhir:
#   database coffeeshop
#     ├── schema menu          → hanya bisa diakses user menu_service
#     ├── schema ordering      → hanya bisa diakses user order_service
#     ├── schema preparation   → hanya bisa diakses user preparation_service
#     └── schema notification  → hanya bisa diakses user notification_service
#   database keycloak          → hanya bisa diakses user keycloak
# =====================================================================
set -euo pipefail

run_psql() {
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" "$@"
}

# ---------------------------------------------------------------------
# 1. Kunci database coffeeshop: tidak ada yang boleh masuk kecuali diberi izin
# ---------------------------------------------------------------------
run_psql --dbname coffeeshop <<'SQL'
REVOKE ALL ON DATABASE coffeeshop FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
SQL

# ---------------------------------------------------------------------
# 2. Satu user + satu schema untuk setiap service
# ---------------------------------------------------------------------
create_service_schema() {
  local user="$1"
  local password="$2"
  local schema="$3"

  echo ">> Membuat user ${user} dengan schema ${schema}"

  run_psql --dbname coffeeshop \
    -v user="$user" \
    -v password="$password" \
    -v schema="$schema" <<'SQL'
CREATE ROLE :"user" LOGIN PASSWORD :'password';
GRANT CONNECT ON DATABASE coffeeshop TO :"user";
CREATE SCHEMA :"schema" AUTHORIZATION :"user";
ALTER ROLE :"user" SET search_path = :"schema";
SQL
}

create_service_schema menu_service         "$MENU_DB_PASSWORD"         menu
create_service_schema order_service        "$ORDER_DB_PASSWORD"        ordering
create_service_schema preparation_service  "$PREPARATION_DB_PASSWORD"  preparation
create_service_schema notification_service "$NOTIFICATION_DB_PASSWORD" notification

# ---------------------------------------------------------------------
# 3. Database terpisah untuk Keycloak (aplikasi pihak ketiga)
# ---------------------------------------------------------------------
echo ">> Membuat database keycloak"

run_psql --dbname postgres -v password="$KEYCLOAK_DB_PASSWORD" <<'SQL'
CREATE ROLE keycloak LOGIN PASSWORD :'password';
CREATE DATABASE keycloak OWNER keycloak;
REVOKE ALL ON DATABASE keycloak FROM PUBLIC;
SQL

echo ">> Inisialisasi database selesai"

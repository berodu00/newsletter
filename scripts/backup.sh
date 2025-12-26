#!/bin/bash
echo "Starting Backup..."
DATE=$(date +%Y%m%d_%H%M%S)

# DB Backup
pg_dump -h localhost -p 5433 -U postgres kz_magazine > db_backup_$DATE.sql

# File Backup (Adjust path as needed)
# tar -czf uploads_$DATE.tar.gz ./uploads

echo "Backup Complete: db_backup_$DATE.sql"

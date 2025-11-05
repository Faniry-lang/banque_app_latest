dotnet ef dbcontext scaffold "Host=localhost;Database=banque_db;Username=postgres;Password=password123;Port=5432" Npgsql.EntityFrameworkCore.PostgreSQL \
  --output-dir Models \
  --context-dir Data \
  --context AppDbContext \
  --force \
  --use-database-names \
  --no-onconfiguring
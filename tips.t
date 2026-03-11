postgres

# 1. Compiler avec le bon driver
javac -cp "libs\postgresql-42.7.10.jar" -d target src\main\java\com\javastocks\*.java src\main\java\com\javastocks\dao\*.java src\main\java\com\javastocks\model\*.java src\main\java\com\javastocks\view\*.java
powershell

# 2. Exécuter avec le bon driver
java -cp "target;libs\postgresql-42.7.10.jar;." com.javastocks.Main


Start-Service -Name "postgresql-x64-18"     # 1. DEMARRER POS GRESQL 18




BDD CE CONNECTER

Start-Service -Name "postgresql-x64-18"

net start postgresql-x64-18

\c javastocks

& "C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe" start -D "C:\Program Files\PostgreSQL\18\data"


& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -p 5433 -d javastocks
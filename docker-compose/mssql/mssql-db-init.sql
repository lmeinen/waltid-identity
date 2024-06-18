CREATE DATABASE $(DB_NAME);
GO
USE $(DB_NAME);

IF NOT EXISTS (SELECT * FROM sys.sql_logins WHERE name = '$(DB_USERNAME)')
BEGIN
    CREATE LOGIN [$(DB_USERNAME)] WITH PASSWORD = '$(DB_PASSWORD)', CHECK_POLICY = OFF;
    ALTER SERVER ROLE [sysadmin] ADD MEMBER [$(DB_USERNAME)];
END
GO

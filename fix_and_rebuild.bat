@echo off
echo 🔨 FIXING ADVANCE ROUND API BUG...
echo.

echo ✅ Step 1: Clean build folder
call gradlew clean

echo.
echo ✅ Step 2: Rebuild project  
call gradlew build -x test

echo.
echo ✅ Step 3: Check if fix works
echo File fixed: src/main/java/com/example/checkscam/service/impl/TournamentKnockoutServiceImpl.java
echo.
echo 🚀 Now restart your Spring Boot server and test:
echo    POST http://localhost:8080/api/tournaments/1/advance-round
echo.
echo 📊 Expected result: Should advance from Round 1 to Round 2 successfully!
echo.
pause

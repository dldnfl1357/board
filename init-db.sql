-- MySQL 초기화 스크립트
-- UTF-8 설정 및 기본 데이터베이스 구성

-- 데이터베이스 문자셋 확인
SELECT @@character_set_database, @@collation_database;

-- 타임존 설정
SET GLOBAL time_zone = '+09:00';
SET time_zone = '+09:00';

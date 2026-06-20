-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: localhost    Database: mesos_ranking_db
-- ------------------------------------------------------
-- Server version	8.0.45-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `match_history`
--

DROP TABLE IF EXISTS `match_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `match_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(50) NOT NULL,
  `final_score` int NOT NULL,
  `players_number` int NOT NULL,
  `match_date` date DEFAULT (curdate()),
  PRIMARY KEY (`id`),
  KEY `idx_leaderboard` (`players_number`,`final_score` DESC)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `match_history`
--

LOCK TABLES `match_history` WRITE;
/*!40000 ALTER TABLE `match_history` DISABLE KEYS */;
INSERT INTO `match_history` VALUES (1,'Carlo',65,4,'2026-06-02'),(2,'TheBestMesosPlayer',189,5,'2026-06-02'),(3,'TheWorstMesosPlayer',3,5,'2026-06-02'),(4,'TheWorstMesosPlayer',3,4,'2026-06-02'),(5,'TheWorstMesosPlayer',3,3,'2026-06-02'),(6,'TheWorstMesosPlayer',3,2,'2026-06-02'),(7,'TheBestMesosPlayer',121,2,'2026-06-02'),(8,'TheBestMesosPlayer',143,3,'2026-06-02'),(9,'TheBestMesosPlayer',169,4,'2026-06-02'),(10,'WillSmith',43,4,'2026-06-02'),(11,'WillSmith',14,2,'2026-06-02'),(12,'TonyStark',34,2,'2026-06-02'),(13,'TonyStark',35,3,'2026-06-02'),(14,'BigoloGio',21,4,'2026-06-02'),(15,'BigoloGio',11,5,'2026-06-02'),(16,'BigoloGio',9,2,'2026-06-02'),(17,'palla',22,2,'2026-06-02'),(18,'baolo',12,2,'2026-06-02'),(19,'palla',54,2,'2026-06-03'),(20,'mezza',27,2,'2026-06-03'),(21,'mezza',21,2,'2026-06-04'),(22,'palla',20,2,'2026-06-04'),(23,'davide',76,2,'2026-06-08'),(24,'palla',49,2,'2026-06-08'),(25,'palla',28,2,'2026-06-08'),(26,'davide',-21,2,'2026-06-08'),(27,'palla',71,2,'2026-06-08'),(28,'davide',38,2,'2026-06-08'),(29,'mezza',63,2,'2026-06-11'),(30,'palla',23,2,'2026-06-11'),(31,'palla',26,2,'2026-06-11'),(32,'ale',9,2,'2026-06-11'),(33,'palla',38,2,'2026-06-19'),(34,'mezza',4,2,'2026-06-19'),(35,'tommy',35,2,'2026-06-19'),(36,'Frodo',-10,2,'2026-06-19'),(37,'Luke',14,2,'2026-06-19'),(38,'tommy',1,2,'2026-06-19'),(39,'Samuel_Jackson',48,2,'2026-06-19'),(40,'Totoro',0,2,'2026-06-19'),(41,'meza',39,2,'2026-06-19'),(42,'palla',-9,2,'2026-06-19');
/*!40000 ALTER TABLE `match_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-20 12:44:01

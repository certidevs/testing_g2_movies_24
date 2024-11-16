-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: peliculasgrupo2
-- ------------------------------------------------------
-- Server version	8.0.39

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

use g2_movies;
--
-- Table structure for table `categoría`
--

DROP TABLE IF EXISTS `categoría`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoría` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripción` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoría`
--

LOCK TABLES `categoría` WRITE;
/*!40000 ALTER TABLE `categoría` DISABLE KEYS */;
INSERT INTO `categoría` VALUES (1,'Acción','Películas con escenas de acción intensa'),(2,'Ciencia Ficción','Películas basadas en temas de ciencia y tecnología'),(3,'Drama','Películas con elementos dramáticos fuertes'),(4,'Crimen','Películas sobre crimen y justicia'),(5,'Suspenso','Películas que mantienen el suspenso hasta el final');
/*!40000 ALTER TABLE `categoría` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `peliculacategoria`
--

DROP TABLE IF EXISTS `peliculacategoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `peliculacategoria` (
  `pelicula_id` int NOT NULL,
  `categoria_id` int NOT NULL,
  PRIMARY KEY (`pelicula_id`,`categoria_id`),
  KEY `categoria_id` (`categoria_id`),
  CONSTRAINT `peliculacategoria_ibfk_1` FOREIGN KEY (`pelicula_id`) REFERENCES `película` (`id`) ON DELETE CASCADE,
  CONSTRAINT `peliculacategoria_ibfk_2` FOREIGN KEY (`categoria_id`) REFERENCES `categoría` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `peliculacategoria`
--

LOCK TABLES `peliculacategoria` WRITE;
/*!40000 ALTER TABLE `peliculacategoria` DISABLE KEYS */;
INSERT INTO `peliculacategoria` VALUES (1,1),(2,1),(3,1),(1,2),(2,2),(4,3),(5,3),(4,4),(5,4),(5,5);
/*!40000 ALTER TABLE `peliculacategoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `película`
--

DROP TABLE IF EXISTS `película`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `película` (
  `id` int NOT NULL AUTO_INCREMENT,
  `título` varchar(255) NOT NULL,
  `duración` int NOT NULL,
  `año` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `película`
--

LOCK TABLES `película` WRITE;
/*!40000 ALTER TABLE `película` DISABLE KEYS */;
INSERT INTO `película` VALUES (1,'Inception',148,2010),(2,'Matrix',136,1999),(3,'Mad Max: Fury Road',120,2015),(4,'The Godfather',175,1972),(5,'Pulp Fiction',154,1994);
/*!40000 ALTER TABLE `película` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `contraseña` varchar(255) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'juan@example.com','password1','Juan'),(2,'maria@example.com','password2','Maria'),(3,'luis@example.com','password3','Luis'),(4,'ana@example.com','password4','Ana'),(5,'carlos@example.com','password5','Carlos');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valoración`
--

DROP TABLE IF EXISTS `valoración`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `valoración` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario_id` int NOT NULL,
  `pelicula_id` int NOT NULL,
  `comentario` text,
  `puntuación` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `pelicula_id` (`pelicula_id`),
  CONSTRAINT `valoración_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE,
  CONSTRAINT `valoración_ibfk_2` FOREIGN KEY (`pelicula_id`) REFERENCES `película` (`id`) ON DELETE CASCADE,
  CONSTRAINT `valoración_chk_1` CHECK ((`puntuación` between 1 and 10))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valoración`
--

LOCK TABLES `valoración` WRITE;
/*!40000 ALTER TABLE `valoración` DISABLE KEYS */;
INSERT INTO `valoración` VALUES (1,1,1,'Increíble historia y efectos visuales',9),(2,2,1,'Muy original y bien dirigida',8),(3,3,2,'Un clásico del cine moderno',10),(4,1,3,'Acción sin parar',9),(5,2,4,'Una obra maestra',10),(6,4,5,'Excelente narrativa y diálogos',9),(7,3,1,'Me encantó el final',8),(8,5,3,'Intensa y emocionante',7),(9,4,2,'Revolucionaria para su época',9),(10,5,5,'Gran combinación de géneros',8);
/*!40000 ALTER TABLE `valoración` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-10 19:09:04

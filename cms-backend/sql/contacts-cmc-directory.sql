CREATE DATABASE  IF NOT EXISTS `cms_directory`;
USE `cms_directory`;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `contact`;

CREATE TABLE `contact` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
   `phone_number` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

--
-- Data for table `contact`
--


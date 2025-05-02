CREATE DATABASE  IF NOT EXISTS `cms_directory`;
USE `cms_directory`;
--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `contact`;

CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
   `phone_number` varchar(45) DEFAULT NULL,
  `password` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
--
-- Data for table `users`
-- Table structure for table `contact`

CREATE TABLE `contact` (
 
 `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
   `phone_number` varchar(45) DEFAULT NULL,
    `user_id` int NOT NULL,
   foreign key (user_id) references users(id),
    PRIMARY KEY (`id`)
  
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
--
-- Data for table `contact`
--
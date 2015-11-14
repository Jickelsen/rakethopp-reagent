-- phpMyAdmin SQL Dump
-- version 4.1.12
-- http://www.phpmyadmin.net
--
-- Host: localhost:8889
-- Generation Time: Jun 02, 2015 at 07:12 PM
-- Server version: 5.5.34
-- PHP Version: 5.5.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `rakethopp.se`
--

-- --------------------------------------------------------

--
-- Table structure for table `games`
--

CREATE TABLE `games` (
	  `num` int(11) NOT NULL AUTO_INCREMENT,
	  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  `title` text NOT NULL,
	  `title_short` text NOT NULL,
	  `description` text NOT NULL,
	  `url` text NOT NULL,
	  `url_text` text NOT NULL,
	  `num_img` int(11) NOT NULL DEFAULT '0',
	  PRIMARY KEY (`num`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `games`
--

INSERT INTO `games` (`num`, `date`, `title`, `title_short`, `description`, `url`, `url_text`, `num_img`) VALUES
(1, '2014-08-24 11:51:41', 'Jolly Gyro Jaunt', 'jgj', 'A twin-stick puzzle platformer. Honorary mention at Swedish Game Awards 2012.', 'http://gameawards.se/competition_entries/727', 'Swedish Game Awards 2012 Entry', 3),
(2, '2014-08-24 11:56:06', 'Jedi Knighting Ceremony', 'jedi_knighting', 'Predating Surgeon Simulator in using awkward controls for physical humor, this game was made for the No More Sweden 2011 game jam', 'http://www.nomoresweden.com/2011/08/the-games-2/', 'No More Sweden 2012 Entry', 3),
(3, '2014-11-08 20:49:58', 'Spelunca', 'spelunca', 'An artistic platformer where timing and precise aim is everything.', 'http://archive.globalgamejam.org/2012/spelunca', 'Nordic Game Jam 2012 Entry', 2),
(4, '2014-11-08 20:49:58', 'GunFlyer', 'gunflyer', 'A dual stick cave flyer bullet hell time trial shooter.', 'https://dl.dropbox.com/u/3877194/Portfolio/Games/GunFlyerLP/GunFlyerLP.zip', 'Download (PC)', 3),
(5, '2014-11-09 20:06:50', 'The Rooms', 'rooms', 'A co-op projected AR game played with the Lykta system in a dark room with surround sound.', 'http://youtu.be/D2rPKd2EvCg', 'Video', 6);

-- --------------------------------------------------------

--
-- Table structure for table `projects`
--

CREATE TABLE `projects` (
	  `num` int(11) NOT NULL AUTO_INCREMENT,
	  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  `title` text NOT NULL,
	  `title_short` text NOT NULL,
	  `description` text NOT NULL,
	  `url` text NOT NULL,
	  `url_text` text NOT NULL,
	  `num_img` int(11) NOT NULL DEFAULT '0',
	  PRIMARY KEY (`num`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `projects`
--

INSERT INTO `projects` (`num`, `date`, `title`, `title_short`, `description`, `url`, `url_text`, `num_img`) VALUES
(3, '2014-11-09 20:22:12', 'Lykta', 'lykta', 'A projected AR system using off the shelf components. Created as part of my master thesis for the game The Rooms.', '', '', 3),
(4, '2014-11-09 20:22:12', 'Monki Mirror', 'monki', 'A digital selfie-enabled mirror, created as part of a Vinnova research project in collaboration with Monki.', '', '', 2),
(5, '2014-11-09 22:07:28', 'Bronze Age Binoculars', 'bab', 'An AR tourist binocular station taking visitors back to the bronze age. Developed at the Interactive Institute in collaboration with Vitlycke Museum.', 'https://www.tii.se/projects/bronze-age-binoculars', 'Interactive Institute project page', 3);

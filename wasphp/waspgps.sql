-- phpMyAdmin SQL Dump
-- version 4.4.10
-- http://www.phpmyadmin.net
--
-- Client :  localhost:3306
-- Généré le :  Mar 30 Août 2016 à 14:06
-- Version du serveur :  5.5.42
-- Version de PHP :  7.0.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `waspgps`
--
CREATE DATABASE IF NOT EXISTS `waspgps` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `waspgps`;

-- --------------------------------------------------------

--
-- Structure de la table `t_group`
--

CREATE TABLE `t_group` (
  `idGroup` int(11) NOT NULL,
  `groupName` varchar(100) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `t_group`
--

INSERT INTO `t_group` (`idGroup`, `groupName`) VALUES
(1, 'Admin'),
(2, 'User');

-- --------------------------------------------------------

--
-- Structure de la table `t_run`
--

CREATE TABLE `t_run` (
  `idRun` int(11) NOT NULL,
  `idUser` int(11) NOT NULL,
  `Date` datetime NOT NULL,
  `Seconds` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `t_run`
--

INSERT INTO `t_run` (`idRun`, `idUser`, `Date`, `Seconds`) VALUES
(33, 1, '2016-08-29 15:29:01', 86400);

-- --------------------------------------------------------

--
-- Structure de la table `t_rundata`
--

CREATE TABLE `t_rundata` (
  `idRundata` int(11) NOT NULL,
  `idRun` int(11) NOT NULL,
  `xcoord` double NOT NULL,
  `ycoord` double NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=10404 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `t_rundata`
--

INSERT INTO `t_rundata` (`idRundata`, `idRun`, `xcoord`, `ycoord`, `count`) VALUES
(1, 33, 12.34, 3.5, 1),
(2, 33, 22.34, 3.5, 2),
(3, 33, 22.34, 4.5, 3);

-- --------------------------------------------------------

--
-- Structure de la table `t_runstats`
--

CREATE TABLE `t_runstats` (
  `idRunstats` int(11) NOT NULL,
  `distance` int(11) NOT NULL,
  `speed` double NOT NULL,
  `maxSpeed` double NOT NULL,
  `idRun` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `t_runstats`
--

INSERT INTO `t_runstats` (`idRunstats`, `distance`, `speed`, `maxSpeed`, `idRun`) VALUES
(4, 1183189084, 13694.3180989, 0, 33);

-- --------------------------------------------------------

--
-- Structure de la table `t_user`
--

CREATE TABLE `t_user` (
  `idUser` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `group` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Contenu de la table `t_user`
--

INSERT INTO `t_user` (`idUser`, `username`, `password`, `group`) VALUES
(1, 'issou', '81fe8bfe87576c3ecb22426f8e57847382917acf', 1);

--
-- Index pour les tables exportées
--

--
-- Index pour la table `t_group`
--
ALTER TABLE `t_group`
  ADD PRIMARY KEY (`idGroup`),
  ADD UNIQUE KEY `groupName` (`groupName`);

--
-- Index pour la table `t_run`
--
ALTER TABLE `t_run`
  ADD PRIMARY KEY (`idRun`),
  ADD KEY `idUser` (`idUser`);

--
-- Index pour la table `t_rundata`
--
ALTER TABLE `t_rundata`
  ADD PRIMARY KEY (`idRundata`),
  ADD KEY `idRun` (`idRun`);

--
-- Index pour la table `t_runstats`
--
ALTER TABLE `t_runstats`
  ADD PRIMARY KEY (`idRunstats`),
  ADD KEY `idRun` (`idRun`);

--
-- Index pour la table `t_user`
--
ALTER TABLE `t_user`
  ADD PRIMARY KEY (`idUser`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `group` (`group`);

--
-- AUTO_INCREMENT pour les tables exportées
--

--
-- AUTO_INCREMENT pour la table `t_group`
--
ALTER TABLE `t_group`
  MODIFY `idGroup` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT pour la table `t_run`
--
ALTER TABLE `t_run`
  MODIFY `idRun` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=34;
--
-- AUTO_INCREMENT pour la table `t_rundata`
--
ALTER TABLE `t_rundata`
  MODIFY `idRundata` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=10404;
--
-- AUTO_INCREMENT pour la table `t_runstats`
--
ALTER TABLE `t_runstats`
  MODIFY `idRunstats` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT pour la table `t_user`
--
ALTER TABLE `t_user`
  MODIFY `idUser` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `t_run`
--
ALTER TABLE `t_run`
  ADD CONSTRAINT `t_run_ibfk_1` FOREIGN KEY (`idUser`) REFERENCES `t_user` (`idUser`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `t_rundata`
--
ALTER TABLE `t_rundata`
  ADD CONSTRAINT `t_rundata_ibfk_1` FOREIGN KEY (`idRun`) REFERENCES `t_run` (`idRun`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `t_runstats`
--
ALTER TABLE `t_runstats`
  ADD CONSTRAINT `t_runstats_ibfk_1` FOREIGN KEY (`idRun`) REFERENCES `t_run` (`idRun`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Contraintes pour la table `t_user`
--
ALTER TABLE `t_user`
  ADD CONSTRAINT `t_user_ibfk_1` FOREIGN KEY (`group`) REFERENCES `t_group` (`idGroup`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

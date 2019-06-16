# ************************************************************
# Sequel Pro SQL dump
# Version 4541

# Dump of table jobs
# ------------------------------------------------------------

DROP TABLE IF EXISTS `jobs`;

CREATE TABLE `jobs` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `sql` text,
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0:停止,1:执行中',
  `createAt` datetime NOT NULL,
  `updateAt` datetime NOT NULL,
  `token` varchar(100) DEFAULT NULL,
  `node` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table nodes
# ------------------------------------------------------------

DROP TABLE IF EXISTS `nodes`;

CREATE TABLE `nodes` (
  `name` varchar(100) NOT NULL DEFAULT '',
  `host` varchar(100) NOT NULL DEFAULT '',
  `port` int(11) NOT NULL DEFAULT '0',
  `role` int(11) NOT NULL DEFAULT '2',
  `createAt` datetime NOT NULL,
  `updateAt` datetime NOT NULL,
  UNIQUE KEY `UNIQUE` (`host`,`port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


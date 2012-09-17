/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50136
Source Host           : localhost:3306
Source Database       : allergyalertsystem

Target Server Type    : MYSQL
Target Server Version : 50136
File Encoding         : 65001

Date: 2010-10-07 22:37:33
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `barcode_product_name_mapping`
-- ----------------------------
DROP TABLE IF EXISTS `barcode_product_name_mapping`;
CREATE TABLE `barcode_product_name_mapping` (
  `barcode` varchar(50) NOT NULL DEFAULT '',
  `product_name` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`barcode`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of barcode_product_name_mapping
-- ----------------------------
INSERT INTO barcode_product_name_mapping VALUES ('12345', 'cake');
INSERT INTO barcode_product_name_mapping VALUES ('078742082776', 'pizza');
INSERT INTO barcode_product_name_mapping VALUES ('821793004972', 'pizza');
INSERT INTO barcode_product_name_mapping VALUES ('4710937336221', 'burger');

-- ----------------------------
-- Table structure for `product_ingredients`
-- ----------------------------
DROP TABLE IF EXISTS `product_ingredients`;
CREATE TABLE `product_ingredients` (
  `product_name` varchar(500) NOT NULL,
  `ingredients` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`product_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of product_ingredients
-- ----------------------------
INSERT INTO product_ingredients VALUES ('pizza', 'egg,chicken');
INSERT INTO product_ingredients VALUES ('cake', 'egg');
INSERT INTO product_ingredients VALUES ('burger', 'chicken,bread');

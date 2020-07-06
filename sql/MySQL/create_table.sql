-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema tutosaurus
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema tutosaurus
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `tutosaurus` DEFAULT CHARACTER SET utf8 ;
USE `tutosaurus` ;

-- -----------------------------------------------------
-- Table `tutosaurus`.`Profile`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tutosaurus`.`Profile` (
  `idProfile` INT NOT NULL,
  `pic` LONGBLOB NULL,
  `name` VARCHAR(45) NULL,
  `surname` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `username` VARCHAR(45) NULL,
  `rating` INT NULL,
  `CV` VARCHAR(45) NULL,
  PRIMARY KEY (`idProfile`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tutosaurus`.`Course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tutosaurus`.`Course` (
  `idCourse` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`idCourse`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tutosaurus`.`teaches`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tutosaurus`.`teaches` (
  `Profile_idProfile` INT NOT NULL,
  `Course_idCourse` INT NOT NULL,
  `rating` INT NULL,
  `total_of_hours` TIME NULL,
  PRIMARY KEY (`Profile_idProfile`, `Course_idCourse`),
  INDEX `fk_Profile_has_Course_Course1_idx` (`Course_idCourse` ASC),
  INDEX `fk_Profile_has_Course_Profile_idx` (`Profile_idProfile` ASC),
  CONSTRAINT `fk_Profile_has_Course_Profile`
    FOREIGN KEY (`Profile_idProfile`)
    REFERENCES `tutosaurus`.`Profile` (`idProfile`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Profile_has_Course_Course1`
    FOREIGN KEY (`Course_idCourse`)
    REFERENCES `tutosaurus`.`Course` (`idCourse`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tutosaurus`.`learns`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tutosaurus`.`learns` (
  `Course_idCourse` INT NOT NULL,
  `Profile_idProfile` INT NOT NULL,
  PRIMARY KEY (`Course_idCourse`, `Profile_idProfile`),
  INDEX `fk_Course_has_Profile_Profile1_idx` (`Profile_idProfile` ASC),
  INDEX `fk_Course_has_Profile_Course1_idx` (`Course_idCourse` ASC),
  CONSTRAINT `fk_Course_has_Profile_Course1`
    FOREIGN KEY (`Course_idCourse`)
    REFERENCES `tutosaurus`.`Course` (`idCourse`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Course_has_Profile_Profile1`
    FOREIGN KEY (`Profile_idProfile`)
    REFERENCES `tutosaurus`.`Profile` (`idProfile`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tutosaurus`.`meeting`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tutosaurus`.`meeting` (
  `Profile_idProfile` INT NULL,
  `Profile_idProfile1` INT NULL,
  `date` DATETIME NULL,
  `location` DECIMAL(9,6) NULL,
  `meeting` INT NOT NULL,
  PRIMARY KEY (`meeting`),
  INDEX `fk_Profile_has_Profile_Profile2_idx` (`Profile_idProfile1` ASC),
  INDEX `fk_Profile_has_Profile_Profile1_idx` (`Profile_idProfile` ASC),
  CONSTRAINT `fk_Profile_has_Profile_Profile1`
    FOREIGN KEY (`Profile_idProfile`)
    REFERENCES `tutosaurus`.`Profile` (`idProfile`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Profile_has_Profile_Profile2`
    FOREIGN KEY (`Profile_idProfile1`)
    REFERENCES `tutosaurus`.`Profile` (`idProfile`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

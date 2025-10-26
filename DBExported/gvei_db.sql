-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 26, 2025 at 05:16 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gvei_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `exchange_offers`
--

CREATE TABLE `exchange_offers` (
  `offer_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `exchange_value` double NOT NULL,
  `subsidy_percent` double NOT NULL,
  `status` enum('Pending','Approved','Rejected') NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `exchange_offers`
--

INSERT INTO `exchange_offers` (`offer_id`, `vehicle_id`, `exchange_value`, `subsidy_percent`, `status`) VALUES
(1, 1, 8000, 20, 'Approved'),
(2, 3, 5000, 15, 'Approved'),
(3, 2, 6000, 18, 'Approved'),
(4, 4, 10000, 25, 'Rejected');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('user','admin') NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `role`) VALUES
(1, 'Alice Johnson', 'alice@example.com', 'alice123', 'user'),
(2, 'Bob Smith', 'bob@example.com', 'bob123', 'user'),
(3, 'Charlie Admin', 'admin@gvei.com', 'admin123', 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

CREATE TABLE `vehicles` (
  `vehicle_id` int(11) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `plate_no` varchar(20) NOT NULL,
  `vehicle_type` enum('Car','Bus','Motorcycle','Truck') NOT NULL,
  `fuel_type` enum('Petrol','Diesel','Hybrid','Electric') NOT NULL,
  `year` int(11) NOT NULL,
  `mileage` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`vehicle_id`, `owner_id`, `plate_no`, `vehicle_type`, `fuel_type`, `year`, `mileage`) VALUES
(1, 1, 'RAB123A', 'Car', 'Petrol', 2015, 120000),
(2, 1, 'RAB456B', 'Motorcycle', 'Petrol', 2018, 45000),
(3, 2, 'RAB789C', 'Truck', 'Diesel', 2012, 200000),
(4, 2, 'RAB101D', 'Car', 'Hybrid', 2022, 30000);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `exchange_offers`
--
ALTER TABLE `exchange_offers`
  ADD PRIMARY KEY (`offer_id`),
  ADD KEY `vehicle_id` (`vehicle_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD PRIMARY KEY (`vehicle_id`),
  ADD UNIQUE KEY `plate_no` (`plate_no`),
  ADD KEY `owner_id` (`owner_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `exchange_offers`
--
ALTER TABLE `exchange_offers`
  MODIFY `offer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `vehicle_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `exchange_offers`
--
ALTER TABLE `exchange_offers`
  ADD CONSTRAINT `exchange_offers_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON DELETE CASCADE;

--
-- Constraints for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD CONSTRAINT `vehicles_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

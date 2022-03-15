# job4j_grabber
[![Build Status](https://app.travis-ci.com/Krasobas/job4j_grabber.svg?branch=main)](https://app.travis-ci.com/Krasobas/job4j_grabber)
[![codecov](https://codecov.io/gh/Krasobas/job4j_grabber/branch/main/graph/badge.svg?token=33XHLP6P78)](https://codecov.io/gh/Krasobas/job4j_grabber)
This project is a job aggregator.

The system starts on schedule. The launch period is specified in the settings file - app.properties.

The program reads all vacancies related to Java and writes them to the database.

The interface is accessed via the REST API.
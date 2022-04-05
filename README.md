# job4j_grabber
[![Build Status](https://app.travis-ci.com/Krasobas/job4j_grabber.svg?branch=master)](https://app.travis-ci.com/Krasobas/job4j_grabber)
[![codecov](https://codecov.io/gh/Krasobas/job4j_grabber/branch/master/graph/badge.svg?token=33XHLP6P78)](https://codecov.io/gh/Krasobas/job4j_grabber)
![GitHub repo size](https://img.shields.io/github/repo-size/Krasobas/job4j_grabber)
![visitor badge](https://visitor-badge.glitch.me/badge?page_id=krasobas.job4j_grabber&left_text=repo%20views)
![GitHub watchers](https://img.shields.io/github/watchers/krasobas/job4j_grabber?style=social)

This project represents a job search aggregator.

---
The system starts on schedule.
The launch period is specified in the settings file - app.properties.

The program reads all vacancies related to Java and writes them to the database.

The interface is accessed via the REST API.

#### Extensions:

1. New sites can be added to the project without changing the code.

2. In the project, you can do parallel parsing of sites.

#### Used technologies:

- JSOUP for parsing
- JDBC for manipulating with DB
- Quartz library for scheduling
- Slf4j for logging
-----
  ![img](./img/img.png)
  ![img](./img/grabber.gif)
# Carshare Mobile API

## Description
This application is an **API REST (backend only)** for a car share application.
Features include an **authentication** system with **two roles**, **user management**, **car trip creation** and **booking** an existing car trip.
This is a demo created in XXX days after work hours for a presentation during my **CDA (Application Developer/Designer) formation** at Greta.

## Prerequisites on your computer
- [Docker](https://www.docker.com/) installed
- [Git](https://git-scm.com/install/) installed


## Tech Stack (clickable links)
[![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/)
[![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/about/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Git](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)](https://git-scm.com/about)
[![GitLab CI](https://img.shields.io/badge/GitLab_CI-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white)](https://about.gitlab.com/)
[![SonarQube](https://img.shields.io/badge/SonarQube_Server-126ED3?style=for-the-badge&logo=sonarqubeserver&logoColor=white)](https://www.sonarsource.com/fr/products/sonarqube/server/)


## Lifecycle command
[!IMPORTANT] TODO : ensure the that compose-prod.yaml is targeted
- `docker-compose up -d` to start the application with **dev profile** `(you need a valid .env file)`
- `docker compose -f compose-prod.yaml up --build` to build and start the application with **production profile** `(you need a valid .env file)`
- `docker-compose down` to **stop** the application
- `docker-compose down -v` to **stop and remove** registered data from the application


## How to run the app
1. Open a terminal and clone the project with this command `https://gitlab.lamy.mobi/LeGall/s4_covoitmobile.git`
2. Create and configure a `.env` file following the template `.example.env`
3. Ensure `Docker` is running on you computer
4. In a terminal use the command `docker compose -f compose-prod.yaml up --build`
5. Wait a little bit, first time your computer will download some data to create the different container before starting the server
6. Open a `web browser`
7. Connect yourself on the database interface at `http://localhost:8888`
    - **login : admin@admin.com**
    - **password : admin**
8. The api is accessible at this url : `http://localhost:8080`
9. [!IMPORTANT] TODO Give the valid routes : Use an api url in your web browser or a dedicated tool like `Postman` or `Insomnia`


## Ports used
The application will expose the following ports :

- **Backend API :** `http://localhost:8080`
- **Database :** Port `5432` (used from docker network)
- **Database administer interface :** `http://localhost:8888`

> **Note :** If you need to update the ports, edit the section `port` in `compose.yaml / compose-prod.yaml` file.


## Database MCD
[!IMPORTANT] TODO include an image of the MCD
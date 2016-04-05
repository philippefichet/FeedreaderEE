# FeedreaderEE

## Installation
Puisqu'il s'agit d'une application JEE 7, il faut un serveur d'application compatible. (glassfish4, wildfly, ...)

Le nom JNDI du datasource est "`java:jboss/datasources/feedreader`" qui peut être à modifier en fonction de la configuration du serveur d'application

### Serveur d'application

#### Wildfly

##### À la main

Téléchargement de [Wildfly](http://wildfly.org/downloads/)

Téléchargement et installation de hsqldb (Base de données par défaut).

Voir le projet [hsqldbWildfly](https://github.com/philippefichet/hsqldbWildfly) pour une installation simplifiée.

Pour les utilisateurs de windows voir [Babun](https://github.com/babun/babun) pour avoir un shell type linux.

Dans le répertoire "`bin`" se trouve un script facilitant l'installation de la datasource.

Le login, mot de passe et l'emplacement de la base de données peuvent être changés.

##### Avec Docker

###### Contruire l'image docker 

`sudo docker build -t "feedreader:10" .`

###### Lancer l'image docker 

`sudo docker run -p 8080:8080 -p 9990:9990 -t "feedreader:10"`

###### Volume

Pour persisté les données : 

`sudo docker run -p 8080:8080 -p 9990:9990 -v /home/feedreader/:/opt/wildfly/.feedreader -t "feedreader:10"`

### Construction de l'application

L'application est construite avec [Gradle](http://gradle.org) et s'intègre facilement dans les IDE supportant Gradle

"`./gradlew installGulpDependencies war`" suffit pour compiler l'application en .war (`build/lib//feedreader-1.0.war`) qu'il faut déployer dans le serveur d'application choisit.

## Configration

"`WEB-INF/shiro.ini`" est le fichier de gestion de la sécurité ( voir [Apache Shiro](http://shiro.apache.org/configuration.html))

L'emplacment peut-être changer lors de la construction de l'application via la propriété "`shiroConfigurationLocations`"

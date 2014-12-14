# FeedreaderEE

## Installation
Puisqu'il s'agit d'une application JEE 7, il faut un serveur d'application compatible. (glassfish4, wildfly, ...)

Le nom JNDI du datasource est "`java:jboss/datasources/feedreader`" qui peut être à modifier en fonction de la configuration du serveur d'application

### Serveur d'application

#### Wildfly
Téléchargement de [Wildfly](http://wildfly.org/downloads/)

Téléchargement et installation de hsqldb (Base de données par défaut).

Voir le projet [hsqldbWildfly](https://github.com/philippefichet/hsqldbWildfly) pour une installation simplifiée.

Pour les utilisateurs de windows voir [Babun](https://github.com/babun/babun) pour avoir un shell type linux.

Dans le répertoire "`bin`" se trouve un script facilitant l'installation de la datasource.

Le login, mot de passe et l'emplacement de la base de données peuvent être changés.

### Construction de l'application

L'application est construite avec [Maven](http://maven.apache.org/) et s'intègre facilement dans les IDE supportant Maven

"`mvn install`" suffit pour compiler l'application en .war (`target/feedreader-1.0.war`) qu'il faut déployer dans le serveur d'application choisit.

## Configration

"`WEB-INF/shiro.ini`" est le fichier de gestion de la sécurité ( voir [Apache Shiro](http://shiro.apache.org/configuration.html))

L'emplacment peut-être changer lors de la construction de l'application via la propriété "`shiroConfigurationLocations`"

## OpenShift

L'application est compatible avec [OpenShift](https://www.openshift.com/). Il suffit alors de créer (ou copier) le fichier "`WEB-INF/shiro.ini`" vers "`~/app-root/data/.feedreader/shiro.ini`" et de le modifier selon vos préférences (utilisateur, mot de passe, ...)





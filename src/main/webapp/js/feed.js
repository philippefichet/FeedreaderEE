// AngularJS
var FeedReader = angular.module('FeedReader', ['ngSanitize']);

FeedReader.filter("feedItemData", function() {
    return function(input) {
        console.log("feedItemData", input);
        if (input == null || input == undefined || isNaN(input)) {
            return "";
        }
        var date = new Date(input);
        var month = date.getMonth();
        if (month < 10) {
            month = "0" + month
        }
        var day = date.getDay();
        if (day < 10) {
            day = "0" + day
        }
        var hours = date.getHours();
        if (hours < 10) {
            hours = "0" + hours
        }
        var minutes = date.getMinutes();
        if (minutes < 10) {
            minutes = "0" + minutes
        }
        return "(" + date.getFullYear() + "-" + month + "-" + day + " " + hours + ":" + minutes + ")";
    }
});

FeedReader.controller('FeedController', function($scope, $http, $window) {
    $scope.error = null;
    $scope.feeds = [];
    $scope.currentFeed = null;
    $scope.feedItemsLoading = false,
    $scope.feedItems = [];
    $scope.feedItemPages = [];
    $scope.feedLoading = false;

    $scope.addForm = function() {
        $scope.feeds.push({
            "id": -1,
            "name": "",
            "url": "",
            "description": "",
            "selected": false,
            "unread": 0,
            "sync": false
        })
    }
    
    /**
     * Formate une date à partir d'un timestamps
     * @param timestamps timestamps à utiliser comme date
     * @returns Date formaté
     */
    $scope.dateformat = function(timestamps) {
        if (timestamps == null) {
            return null;
        }
        var date = new Date(timestamps);
        // 2014-05-12 09:12:48
        var format = date.getFullYear() + "-";
        if (date.getMonth() < 10) {
            format += "0";
        }
        format += date.getMonth() + "-";
        if (date.getDay() < 10) {
            format += "0";
        }
        format += date.getDay();
        return format;
    }

    /**
     * Ajout/Sauvegarde un flux
     * @param objcet feed Flux à ajouter/sauvegarder
     * @param integer index Index du flux dans les flux présent pour mise à jour aprés sauvegarde
     */
    $scope.saveFeed = function(feed, index) {
        feed.sync = true;
        if (feed.id == -1) {
            $http.put($window.webservicesUrl["feed"], feed).success(function(data) {
                $scope.feeds[index] = data
                $scope.feeds[index].sync = false;
                $scope.error = null;
            }).error(function() {
                $scope.feeds[index].sync = false;
                $scope.error = "Erreur dans l'ajout d'un flux \"" + feed.name + "\"";
            });
        } else {
            $http.post($window.webservicesUrl["feed"] + "?id=" + feed.id, feed).success(function(data) {
                $scope.feeds[index] = data
                $scope.feeds[index].sync = false;
                $scope.error = null;
            }).error(function() {
                $scope.feeds[index].sync = false;
                $scope.error = "Erreur dans la mise à jour d'un flux : \"" + feed.name + "\"";
            });
        }
    }

    $scope.removeFeed = function(feed, index) {
        feed.sync = true;
        $http.delete($window.webservicesUrl["feed"] + "?id=" + feed.id).success(function(data) {
            $scope.feeds.splice(index, 1);
            feed.sync = false;
            $scope.error = null;
        }).error(function() {
            feed.sync = false;
            $scope.error = "Erreur dans la suppression du flux \"" + feed.name + "\"";
        });
    }

    /**
     * Change la valeur de lu/non lu
     * @param string url Url à appeler pour marquer lu/non lu un article
     * @param integer $index Index dans la liste des article de la pages afficher a marqué comme lu/non lu
     */
    $scope.setReaded = function(url, $index) {
        $http.post(url).success(function(data) {
            $scope.feedItems[$index].readed = data.readed;
            $scope.feedItems[$index].url.reverseReaded = data.reverseReaded;
        });
        return;
    }

    /**
     * Chargement de la liste des articles d'un flux
     * @param string url Url de récupération de la liste des articles
     * @param object feed Flux selectionné
     * @param integer page Numéro de page des articles a récupérer
     */
    $scope.loadFeedItem = function(feed, page) {
        url = $window.webservicesUrl['feedItem'];
        $scope.feedItemsLoading = true;
        if (feed == undefined) {
            feed = $scope.currentFeed;
        } else {
            $scope.currentFeed = feed;
        }

        for (var i = 0; i < $scope.feeds.length; i++) {
            $scope.feeds[i].selected = ($scope.feeds[i].id == feed.id);
        }

        if (page == undefined) {
            page = 1;
        }
        url = url.replace('{{feed.id}}', feed.id).replace('{{page}}', page);
        $http.get(url).success(function(feedItemInfo) {
            $scope.feedItems = feedItemInfo.feedItems;
            $scope.feedItemPages = [];
            if (page > 1) {
                $scope.feedItemPages.push({"id": page - 1, "label": "<", "selected": false})
            }
            // Page minimal à afficher
            var startPage = Math.max(1, page - 2);
            
            // Page maximal à afficher
            var endPage = Math.min(feedItemInfo.pages, page + 2);
            // Si page une ajout de 2 a la fin si possible
            if (page < 2) {
                endPage = Math.min(feedItemInfo.pages, page + 4);
            // Si page deux ajout de 1 a la fin si possible
            } else if (page < 3) {
                endPage = Math.min(feedItemInfo.pages, page + 3);
            }
            
            for (var i = startPage; i <= endPage; i++) {
                $scope.feedItemPages.push({"id": i, "label": i, "selected": i == page})
            }
            if (page < feedItemInfo.pages) {
                $scope.feedItemPages.push({"id": page + 1, "label": ">", "selected": false})
            }
            $scope.feedItemsLoading = false;
            window.location.hash = $scope.currentFeed.id + "/" + page;
        })
    }

    $http.get($window.webservicesUrl["feed"]).success(function(feeds) {
        $scope.feeds = feeds;
        for (var i = 0; i < $scope.feeds.length; i++) {
            $scope.feeds[i].selected = false;
        }
        $scope.feedLoading = true;
        // Si il y a du deeplink
        var hash = $window.location.hash;
        if (hash !== "") {
            // Suppression de #
            hash = hash.substr(1, hash.length - 1);
            deepLinking = hash.split("/");
            if (deepLinking.length > 1) {
                for (var i = 0; i < $scope.feeds.length; i++) {
                    if ($scope.feeds[i].id == deepLinking[0])
                    {
                        $scope.loadFeedItem($scope.feeds[i], deepLinking[1]);
                    }
                }
            }
        }
    });

//	alert($window.webkitNotifications.checkPermission());
//	$window.webkitNotifications.createNotification('icon.png', 'Notification', 'Activation des notifications');
//	// Notification
//	if ($window.webkitNotifications) {
//		if ($window.webkitNotifications.checkPermission() == 0) {
//			$window.webkitNotifications.createNotification('icon.png', 'Notification', 'Activation des notifications');
//		} else { 
//			$window.webkitNotifications.requestPermission();
//		}
//	}

    // @TODO Remetre en fonciton les websockets
    $scope.websocketInit = function() {
//        // Create our websocket object with the address to the websocket
//        var ws = new WebSocket("ws://" + $window.location.hostname + ":" + $window.location.port + "/feedreader/live/feed");
//        ws.onopen = function() {
//            console.log("Socket has been opened!");
//        };
//        ws.onclose = function() {
//            $scope.websocketInit();
//        }
//
//        ws.onmessage = function(message) {
//            var data = angular.fromJson(message.data);
//            $scope.$apply(function() {
//                if (data.type == "feeds") {
//                    $scope.feeds = data.feeds;
//                    console.log($scope.currentFeed);
//
//                    // Si il y a un bien un flux séléctionner
//                    if ($scope.currentFeed !== null && $scope.currentFeed !== undefined) {
//                        // Reséléction du flux afficher
//                        for (var i = 0; i < $scope.feeds.length; i++) {
//                            $scope.feeds[i].selected = ($scope.feeds[i].id == $scope.currentFeed.id);
//                        }
//                    }
//
//                    for (var i = 0; i < data.feedItems.length; i++) {
//                        for (var j = 0; j < $scope.feedItems.length; j++) {
//                            // Si les éléments sont trouvé
//                            if ($scope.feedItems[j].id == data.feedItems[i].id) {
//                                $scope.feedItems[j].enclosure = data.feedItems[i].enclosure;
//                                $scope.feedItems[j].feedItemId = data.feedItems[i].feedItemId;
//                                $scope.feedItems[j].link = data.feedItems[i].link;
//                                $scope.feedItems[j].readed = data.feedItems[i].readed;
//                                $scope.feedItems[j].summary = data.feedItems[i].summary;
//                                $scope.feedItems[j].title = data.feedItems[i].title;
//                                $scope.feedItems[j].url = data.feedItems[i].url;
//                                break;
//                            }
//                        }
//                    }
//                } else if (data.type == "feedItems") {
//                    alert("Mise à jours items");
//                    // Notification
////					if ($window.webkitNotifications) {
////						if (window.webkitNotifications.checkPermission() == 0) {
////							window.webkitNotifications.createNotification('', 'Notification', 'Activation des notifications');
////						} else {
////						    window.webkitNotifications.requestPermission();
////						}
////					}
//                }
//            })
//        };
    };

    $scope.websocketInit();
})

// Bootstrap
$(document).ready(function() {
    $(document).on('click', '#feed-one .nav-tabs a', function(e) {
        var self = $(this);
        if (self.hasClass('tab-open')) {
            return true;
        } else {
            e.preventDefault();
            var href = self.attr("href");
            var parent = self.parents('.panel-collapse')
            parent.find('.resume, .link').hide();
            parent.find(".active").removeClass('active')
            $(href).show();
            console.log(href);
            self.parent().addClass('active');
        }
    });

    $(document).on('click', '#feed-one .nav-tabs a.tab-link', function(e) {
        var parent = $(this).parents('.panel-collapse');
        var link = $(this).parents('.panel-collapse').find('.link');
        parent.find(".feeditem-resume").hide();
        var href = link.data('href');
        if (link.find('iframe').length == 0) {
            link.append($('<iframe src="' + href + '"></iframe>'))
        }
    });

    $(document).on('show.bs.collapse', '#feed-one .panel', function() {
        var self = $(this);
        if (self.find('.glyphicon-star-empty').length > 0) {
            var urlReaded = self.data('readed');
            $.ajax({
                "url": urlReaded,
                "type": "POST",
                "dataType": "json"
            })
        }
    });
    
    $(document).on('click', "#feed-list-toggle", function() {
        $("#feed-list").toggleClass("open");
        $("#feed-list-toggle").toggleClass("open");
    })
})

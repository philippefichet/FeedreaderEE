var RoutesManager = {
    routes: {},
    "handleRoute": function(url, pushState) {
        var urlSplit = url.replace(window.baseUrl, "").split("/");
        var routes = Object.keys(this.routes);
        var routeChoosen = null;
        var routeParameters = [];
        for (var i = 0; i < routes.length; i++) {
            var route = routes[i];
            var routeSplit = route.split("/");
            if (routeSplit.length === urlSplit.length) {
                var selected = true;
                var selectedParameters = [];
                for (var j = 0; j < routeSplit.length; j++) {
                    var isParameter = (routeSplit[j].indexOf("{") === 0);
                    if (routeSplit[j] != urlSplit[j] && isParameter === false) {
                        selected = false;
                        break;
                    }
                    if (isParameter) {
                        selectedParameters.push(urlSplit[j]);
                    }
                }
                if (selected) {
                    routeChoosen = route;
                    routeParameters = selectedParameters;
                }
            }
        }

        if (routeChoosen === null) {
            throw Error("No route found");
        }

        if (typeof history.pushState === "undefined") {
            throw Error("Pushstate not available");
        }

        var title = typeof RoutesManager.routes[routeChoosen]["title"] !== "undefined" ? RoutesManager.routes[routeChoosen]["title"] : "";
        var state = {"url": url, "title": title};
        RoutesManager.routes[routeChoosen].load.apply(null, routeParameters);
        if (pushState !== false && document.location.pathname !== url) {
            history.pushState(state, title, url);
        }
    }
};
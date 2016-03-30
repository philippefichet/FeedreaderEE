RoutesManager.routes =  {
    "/": {
        "load": function() {
            require(["Feed/List"], function(FeedList){
                FeedList.build("main-container", window.baseUrl + "/ws/feed", "Visualisation des flux RSS/Atom");
            });
        },
        "title": ""
    },
    "/feed/{id}": {
        "load": function(id) {
            require(["Feed/Item/List"], function(FeedItemList){
                FeedItemList.build("main-container", window.baseUrl + "/ws/feed/" + id + "/item");
            });
        },
        "title": ""
    },
    "/feed/{id}/page/{page}": {
        "load": function(id, page) {
            require(["Feed/Item/List"], function(FeedItemList){
                FeedItemList.build("main-container", window.baseUrl + "/ws/feed/" + id + "/item", page);
            });
        },
        "title": ""
    },
    "/feedItem/{id}": {
        "load": function(id) {
            require(["Feed/Item"], function(FeedItem){
                FeedItem.build("main-container", window.baseUrl + "/ws/feedItem/" + id);
            });
        },
        "title": ""
    }
};
$(document).on("click", "a", function(event) {
    var $this = $(this);
    var href = $this.attr("href");
    RoutesManager["handleRoute"](href);
    return false;
})
$(document).ready(function() {
    RoutesManager["handleRoute"](document.location.pathname);
    window.onpopstate = function(event) {
        var state = event.state;
        console.log(state);
        if (state["url"] !== undefined) {
            RoutesManager["handleRoute"](state["url"], false);
            if (state["title"] !== undefined) {
                document.title = state["title"];
            }
        }
    };
});

$(document).bind("ajaxSend", function(){
    $("body").prepend("<div class=\"ajax-loader\"><div class=\"cssload-shimes\"><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span></div></div>");
}).bind("ajaxComplete", function(){
    $(".ajax-loader").remove();
});


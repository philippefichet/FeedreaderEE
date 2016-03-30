define(["exports", "jquery", "react", "reactDOM", "BS3/Badge"], function (exports, _jquery, _react, _reactDOM, _Badge) {
    "use strict";

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.FeedItemList = undefined;
    exports.build = build;

    var _jquery2 = _interopRequireDefault(_jquery);

    var _react2 = _interopRequireDefault(_react);

    var _reactDOM2 = _interopRequireDefault(_reactDOM);

    var _Badge2 = _interopRequireDefault(_Badge);

    function _interopRequireDefault(obj) {
        return obj && obj.__esModule ? obj : {
            default: obj
        };
    }

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var _createClass = function () {
        function defineProperties(target, props) {
            for (var i = 0; i < props.length; i++) {
                var descriptor = props[i];
                descriptor.enumerable = descriptor.enumerable || false;
                descriptor.configurable = true;
                if ("value" in descriptor) descriptor.writable = true;
                Object.defineProperty(target, descriptor.key, descriptor);
            }
        }

        return function (Constructor, protoProps, staticProps) {
            if (protoProps) defineProperties(Constructor.prototype, protoProps);
            if (staticProps) defineProperties(Constructor, staticProps);
            return Constructor;
        };
    }();

    function _possibleConstructorReturn(self, call) {
        if (!self) {
            throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
        }

        return call && (typeof call === "object" || typeof call === "function") ? call : self;
    }

    function _inherits(subClass, superClass) {
        if (typeof superClass !== "function" && superClass !== null) {
            throw new TypeError("Super expression must either be null or a function, not " + typeof superClass);
        }

        subClass.prototype = Object.create(superClass && superClass.prototype, {
            constructor: {
                value: subClass,
                enumerable: false,
                writable: true,
                configurable: true
            }
        });
        if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass;
    }

    var component = null;

    var FeedItemList = exports.FeedItemList = function (_React$Component) {
        _inherits(FeedItemList, _React$Component);

        function FeedItemList(props) {
            _classCallCheck(this, FeedItemList);

            var _this = _possibleConstructorReturn(this, Object.getPrototypeOf(FeedItemList).call(this, props));

            _this.state = {
                feed: {},
                feedItems: [],
                page: props.page | 0,
                totalPage: 1
            };
            return _this;
        }

        _createClass(FeedItemList, [{
            key: "componentDidMount",
            value: function componentDidMount() {
                this.loadFeedItem(this.state.page);
            }
        }, {
            key: "loadFeedItem",
            value: function loadFeedItem(page) {
                var _this2 = this;

                page = page | 0;
                if (this.state.page === page && this.state.feedItems.length > 0) {
                    return;
                }
                _jquery2.default.ajax({
                    url: this.props.url,
                    dataType: "json",
                    data: {
                        page: page
                    }
                }).success(function (json) {
                    _this2.setState({
                        feedItems: json.feedItems,
                        feed: json.feed,
                        page: page,
                        totalPage: json.pages
                    });
                }).error(function () {});
            }
        }, {
            key: "loadFeedItemPreviousPage",
            value: function loadFeedItemPreviousPage() {
                if (this.state.page > 1) {
                    this.loadFeedItem(this.state.page - 1);
                    history.pushState(this.state, "", window.baseUrl + "/feed/" + this.state.feed.id + "/page/" + (this.state.page - 1));
                }
            }
        }, {
            key: "loadFeedItemNextPage",
            value: function loadFeedItemNextPage() {
                if (this.state.page < this.state.totalPage) {
                    this.loadFeedItem(this.state.page + 1);
                    var url = window.baseUrl + "/feed/" + this.state.feed.id + "/page/" + (this.state.page + 1);
                    history.pushState({ url: url }, "", url);
                }
            }
        }, {
            key: "toogleRead",
            value: function toogleRead(feedItem) {
                var _this3 = this;

                return function (event) {
                    _jquery2.default.ajax({
                        url: feedItem.url.reverseReaded,
                        dataType: "json",
                        method: "POST"
                    }).success(function (json) {
                        // Changement de l'était de l'article
                        _this3.setState({
                            feedItems: _this3.state.feedItems.map(function (feedItemState) {
                                if (feedItemState.id == json.id) {
                                    return json;
                                } else {
                                    return feedItemState;
                                }
                            }),
                            feed: json.feed
                        });
                    });
                };
            }
        }, {
            key: "render",
            value: function render() {
                var _this4 = this;

                var feeds = this.state.feedItems.map(function (feedItem) {
                    return _react2.default.createElement(
                        "div",
                        { key: feedItem.id },
                        _react2.default.createElement(
                            "span",
                            { className: "btn-group-sm" },
                            _react2.default.createElement(
                                "button",
                                { onClick: _this4.toogleRead(feedItem).bind(_this4), className: "btn btn-fab btn-success" },
                                _react2.default.createElement("i", { className: "fa " + (feedItem.readed ? "fa-star-o" : "fa-star") })
                            )
                        ),
                        _react2.default.createElement(
                            "a",
                            { className: "btn btn-raised" + (feedItem.readed ? "" : " btn-info"),
                                href: window.baseUrl + "/feedItem/" + feedItem.id },
                            feedItem.title
                        )
                    );
                });
                return _react2.default.createElement(
                    "div",
                    { className: "feed-item-list" },
                    _react2.default.createElement(
                        "div",
                        { className: "header" },
                        _react2.default.createElement(
                            "div",
                            { className: "title" },
                            _react2.default.createElement(
                                "h1",
                                null,
                                this.state.feed.name === undefined ? "Chargement ..." : this.state.feed.name,
                                " ",
                                _react2.default.createElement(_Badge2.default, { counter: this.state.feed.unread })
                            ),
                            _react2.default.createElement(
                                "div",
                                { className: "return" },
                                _react2.default.createElement(
                                    "a",
                                    { href: window.baseUrl + "/", className: "btn btn-raised btn-info" },
                                    "Retour"
                                )
                            )
                        ),
                        _react2.default.createElement(
                            "div",
                            { className: "pagination" },
                            _react2.default.createElement(
                                "div",
                                { className: "page-info" },
                                this.state.page + " / " + this.state.totalPage
                            ),
                            _react2.default.createElement(
                                "button",
                                { className: "btn btn-raised btn-success previous" + (this.state.page > 1 ? "" : " disabled"), onClick: this.loadFeedItemPreviousPage.bind(this) },
                                "Précédent"
                            ),
                            _react2.default.createElement(
                                "button",
                                { className: "btn btn-raised btn-success next" + (this.state.page < this.state.totalPage ? "" : " disabled"), onClick: this.loadFeedItemNextPage.bind(this) },
                                "Suivant"
                            )
                        )
                    ),
                    feeds
                );
            }
        }]);

        return FeedItemList;
    }(_react2.default.Component);

    function build(id, url) {
        var page = arguments.length <= 2 || arguments[2] === undefined ? 1 : arguments[2];

        component = _reactDOM2.default.render(_react2.default.createElement(FeedItemList, { url: url, page: page }), document.getElementById(id));
        component.loadFeedItem(page);
    }
});
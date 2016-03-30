define(["exports", "jquery", "react", "reactDOM", "BS3/Badge"], function (exports, _jquery, _react, _reactDOM, _Badge) {
    "use strict";

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.FeedItem = undefined;
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

    var FeedItem = exports.FeedItem = function (_React$Component) {
        _inherits(FeedItem, _React$Component);

        function FeedItem(props) {
            _classCallCheck(this, FeedItem);

            var _this = _possibleConstructorReturn(this, Object.getPrototypeOf(FeedItem).call(this, props));

            _this.state = {
                feed: {
                    name: "Chargement ..."
                },
                title: "Chargement ...",
                summary: "Chargement ...",
                url: {
                    reverseReaded: "",
                    toRead: ""
                }
            };
            return _this;
        }

        _createClass(FeedItem, [{
            key: "componentDidMount",
            value: function componentDidMount() {
                var _this2 = this;

                _jquery2.default.ajax({
                    url: this.props.url,
                    dataType: "json"
                }).success(function (json) {
                    _this2.setState(json);
                    _jquery2.default.ajax({
                        url: json.url.toRead,
                        dataType: "json",
                        method: "POST"
                    }).success(function (json) {
                        _this2.setState(json);
                    });
                }).error(function () {});
            }
        }, {
            key: "toogleRead",
            value: function toogleRead() {
                var _this3 = this;

                _jquery2.default.ajax({
                    url: this.state.url.reverseReaded,
                    dataType: "json",
                    method: "POST"
                }).success(function (json) {
                    _this3.setState(json);
                });
            }
        }, {
            key: "render",
            value: function render() {
                return _react2.default.createElement(
                    "div",
                    { className: "feed-item" },
                    _react2.default.createElement(
                        "div",
                        { className: "header" },
                        _react2.default.createElement(
                            "div",
                            { className: "title" },
                            _react2.default.createElement(
                                "h1",
                                null,
                                this.state.feed.name,
                                " ",
                                _react2.default.createElement(_Badge2.default, { counter: this.state.feed.unread })
                            ),
                            _react2.default.createElement(
                                "div",
                                { className: "return" },
                                _react2.default.createElement(
                                    "a",
                                    { href: window.baseUrl + "/feed/" + this.state.feed.id, className: "btn btn-raised btn-info" },
                                    "Retour"
                                ),
                                _react2.default.createElement(
                                    "span",
                                    { className: "btn-group-sm" },
                                    _react2.default.createElement(
                                        "button",
                                        { onClick: this.toogleRead.bind(this), className: "btn btn-fab btn-success" },
                                        _react2.default.createElement("i", { className: "fa " + (this.state.readed ? "fa-star-o" : "fa-star") })
                                    )
                                )
                            )
                        )
                    ),
                    _react2.default.createElement(
                        "div",
                        { className: "panel panel-info" },
                        _react2.default.createElement(
                            "div",
                            { className: "panel-heading" },
                            _react2.default.createElement(
                                "h3",
                                { className: "panel-title" },
                                this.state.title
                            )
                        ),
                        _react2.default.createElement(
                            "div",
                            { className: "panel-body" },
                            _react2.default.createElement(
                                "p",
                                null,
                                _react2.default.createElement(
                                    "a",
                                    { href: this.state.link },
                                    "Lien vers l'article"
                                )
                            ),
                            _react2.default.createElement("p", { dangerouslySetInnerHTML: { __html: this.state.summary } })
                        )
                    )
                );
            }
        }]);

        return FeedItem;
    }(_react2.default.Component);

    function build(id, url) {
        _reactDOM2.default.render(_react2.default.createElement(FeedItem, { url: url }), document.getElementById(id));
    }
});
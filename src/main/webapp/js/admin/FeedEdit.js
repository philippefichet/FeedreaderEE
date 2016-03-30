define(["exports", "jquery", "react", "reactDOM"], function (exports, _jquery, _react, _reactDOM) {
    "use strict";

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.FeedEdit = undefined;
    exports.build = build;

    var _jquery2 = _interopRequireDefault(_jquery);

    var _react2 = _interopRequireDefault(_react);

    var _reactDOM2 = _interopRequireDefault(_reactDOM);

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

    var FeedEdit = exports.FeedEdit = function (_React$Component) {
        _inherits(FeedEdit, _React$Component);

        function FeedEdit(props) {
            _classCallCheck(this, FeedEdit);

            var _this = _possibleConstructorReturn(this, Object.getPrototypeOf(FeedEdit).call(this, props));

            _this.state = {
                feeds: []
            };
            return _this;
        }

        _createClass(FeedEdit, [{
            key: "addFeed",
            value: function addFeed() {
                var _this2 = this,
                    _arguments = arguments;

                var domElement = _reactDOM2.default.findDOMNode(this);
                var value = domElement.getElementsByClassName("feed-edit")[0].getElementsByClassName("form-control");

                var feedAdd = {
                    name: value[0].value,
                    url: value[1].value,
                    description: value[2].value
                };
                _jquery2.default.ajax({
                    url: this.props.url,
                    method: "PUT",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(feedAdd)
                }).success(function (feedAdded) {
                    _this2.state.feeds.push(feedAdded);
                    _this2.setState({ feeds: _this2.state.feeds });
                }).error(function () {
                    console.error(_arguments);
                });
            }
        }, {
            key: "removeFeed",
            value: function removeFeed(feed) {
                var _this3 = this,
                    _arguments2 = arguments;

                return function () {
                    _jquery2.default.ajax({
                        url: _this3.props.url + "?id=" + feed.id,
                        method: "DELETE"
                    }).success(function () {
                        _this3.setState({
                            feeds: _this3.state.feeds.filter(function (f) {
                                return f.id !== feed.id;
                            })
                        });
                    }).error(function () {
                        console.error(_arguments2);
                    });
                };
            }
        }, {
            key: "componentDidMount",
            value: function componentDidMount() {
                var _this4 = this,
                    _arguments3 = arguments;

                _jquery2.default.ajax({
                    url: this.props.url,
                    dataType: "json"
                }).success(function (feeds) {
                    _this4.setState({ feeds: feeds });
                }).error(function () {
                    console.error(_arguments3);
                });
            }
        }, {
            key: "render",
            value: function render() {
                var _this5 = this;

                var feeds = this.state.feeds.map(function (feed) {
                    return _react2.default.createElement(
                        "tr",
                        { key: feed.id },
                        _react2.default.createElement(
                            "td",
                            null,
                            _react2.default.createElement(
                                "a",
                                { href: window.baseUrl + "/feed/" + feed.id },
                                feed.name
                            )
                        ),
                        _react2.default.createElement(
                            "td",
                            null,
                            feed.url
                        ),
                        _react2.default.createElement(
                            "td",
                            null,
                            feed.description
                        ),
                        _react2.default.createElement(
                            "td",
                            null,
                            _react2.default.createElement(
                                "button",
                                { type: "button", className: "btn btn-raised btn-danger", onClick: _this5.removeFeed(feed).bind(_this5) },
                                "Supprimer"
                            )
                        )
                    );
                });
                return _react2.default.createElement(
                    "div",
                    null,
                    _react2.default.createElement(
                        "div",
                        { className: "row feed-edit" },
                        _react2.default.createElement(
                            "div",
                            { className: "col-md-3 form-group" },
                            _react2.default.createElement("input", { type: "text", className: "form-control", placeholder: "Nom" })
                        ),
                        _react2.default.createElement(
                            "div",
                            { className: "col-md-3 form-group" },
                            _react2.default.createElement("input", { type: "text", className: "form-control", placeholder: "Url" })
                        ),
                        _react2.default.createElement(
                            "div",
                            { className: "col-md-3 form-group" },
                            _react2.default.createElement("input", { type: "text", className: "form-control", placeholder: "Description" })
                        ),
                        _react2.default.createElement(
                            "div",
                            { className: "col-md-3 form-group" },
                            _react2.default.createElement(
                                "button",
                                { type: "button", className: "btn btn-raised btn-success", onClick: this.addFeed.bind(this) },
                                "Ajouter"
                            )
                        )
                    ),
                    _react2.default.createElement(
                        "table",
                        { className: "table table-striped" },
                        _react2.default.createElement(
                            "thead",
                            null,
                            _react2.default.createElement(
                                "tr",
                                { className: "alert alert-info" },
                                _react2.default.createElement(
                                    "th",
                                    null,
                                    "Nom"
                                ),
                                _react2.default.createElement(
                                    "th",
                                    null,
                                    "Url"
                                ),
                                _react2.default.createElement(
                                    "th",
                                    null,
                                    "Description"
                                ),
                                _react2.default.createElement(
                                    "th",
                                    null,
                                    "Action"
                                )
                            )
                        ),
                        _react2.default.createElement(
                            "tbody",
                            null,
                            feeds
                        )
                    )
                );
            }
        }]);

        return FeedEdit;
    }(_react2.default.Component);

    function build(id, url) {
        _reactDOM2.default.render(_react2.default.createElement(FeedEdit, { url: url }), document.getElementById(id));
    }
});
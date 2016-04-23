define(["exports", "jquery", "react", "reactDOM", "BS3/Badge"], function (exports, _jquery, _react, _reactDOM, _Badge) {
    "use strict";

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    exports.FeedList = undefined;
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

    var FeedList = exports.FeedList = function (_React$Component) {
        _inherits(FeedList, _React$Component);

        function FeedList(props) {
            _classCallCheck(this, FeedList);

            var _this = _possibleConstructorReturn(this, Object.getPrototypeOf(FeedList).call(this, props));

            _this.state = {
                feeds: []
            };
            return _this;
        }

        _createClass(FeedList, [{
            key: "componentDidMount",
            value: function componentDidMount() {
                var _this2 = this;

                _jquery2.default.ajax({
                    url: this.props.url,
                    dataType: "json"
                }).success(function (feeds) {
                    _this2.setState({ feeds: feeds });
                }).error(function () {});
            }
        }, {
            key: "render",
            value: function render() {
                var feeds = this.state.feeds.map(function (feed) {
                    return _react2.default.createElement(
                        "div",
                        { key: feed.id },
                        _react2.default.createElement(
                            "a",
                            { href: window.baseUrl + "/feed/" + feed.id,
                                className: "btn btn-raised" + (feed.error.length > 0 ? " btn-danger" : feed.unread > 0 ? " btn-success" : ""),
                                title: feed.error
                            },
                            feed.name,
                            " ",
                            _react2.default.createElement(_Badge2.default, { counter: feed.unread })
                        )
                    );
                });
                return _react2.default.createElement(
                    "div",
                    { className: "feed-list" },
                    _react2.default.createElement(
                        "h1",
                        null,
                        this.props.title
                    ),
                    feeds
                );
            }
        }]);

        return FeedList;
    }(_react2.default.Component);

    function build(id, url, title) {
        _reactDOM2.default.render(_react2.default.createElement(FeedList, { url: url, title: title }), document.getElementById(id));
    }
});
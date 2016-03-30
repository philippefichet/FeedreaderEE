import $ from "jquery";
import React from "react";
import ReactDOM from "reactDOM";
import Badge from "BS3/Badge";


export class FeedList extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
          feeds: [],
      };
    }
    componentDidMount() {
        $.ajax({
            url: this.props.url,
            dataType: "json"
        }).success((feeds) => {
            this.setState({feeds: feeds})
        }).error(() => {
            
        });
    }
    render() {
        let feeds = this.state.feeds.map((feed) => {
            return (
                <div key={feed.id}>
                    <a href={window.baseUrl + "/feed/" + feed.id}
                        className={"btn btn-raised" + (feed.unread > 0 ? " btn-success" : "")}
                    >
                        {feed.name} <Badge counter={feed.unread}/>
                    </a>
                </div>
            );
        });
        return (
            <div className="feed-list">
                <h1>{this.props.title}</h1>
                {feeds}
            </div>
        )
    }
}

export function build(id, url, title) {
  ReactDOM.render(
    <FeedList url={url} title={title}/>,
    document.getElementById(id)
  );
}
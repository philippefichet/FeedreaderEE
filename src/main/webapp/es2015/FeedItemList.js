import $ from "jquery";
import React from "react";
import ReactDOM from "reactDOM";
import Badge from "BS3/Badge";

let component = null;

export class FeedItemList extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
          feed: {},
          feedItems: [],
          page: props.page | 0,
          totalPage: 1
      };
    }
    componentDidMount() {
        this.loadFeedItem(this.state.page)
    }
    loadFeedItem(page) {
        page = page | 0
        if (this.state.page === page && this.state.feedItems.length > 0) {
            return;
        }
        $.ajax({
            url: this.props.url,
            dataType: "json",
            data: {
                page: page
            }
        }).success((json) => {
            this.setState({
                feedItems: json.feedItems,
                feed: json.feed,
                page: page,
                totalPage: json.pages
            })
        }).error(() => {
            
        });
    }
    loadFeedItemPreviousPage() {
        if (this.state.page > 1) {
            this.loadFeedItem(this.state.page - 1)
            history.pushState(this.state, "", window.baseUrl + "/feed/" + this.state.feed.id + "/page/" + (this.state.page - 1));
        }
    }
    loadFeedItemNextPage() {
        if (this.state.page < this.state.totalPage) {
            this.loadFeedItem(this.state.page + 1)
            let url = window.baseUrl + "/feed/" + this.state.feed.id + "/page/" + (this.state.page + 1);
            history.pushState({url: url}, "", url);
        }
    }
    toogleRead(feedItem) {
        return (event) => {
            $.ajax({
                url: feedItem.url.reverseReaded,
                dataType: "json",
                method: "POST"
            }).success((json) => {
                // Changement de l'était de l'article
                this.setState({
                    feedItems: this.state.feedItems.map((feedItemState) => {
                        if(feedItemState.id == json.id) {
                            return json;
                        } else {
                            return feedItemState;
                        }
                    }),
                    feed: json.feed
                })
            });
        }
    }
    render() {
        let feeds = this.state.feedItems.map((feedItem) => {
            return (
                <div key={feedItem.id}>
                    <span className="btn-group-sm">
                        <button onClick={this.toogleRead(feedItem).bind(this)} className="btn btn-fab btn-success"><i className={"fa " + (feedItem.readed ? "fa-star-o" : "fa-star")}></i></button>
                    </span>
                    <a className={"btn btn-raised" + (feedItem.readed ? "" : " btn-info")} 
                    href={window.baseUrl + "/feedItem/" + feedItem.id}>
                        {feedItem.title}
                    </a>
                </div>
            );
        });
        return (
            <div className="feed-item-list">
                <div className={"header"}>
                    <div className={"title"}>
                        <h1>{this.state.feed.name === undefined ? "Chargement ..." : this.state.feed.name} <Badge counter={this.state.feed.unread} /></h1>
                        <div className="return">
                            <a href={window.baseUrl + "/"} className="btn btn-raised btn-info">Retour</a>
                        </div>
                    </div>
                    <div className="pagination">
                        <div className="page-info">{this.state.page + " / " + this.state.totalPage}</div>
                        <button className={"btn btn-raised btn-success previous" + ( this.state.page > 1 ? "" : " disabled" )} onClick={this.loadFeedItemPreviousPage.bind(this)}>Précédent</button>
                        <button className={"btn btn-raised btn-success next" + ( this.state.page < this.state.totalPage ? "" : " disabled" )} onClick={this.loadFeedItemNextPage.bind(this)}>Suivant</button>
                    </div>
                </div>
                {feeds}
            </div>
        )
    }
}

export function build(id, url, page = 1) {
  component = ReactDOM.render(
    <FeedItemList url={url} page={page}/>,
    document.getElementById(id)
  );
  component.loadFeedItem(page)
}
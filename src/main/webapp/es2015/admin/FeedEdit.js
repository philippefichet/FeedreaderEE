import $ from "jquery";
import React from "react";
import ReactDOM from "reactDOM";

export class FeedEdit extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
          feeds: [],
      };
    }
    addFeed() {
         let domElement = ReactDOM.findDOMNode(this)
         let value = domElement
             .getElementsByClassName("feed-edit")[0]
             .getElementsByClassName("form-control");
     
         let feedAdd = {
            name: value[0].value,
            url: value[1].value,
            description: value[2].value,
        };
        $.ajax({
            url: this.props.url,
            method: "PUT",
            contentType: "application/json",
            dataType: "json",
            data: JSON.stringify(feedAdd)
        }).success((feedAdded) => {
            this.state.feeds.push(feedAdded);
            this.setState({feeds: this.state.feeds});
        }).error(() => {
            console.error(arguments);
        });
    }
    saveFeed(feed) {
        return () => {
            let domElement = ReactDOM.findDOMNode(this);
            let url = domElement
                .getElementsByClassName("feed-" + feed.id)[0]
                .getElementsByClassName("feed-url")[0].value;
            feed.url = url;
            console.log(feed);
            $.ajax({
                url: this.props.url,
                method: "POST",
                contentType: "application/json",
                dataType:"json",
                data: JSON.stringify(feed)
            }).success((feedUpdated) => {
                this.setState({
                    feeds: this.state.feeds.map((f) => {
                        if (f.id !== feedUpdated.id) {
                            return f;
                        } else {
                            return feedUpdated;
                        }
                    })
                });
            }).error(() => {
                console.error(arguments);
            });
        }
    }
    removeFeed(feed) {
        return () => {
            $.ajax({
                url: this.props.url + "?id=" + feed.id,
                method: "DELETE"
            }).success(() => {
                this.setState({
                    feeds: this.state.feeds.filter((f) => {
                        return f.id !== feed.id;
                    })
                });
            }).error(() => {
                console.error(arguments);
            });
        }
    }
    componentDidMount() {
        $.ajax({
            url: this.props.url,
            dataType: "json"
        }).success((feeds) => {
            this.setState({feeds: feeds})
        }).error(() => {
            console.error(arguments);
        });
    }
    render() {
        let feeds = this.state.feeds.map((feed) => {
            return (
                <tr key={feed.id} id={"feed-" + feed.id} className={"feed-" + feed.id}>
                    <td><a href={window.baseUrl + "/feed/" + feed.id}>{feed.name}</a></td>
                    <td><input type="text" className="feed-url form-control" defaultValue={feed.url}/></td>
                    <td>{feed.description}</td>
                    <td>
                        <button type="button" className="btn btn-raised btn-info" onClick={this.saveFeed(feed).bind(this)}>
                            <i className="material-icons">edit</i>
                        </button>
                        <button type="button" className="btn btn-raised btn-danger" onClick={this.removeFeed(feed).bind(this)}>
                            <i className="material-icons">delete_forever</i>
                        </button>
                    </td>
                </tr>
            );
        });
        return (
            <div>
                <div className="row feed-edit">
                    <div className="col-md-3 form-group">
                        <input type="text" className="form-control" placeholder="Nom"/>
                    </div>
                    <div className="col-md-3 form-group">
                        <input type="text" className="form-control" placeholder="Url"/>
                    </div>
                    <div className="col-md-3 form-group">
                        <input type="text" className="form-control" placeholder="Description"/>
                    </div>
                    <div className="col-md-3 form-group">
                        <button type="button" className="btn btn-raised btn-success" onClick={this.addFeed.bind(this)}>Ajouter</button>
                    </div>
                </div>
                <table className="table table-striped">
                    <thead>
                        <tr className="alert alert-info">
                            <th>Nom</th>
                            <th>Url</th>
                            <th>Description</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {feeds}
                    </tbody>
                </table>
            </div>
        )
    }
}

export function build(id, url) {
  ReactDOM.render(
    <FeedEdit url={url}/>,
    document.getElementById(id)
  );
}
import $ from "jquery";
import React from "react";
import ReactDOM from "reactDOM";
import Badge from "BS3/Badge";


export class FeedItem extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
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
    }
    componentDidMount() {
        $.ajax({
            url: this.props.url,
            dataType: "json"
        }).success((json) => {
            this.setState(json)
            $.ajax({
                url: json.url.toRead,
                dataType: "json",
                method: "POST"
            }).success((json) => {
                this.setState(json)
            });
        }).error(() => {
            
        });
    }
    toogleRead() {
        $.ajax({
            url: this.state.url.reverseReaded,
            dataType: "json",
            method: "POST"
        }).success((json) => {
            this.setState(json)
        });
    }
    render() {
        return (
            <div className="feed-item">
                <div className={"header"}>
                    <div className={"title"}>
                        <h1>{this.state.feed.name} <Badge counter={this.state.feed.unread} /></h1>
                        <div className="return">
                            <a href={window.baseUrl + "/feed/" + this.state.feed.id} className="btn btn-raised btn-info">Retour</a>
                            <span className="btn-group-sm">
                                <button onClick={this.toogleRead.bind(this)} className="btn btn-fab btn-success"><i className={"fa " + (this.state.readed ? "fa-star-o" : "fa-star")}></i></button>
                            </span>
                        </div>
                    </div>
                </div>
                <div className="panel panel-info">
                  <div className="panel-heading">
                    <h3 className="panel-title">{this.state.title}</h3>
                  </div>
                  <div className="panel-body">
                    <p>
                      <a href={this.state.link}>Lien vers l'article</a>
                    </p>
                    <p dangerouslySetInnerHTML={{__html: this.state.summary}}></p>
                  </div>
                </div>
            </div>

        )
    }
}

export function build(id, url) {
  ReactDOM.render(
    <FeedItem url={url}/>,
    document.getElementById(id)
  );
}